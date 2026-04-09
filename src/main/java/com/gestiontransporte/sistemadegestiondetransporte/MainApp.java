package com.gestiontransporte.sistemadegestiondetransporte;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.text.Text;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Line;
import javafx.geometry.Bounds;
import java.util.List;
import java.util.Set;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;
import com.gestiontransporte.sistemadegestiondetransporte.ui.MainController;
import com.gestiontransporte.sistemadegestiondetransporte.persistencia.JsonData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Grafo grafo = new Grafo();
    private MainController controllerRef;
    private SmartGraphPanel<Parada, Ruta> currentPanel = null;
    private com.brunomnsilva.smartgraph.graph.DigraphEdgeList<Parada, Ruta> currentGraph = null;
    // overlay lines used to draw highlighted edges on top of the SmartGraph panel
    private final java.util.List<javafx.scene.Node> overlayEdges = new java.util.ArrayList<>();
    private List<Parada> lastHighlightedPath = null;
    private javafx.scene.layout.Pane overlayGroup = null;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            grafo = JsonData.cargar();
            if (grafo == null) grafo = new Grafo();
        } catch (Exception ex) {
            ex.printStackTrace();
            grafo = new Grafo();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestiontransporte/sistemadegestiondetransporte/MainView.fxml"));
        Parent root = loader.load();
        controllerRef = loader.getController();
        controllerRef.setGrafo(grafo);
        controllerRef.setMainApp(this);
        controllerRef.actualizarCombosParadas();

        Scene scene = new Scene(root, 1000, 700);
        try { String css = getClass().getResource("/com/gestiontransporte/sistemadegestiondetransporte/application.css").toExternalForm(); scene.getStylesheets().add(css); } catch (Exception ignored) {}

        stage.setScene(scene);
        stage.setTitle("Sistema de Gestion - Grafo");
        stage.setMaximized(true);
        stage.show();

        Platform.runLater(this::drawGraph);
    }

    public void redraw() { Platform.runLater(this::drawGraph); }

    private void drawGraph() {
        try {
            AnchorPane container = controllerRef.getGraphContainer();
            if (container == null) return;

            DigraphEdgeList<Parada, Ruta> sg = new DigraphEdgeList<>();
            for (Parada p : grafo.getParadas()) {
                sg.insertVertex(p);
            }
            for (Parada o : grafo.getParadas()) {
                for (Ruta r : grafo.getRutasDesde(o)) {
                    Parada d = r.getDestino();
                    try { sg.insertEdge(o, d, r); } catch (Exception ignore) {}
                }
            }

            SmartCircularSortedPlacementStrategy placement;
            try {
                Class<?> cls = SmartCircularSortedPlacementStrategy.class;
                try {
                    java.lang.reflect.Constructor<?> cons = cls.getConstructor(double.class);
                    placement = (SmartCircularSortedPlacementStrategy) cons.newInstance(320.0);
                } catch (NoSuchMethodException ns) {
                    placement = new SmartCircularSortedPlacementStrategy();
                    try {
                        java.lang.reflect.Method m = cls.getMethod("setRadius", double.class);
                        m.invoke(placement, 320.0);
                    } catch (Exception ignored) {}
                }
            } catch (Exception ex) {
                placement = new SmartCircularSortedPlacementStrategy();
            }

            SmartGraphPanel<Parada, Ruta> panel = new SmartGraphPanel<>(sg, placement);
            currentPanel = panel;
            currentGraph = sg;
            panel.getStyleClass().add("smartgraph-custom");
            // Let SmartGraph handle automatic layout so vertices spread without manual moves
            panel.setAutomaticLayout(true);

            final SmartGraphPanel[] panelRef = new SmartGraphPanel[] { panel };
            controllerRef.setGraphPane(panel);

            Runnable initTask = new Runnable() {
                @Override
                public void run() {
                    try {
                        controllerRef.getGraphContainer().applyCss();
                        controllerRef.getGraphContainer().layout();
                        if (panelRef[0].getWidth() <= 0 || panelRef[0].getHeight() <= 0) { Platform.runLater(this); return; }
                        panelRef[0].init();
                        panelRef[0].update();
                        panelRef[0].updateAndWait();
                    } catch (IllegalStateException ise) {
                        Platform.runLater(this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

            AnchorPane cont = controllerRef.getGraphContainer();
            if (container.getWidth() > 0 && container.getHeight() > 0) initTask.run();
            else {
                container.widthProperty().addListener((obs, o, n) -> { if (container.getWidth() > 0 && container.getHeight() > 0) Platform.runLater(initTask); });
                container.heightProperty().addListener((obs, o, n) -> { if (container.getWidth() > 0 && container.getHeight() > 0) Platform.runLater(initTask); });
            }

            // clear any previous overlays when redrawing
            clearOverlayLines();

            // Ensure we have an overlay pane on top of the graph to draw highlight lines
            try {
                if (overlayGroup == null) {
                    overlayGroup = new javafx.scene.layout.Pane();
                    overlayGroup.setPickOnBounds(false);
                    overlayGroup.setMouseTransparent(true);
                    // anchor to fill container
                    AnchorPane.setTopAnchor(overlayGroup, 0.0);
                    AnchorPane.setBottomAnchor(overlayGroup, 0.0);
                    AnchorPane.setLeftAnchor(overlayGroup, 0.0);
                    AnchorPane.setRightAnchor(overlayGroup, 0.0);
                    // add after panel has been placed
                    if (container != null && !container.getChildren().contains(overlayGroup)) container.getChildren().add(overlayGroup);
                }
                if (overlayGroup != null) overlayGroup.toFront();
            } catch (Exception ignored) {}

        } catch (Exception ex) { ex.printStackTrace(); }
    }






    public void smartAddParada(Parada p) { redraw(); }
    public void smartRemoveParada(Parada p) { redraw(); }
    public void smartAddRuta(Parada o, Parada d) { redraw(); }
    public void smartRemoveRuta(Parada o, Parada d) { redraw(); }

    public void highlightParadas(List<Parada> paradas) {
        if (currentPanel == null || paradas == null || paradas.isEmpty()) return;
        Platform.runLater(() -> {
            try {
                // Access all nodes in the SmartGraphPanel
                Set<Node> allNodes = currentPanel.lookupAll("*");

                // Create a map: parada -> node for quick lookup
                java.util.Map<Parada, Node> paradaToNode = new java.util.HashMap<>();

                for (Node node : allNodes) {
                    String nodeLabel = findLabelTextRecursive(node);
                    if (nodeLabel != null) {
                        // Try to match with each parada
                        for (Parada p : paradas) {
                            if (nodeLabel.contains(p.getNombre())) {
                                paradaToNode.put(p, node);
                                break;
                            }
                        }
                    }
                }

                // Highlight the matched nodes - simple color change
                for (Parada p : paradas) {
                    Node node = paradaToNode.get(p);
                    if (node != null) {
                        // Apply simple highlight: orange fill
                        node.setStyle("-fx-fill: #ff7f0e;");
                    }
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // Remove overlay line nodes from the graph container
    private void clearOverlayLines() {
        try {
            if (overlayEdges.isEmpty()) return;
            if (overlayGroup != null) {
                overlayGroup.getChildren().removeAll(overlayEdges);
            }
            overlayEdges.clear();
        } catch (Exception ignored) {}
    }

    // Draw simple straight lines between consecutive vertices in the given path.
    // This is an overlay on top of SmartGraph visuals and does not modify SmartGraph internals.
    private void drawOverlayForPath(List<Parada> paradas) {
        // Removed - manual overlay lines disabled
    }

    private void clearEdgeHighlights() {
        try {
            if (currentPanel == null) return;
            Set<Node> edges = currentPanel.lookupAll(".edge, .edge-line, .edge-path, polyline, .edge-arrow");
            for (Node n : edges) {
                try { n.getStyleClass().remove("highlighted-edge"); } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
    }

    private void highlightEdgesForPath(List<Parada> paradas) {
        try {
            clearEdgeHighlights();
            if (paradas == null || paradas.size() < 2 || currentPanel == null) return;
            AnchorPane container = controllerRef == null ? null : controllerRef.getGraphContainer();
            if (container == null) return;

            // prepare mapping of vertex labels to center points
            java.util.Map<String, javafx.geometry.Point2D> labelCenter = new java.util.HashMap<>();
            Set<Node> vertices = currentPanel.lookupAll(".vertex, .vertex-visual");
            for (Node v : vertices) {
                String t = findLabelTextRecursive(v);
                if (t == null) continue;
                Bounds b = v.localToScene(v.getBoundsInLocal());
                javafx.geometry.Point2D p = container.sceneToLocal(b.getMinX() + b.getWidth() / 2, b.getMinY() + b.getHeight() / 2);
                labelCenter.put(t, p);
            }

            Set<Node> edgeNodes = currentPanel.lookupAll(".edge, .edge-line, .edge-path, polyline, .edge-arrow");
            for (int i = 0; i < paradas.size() - 1; i++) {
                Parada a = paradas.get(i);
                Parada b = paradas.get(i + 1);
                String ta = a.toString();
                String tb = b.toString();
                javafx.geometry.Point2D pa = labelCenter.get(ta);
                javafx.geometry.Point2D pb = labelCenter.get(tb);
                if (pa == null || pb == null) {
                    // try by name suffix
                    for (java.util.Map.Entry<String, javafx.geometry.Point2D> e : labelCenter.entrySet()) {
                        if (e.getKey().endsWith(a.getNombre())) { pa = e.getValue(); break; }
                    }
                    for (java.util.Map.Entry<String, javafx.geometry.Point2D> e : labelCenter.entrySet()) {
                        if (e.getKey().endsWith(b.getNombre())) { pb = e.getValue(); break; }
                    }
                }
                if (pa == null || pb == null) continue;
                javafx.geometry.Point2D mid = new javafx.geometry.Point2D((pa.getX() + pb.getX()) / 2, (pa.getY() + pb.getY()) / 2);

                for (Node edge : edgeNodes) {
                    try {
                        Bounds eb = edge.localToScene(edge.getBoundsInLocal());
                        javafx.geometry.Point2D em = container.sceneToLocal(eb.getMinX() + eb.getWidth() / 2, eb.getMinY() + eb.getHeight() / 2);
                        double dist = em.distance(mid);
                        if (dist < 60) {
                            try { edge.getStyleClass().add("highlighted-edge"); } catch (Exception ignored) {}
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String findLabelTextRecursive(Node n) {
        if (n == null) return null;
        if (n instanceof Labeled) return ((Labeled) n).getText();
        if (n instanceof Text) return ((Text) n).getText();
        if (n instanceof Parent) {
            for (Node c : ((Parent) n).getChildrenUnmodifiable()) {
                String t = findLabelTextRecursive(c);
                if (t != null) return t;
            }
        }
        return null;
    }

    private void applyTextStyleRecursive(Node n, String style) {
        if (n == null) return;
        if (n instanceof Labeled) { try { ((Labeled) n).setStyle(style); } catch (Exception ignored) {} }
        if (n instanceof Text) { try { ((Text) n).setStyle(style); } catch (Exception ignored) {} }
        if (n instanceof Parent) {
            for (Node c : ((Parent) n).getChildrenUnmodifiable()) applyTextStyleRecursive(c, style);
        }
    }

    private void applyShapeStyleRecursive(Node n, String style) {
        if (n == null) return;
        if (n instanceof Shape) { try { ((Shape) n).setStyle(style); } catch (Exception ignored) {} }
        if (n instanceof Parent) {
            for (Node c : ((Parent) n).getChildrenUnmodifiable()) applyShapeStyleRecursive(c, style);
        }
    }

    private void clearTextStyleRecursive(Node n) {
        if (n == null) return;
        if (n instanceof Labeled) { try { ((Labeled) n).setStyle(""); } catch (Exception ignored) {} }
        if (n instanceof Text) { try { ((Text) n).setStyle(""); } catch (Exception ignored) {} }
        if (n instanceof Parent) {
            for (Node c : ((Parent) n).getChildrenUnmodifiable()) clearTextStyleRecursive(c);
        }
    }

    private void clearShapeStyleRecursive(Node n) {
        if (n == null) return;
        if (n instanceof Shape) { try { ((Shape) n).setStyle(""); } catch (Exception ignored) {} }
        if (n instanceof Parent) {
            for (Node c : ((Parent) n).getChildrenUnmodifiable()) clearShapeStyleRecursive(c);
        }
    }

    public void highlightParadasAlternativa(List<Parada> paradas) {
        if (currentPanel == null || paradas == null || paradas.isEmpty()) return;
        Platform.runLater(() -> {
            try {
                Set<Node> allNodes = currentPanel.lookupAll("*");
                java.util.Map<Parada, Node> paradaToNode = new java.util.HashMap<>();

                for (Node node : allNodes) {
                    String nodeLabel = findLabelTextRecursive(node);
                    if (nodeLabel != null) {
                        for (Parada p : paradas) {
                            if (nodeLabel.contains(p.getNombre())) {
                                paradaToNode.put(p, node);
                                break;
                            }
                        }
                    }
                }

                for (Parada p : paradas) {
                    Node node = paradaToNode.get(p);
                    if (node != null) {
                        node.setStyle("-fx-fill: #7c3aed;");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void main(String[] args) { launch(args); }
}
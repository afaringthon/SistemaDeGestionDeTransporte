package com.gestiontransporte.sistemadegestiondetransporte;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.text.Text;
import javafx.scene.shape.Shape;
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
            panel.getStyleClass().add("smartgraph-custom");
            panel.setAutomaticLayout(false);

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

            if (container.getWidth() > 0 && container.getHeight() > 0) initTask.run();
            else {
                container.widthProperty().addListener((obs, o, n) -> { if (container.getWidth() > 0 && container.getHeight() > 0) Platform.runLater(initTask); });
                container.heightProperty().addListener((obs, o, n) -> { if (container.getWidth() > 0 && container.getHeight() > 0) Platform.runLater(initTask); });
            }

        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void smartAddParada(Parada p) { redraw(); }
    public void smartRemoveParada(Parada p) { redraw(); }
    public void smartAddRuta(Parada o, Parada d) { redraw(); }
    public void smartRemoveRuta(Parada o, Parada d) { redraw(); }

    public void highlightParadas(List<Parada> paradas) {
        if (currentPanel == null) return;
        Platform.runLater(() -> {
            try {
                Set<Node> all = currentPanel.lookupAll(".vertex, .vertex-visual");
                for (Node n : all) {
                    n.getStyleClass().remove("highlighted-vertex");
                    n.setStyle("");
                    clearTextStyleRecursive(n);
                    clearShapeStyleRecursive(n);
                }

                if (paradas == null || paradas.isEmpty()) return;

                Set<Node> candidates = currentPanel.lookupAll(".vertex, .vertex-visual");
                for (Parada p : paradas) {
                    String target = p.toString();
                    for (Node n : candidates) {
                        String found = findLabelTextRecursive(n);
                        if (found != null && found.equals(target)) {
                            try { n.getStyleClass().add("highlighted-vertex"); } catch (Exception ignored) {}
                            try { n.setStyle("-fx-effect: dropshadow(gaussian, rgba(220,50,47,0.9), 10, 0.2, 0, 0);"); } catch (Exception ignored) {}
                            applyTextStyleRecursive(n, "-fx-fill: #d62728; -fx-font-weight: bold;");
                            applyShapeStyleRecursive(n, "-fx-stroke: #d62728; -fx-stroke-width: 2; -fx-fill: white;");
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
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

    public static void main(String[] args) { launch(args); }
}
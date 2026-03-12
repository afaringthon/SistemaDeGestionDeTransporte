package com.gestiontransporte.sistemadegestiondetransporte;

import com.gestiontransporte.sistemadegestiondetransporte.algoritmos.Dijkstra;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;
import com.gestiontransporte.sistemadegestiondetransporte.persistencia.JsonData;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.*;

public class MainApp extends Application {

    Grafo grafo = new Grafo(); // se guardan las paradas y rutas
    private Pane graphPane; //Panel donde se dibuja visualmente el grafo

    // Combos globales
    private ComboBox<Parada> comboOrigen;
    private ComboBox<Parada> comboDestino;
    private ComboBox<Parada> comboRutaOrigen;
    private ComboBox<Parada> comboRutaDestino;
    private ComboBox<Parada> comboEliminarParada;

    @Override
    public void start(Stage stage) {
        try{
            grafo = JsonData.cargar(); // carga el archivo
        }catch (IOException e){
            grafo = new Grafo();
        }

        graphPane = new Pane();
        graphPane.setPrefSize(600, 400);

        comboOrigen = new ComboBox<>();
        comboDestino = new ComboBox<>();
        comboOrigen.setPromptText("Origen");
        comboDestino.setPromptText("Destino");

        actualizarCombosParadas(); //lena los campos con las paradas actuales

        Button btnBuscarCamino = new Button("Camino más rápido (tiempo)");
        btnBuscarCamino.setOnAction(e -> {
            Parada origen = comboOrigen.getValue();
            Parada destino = comboDestino.getValue();
            if (origen == null || destino == null) {
                System.out.println("Debes elegir origen y destino");
                return;
            }
            Dijkstra.Resultado resultado = Dijkstra.calcular(grafo, origen.getId(), Dijkstra.Criterio.TIEMPO);
            var path = resultado.reconstruirCamino(grafo, origen.getId(), destino.getId());

            drawGraph(grafo, graphPane, path);
            //var path es parecido a un List<Parada> path, pero por comodidad usamos path
        });

        HBox barraCamino = new HBox(10, comboOrigen, comboDestino, btnBuscarCamino);

        // ====== Controles Paradas: agregar y eliminar ======
        TextField txtIdParada = new TextField();
        txtIdParada.setPromptText("ID parada");

        TextField txtNombreParada = new TextField();
        txtNombreParada.setPromptText("Nombre parada");

        Button btnAgregarParada = new Button("Agregar Parada");
        btnAgregarParada.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtIdParada.getText());
                String nombre = txtNombreParada.getText();
                if (nombre.isEmpty()) {
                    System.out.println("El nombre no puede estar vacío");
                    return;
                }
                Parada nueva = new Parada(id, nombre);
                grafo.agregarParada(nueva);
                actualizarCombosParadas();
                comboEliminarParada.getItems().setAll(grafo.getParadas());
                drawGraph(grafo, graphPane, null);
                txtIdParada.clear();
                txtNombreParada.clear();
            } catch (NumberFormatException ex) {
                System.out.println("ID debe ser un numero");
            }
        });

        comboEliminarParada = new ComboBox<>();
        comboEliminarParada.setPromptText("Parada a eliminar");
        comboEliminarParada.getItems().addAll(grafo.getParadas());

        Button btnEliminarParada = new Button("Eliminar Parada");
        btnEliminarParada.setOnAction(e -> {
            Parada seleccionada = comboEliminarParada.getValue();
            if (seleccionada == null) {
                System.out.println("Elige una parada para eliminar");
                return;
            }

            grafo.eliminarParada(seleccionada.getId());

            actualizarCombosParadas();
            comboEliminarParada.getItems().setAll(grafo.getParadas());
            drawGraph(grafo, graphPane, null);
        });


        //organiza visualmente los controles de paradas
        VBox panelParadas = new VBox(5,
                new Text("Paradas"),
                txtIdParada, txtNombreParada, btnAgregarParada,
                comboEliminarParada, btnEliminarParada
        );

        //
        comboRutaOrigen = new ComboBox<>();
        comboRutaDestino = new ComboBox<>();
        comboRutaOrigen.setPromptText("Origen ruta");
        comboRutaDestino.setPromptText("Destino ruta");
        comboRutaOrigen.getItems().addAll(grafo.getParadas());
        comboRutaDestino.getItems().addAll(grafo.getParadas());

        TextField txtDistancia = new TextField();
        txtDistancia.setPromptText("Distancia");
        TextField txtTiempo = new TextField();
        txtTiempo.setPromptText("Tiempo");

        Button btnAgregarRuta = new Button("Agregar Ruta (ida y vuelta)");
        btnAgregarRuta.setOnAction(e -> {
            Parada o = comboRutaOrigen.getValue();
            Parada d = comboRutaDestino.getValue();
            if (o == null || d == null || o.equals(d)) {
                System.out.println("Debes elegir origen y destino distintos para la ruta");
                return;
            }
            try {
                double dist = Double.parseDouble(txtDistancia.getText());
                double tiem = Double.parseDouble(txtTiempo.getText());

                // crear ruta ida y vuelta
                grafo.crearRutaDoble(o,d,dist,tiem);

                drawGraph(grafo, graphPane, null);
                txtDistancia.clear();
                txtTiempo.clear();
            } catch (NumberFormatException ex) {
                System.out.println("Distancia y tiempo deben ser números");
            }
        });

        Button btnEliminarRuta = new Button("Eliminar Ruta (ida y vuelta)");
        btnEliminarRuta.setOnAction(e -> {
            Parada o = comboRutaOrigen.getValue();
            Parada d = comboRutaDestino.getValue();
            if (o == null || d == null || o.equals(d)) {
                System.out.println("Debes elegir origen y destino para eliminar la ruta");
                return;
            }
            grafo.eliminarRutaDoble(o, d);
            drawGraph(grafo, graphPane, null);

        });

        VBox panelRutas = new VBox(5,
                new Text("Rutas"),
                comboRutaOrigen, comboRutaDestino,
                txtDistancia, txtTiempo,
                btnAgregarRuta,
                btnEliminarRuta
        );

        HBox panelControl = new HBox(20, panelParadas, panelRutas);
        VBox root = new VBox(10, barraCamino, panelControl, graphPane);

        drawGraph(grafo, graphPane, null);

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Gestión y Visualización de Grafo");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            try {
                JsonData.guardar(grafo);
                System.out.println("Grafo guardado al cerrar la aplicación.");
            } catch (IOException e) {
                System.out.println("Error al guardar grafo al cerrar: " + e.getMessage());
            }
        });
        stage.show();
    }


    // Actualiza todos los ComboBox de paradas
    private void actualizarCombosParadas() {
        if (comboOrigen != null) {
            comboOrigen.getItems().setAll(grafo.getParadas());
        }
        if (comboDestino != null) {
            comboDestino.getItems().setAll(grafo.getParadas());
        }
        if (comboRutaOrigen != null) {
            comboRutaOrigen.getItems().setAll(grafo.getParadas());
        }
        if (comboRutaDestino != null) {
            comboRutaDestino.getItems().setAll(grafo.getParadas());
        }
    }

    private void drawGraph(Grafo grafo, Pane graphPane, List<Parada> shortestPath) {
        graphPane.getChildren().clear();
        //borrar dibujo anterior

        int n = grafo.getParadas().size();
        double centerX = 300, centerY = 200, radius = 150;
        Map<Integer, double[]> positions = new HashMap<>();
        //prepara posiciones


        // Posicionar nodos en circulo
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = centerX + Math.cos(angle) * radius;
            double y = centerY + Math.sin(angle) * radius;
            Parada p = grafo.getParadas().get(i);
            positions.put(p.getId(), new double[]{x, y});
        }

        // Dibujar rutas y su peso
        for (Ruta r : grafo.getTodasLasRutas()) {
            int origenId = r.getOrigen().getId();
            int destinoId = r.getDestino().getId();
            double[] origPos = positions.get(origenId);
            double[] destPos = positions.get(destinoId);

            Line edge = new Line(origPos[0], origPos[1], destPos[0], destPos[1]);
            edge.setStrokeWidth(2);
            edge.setStroke(Color.GRAY);
            graphPane.getChildren().add(edge);

            double midX = (origPos[0] + destPos[0]) / 2;
            double midY = (origPos[1] + destPos[1]) / 2;
            String weightLabel = r.getTiempo() + "";
            Text edgeLabel = new Text(midX, midY, weightLabel);
            edgeLabel.setFill(Color.DODGERBLUE);
            graphPane.getChildren().add(edgeLabel);
        }

        // Dibujar nodos y resaltar
        for (Parada p : grafo.getParadas()) {
            double[] pos = positions.get(p.getId());

            boolean inPath = false;
            if (shortestPath != null) {
                for (Parada sp : shortestPath) {
                    if (sp.getId() == p.getId()) {
                        inPath = true;
                        break;
                    }
                }
            }

            Circle node = new Circle(pos[0], pos[1], 20);
            node.setFill(inPath ? Color.ORANGE : Color.LIGHTBLUE);
            graphPane.getChildren().add(node);

            Text label = new Text(pos[0] - 10, pos[1] + 5, p.getNombre());
            label.setFill(Color.BLACK);
            graphPane.getChildren().add(label);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
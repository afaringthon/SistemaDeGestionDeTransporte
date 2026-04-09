package com.gestiontransporte.sistemadegestiondetransporte.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.gestiontransporte.sistemadegestiondetransporte.MainApp;
import com.gestiontransporte.sistemadegestiondetransporte.algoritmos.*;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.List;

public class MainController {

	@FXML public AnchorPane graphContainer;
	@FXML public AnchorPane graphContainer2;
	@FXML public Label statusLabel;

	@FXML private ComboBox<Parada> comboCalcOrigen;
	@FXML private ComboBox<Parada> comboCalcDestino;
	@FXML private ComboBox<String> comboCriterio;
	@FXML private ComboBox<String> comboAlgoritmo;
	@FXML private ComboBox<String> comboVehiculo;
	@FXML private Label labelRutaPrincipal;
	@FXML private Label labelValorPrincipal;
	@FXML private Label labelRutaAlternativa;
	@FXML private Label labelValorAlternativo;

	private Grafo grafo;
	private MainApp mainApp;

	public void setGraphPane(Pane pane) {
		if (graphContainer == null) return;
		graphContainer.getChildren().setAll(pane);
		AnchorPane.setTopAnchor(pane, 0.0);
		AnchorPane.setBottomAnchor(pane, 0.0);
		AnchorPane.setLeftAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);
	}

	public AnchorPane getGraphContainer() { return graphContainer; }
	public void setGrafo(Grafo grafo) { this.grafo = grafo; }
	public void setMainApp(MainApp mainApp) { this.mainApp = mainApp; }

	public void setStatus(String text) {
		if (statusLabel != null) statusLabel.setText(text);
	}

	private void showError(String message) {
		setStatus("Error: " + message);
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.showAndWait();
		});
	}

	private void showInfo(String message) {
		setStatus(message);
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Información");
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.showAndWait();
		});
	}

	@FXML
	private void initialize() {
		try {
			if (comboCriterio != null) comboCriterio.getItems().addAll("TIEMPO", "DISTANCIA", "COSTO");
			if (comboAlgoritmo != null) comboAlgoritmo.getItems().addAll("DIJKSTRA", "BELLMAN-FORD", "BFS");
			if (comboVehiculo != null) comboVehiculo.getItems().addAll("Carro", "Moto", "A Pie");
		} catch (Exception ignore) {}
	}

	public void actualizarCombosParadas() {
		if (grafo == null) return;
		if (comboCalcOrigen != null) comboCalcOrigen.getItems().setAll(grafo.getParadas());
		if (comboCalcDestino != null) comboCalcDestino.getItems().setAll(grafo.getParadas());
	}

	private Stage crearPopup(String titulo, int ancho, int alto) {
		Stage popup = new Stage();
		popup.setTitle(titulo);
		popup.initModality(Modality.APPLICATION_MODAL);
		popup.setResizable(false);
		return popup;
	}

	@FXML
	private void onAgregarParada(ActionEvent event) {
		Stage popup = crearPopup("Agregar Parada", 300, 150);
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));
		Label label = new Label("Nombre de la parada:");
		TextField txtNombre = new TextField();
		txtNombre.setPromptText("Nombre");
		Button btnConfirmar = new Button("Agregar");
		btnConfirmar.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white;");
		btnConfirmar.setMaxWidth(Double.MAX_VALUE);
		btnConfirmar.setOnAction(e -> {
			String nombre = txtNombre.getText().trim();
			if (nombre.isEmpty()) { showError("Ingrese un nombre válido"); return; }
			Parada p = new Parada(nombre);
			boolean ok = grafo.agregarParada(p);
			if (ok) {
				showInfo("Parada agregada: " + p.getNombre());
				actualizarCombosParadas();
				if (mainApp != null) mainApp.smartAddParada(p);
				popup.close();
			} else {
				showError("Ya existe una parada con el nombre: " + nombre);
			}
		});
		layout.getChildren().addAll(label, txtNombre, btnConfirmar);
		popup.setScene(new Scene(layout, 300, 150));
		popup.showAndWait();
	}

	@FXML
	private void onModificarParada(ActionEvent event) {
		Stage popup = crearPopup("Modificar Parada", 300, 220);
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));
		Label label1 = new Label("Selecciona la parada:");
		ComboBox<Parada> combo = new ComboBox<>();
		combo.getItems().setAll(grafo.getParadas());
		combo.setMaxWidth(Double.MAX_VALUE);
		Label label2 = new Label("Nuevo nombre:");
		TextField txtNombre = new TextField();
		txtNombre.setPromptText("Nuevo nombre");
		Button btnConfirmar = new Button("Modificar");
		btnConfirmar.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white;");
		btnConfirmar.setMaxWidth(Double.MAX_VALUE);
		btnConfirmar.setOnAction(e -> {
			Parada seleccionada = combo.getValue();
			String nuevoNombre = txtNombre.getText().trim();
			if (seleccionada == null) { showError("Selecciona una parada"); return; }
			if (nuevoNombre.isEmpty()) { showError("Ingrese un nombre válido"); return; }
			boolean ok = grafo.modificarParada(seleccionada.getId(), new Parada(nuevoNombre));
			if (ok) {
				showInfo("Parada modificada a: " + nuevoNombre);
				actualizarCombosParadas();
				if (mainApp != null) mainApp.redraw();
				popup.close();
			} else {
				showError("No se pudo modificar la parada");
			}
		});
		layout.getChildren().addAll(label1, combo, label2, txtNombre, btnConfirmar);
		popup.setScene(new Scene(layout, 300, 220));
		popup.showAndWait();
	}

	@FXML
	private void onRemoverParada(ActionEvent event) {
		Stage popup = crearPopup("Eliminar Parada", 300, 160);
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));
		Label label = new Label("Selecciona la parada a eliminar:");
		ComboBox<Parada> combo = new ComboBox<>();
		combo.getItems().setAll(grafo.getParadas());
		combo.setMaxWidth(Double.MAX_VALUE);
		Button btnConfirmar = new Button("Eliminar");
		btnConfirmar.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white;");
		btnConfirmar.setMaxWidth(Double.MAX_VALUE);
		btnConfirmar.setOnAction(e -> {
			Parada seleccionada = combo.getValue();
			if (seleccionada == null) { showError("Selecciona una parada"); return; }
			boolean ok = grafo.eliminarParada(seleccionada);
			if (ok) {
				showInfo("Parada eliminada: " + seleccionada.getNombre());
				actualizarCombosParadas();
				if (mainApp != null) mainApp.redraw();
				popup.close();
			} else {
				showError("No se pudo eliminar la parada");
			}
		});
		layout.getChildren().addAll(label, combo, btnConfirmar);
		popup.setScene(new Scene(layout, 300, 160));
		popup.showAndWait();
	}

	@FXML
	private void onAgregarRuta(ActionEvent event) {
		Stage popup = crearPopup("Agregar Ruta", 320, 400);
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));
		ComboBox<Parada> comboOrigen = new ComboBox<>();
		comboOrigen.getItems().setAll(grafo.getParadas());
		comboOrigen.setPromptText("Origen");
		comboOrigen.setMaxWidth(Double.MAX_VALUE);
		ComboBox<Parada> comboDestino = new ComboBox<>();
		comboDestino.getItems().setAll(grafo.getParadas());
		comboDestino.setPromptText("Destino");
		comboDestino.setMaxWidth(Double.MAX_VALUE);
		TextField txtDistancia = new TextField();
		txtDistancia.setPromptText("Distancia (km)");
		TextField txtTiempo = new TextField();
		txtTiempo.setPromptText("Tiempo (min)");
		TextField txtCosto = new TextField();
		txtCosto.setPromptText("Costo");
		Button btnConfirmar = new Button("Agregar");
		btnConfirmar.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white;");
		btnConfirmar.setMaxWidth(Double.MAX_VALUE);
		btnConfirmar.setOnAction(e -> {
			Parada o = comboOrigen.getValue();
			Parada d = comboDestino.getValue();
			if (o == null || d == null) { showError("Selecciona origen y destino"); return; }
			if (o.equals(d)) { showError("Origen y destino no pueden ser iguales"); return; }
			try {
				double dist = Double.parseDouble(txtDistancia.getText().trim());
				double tiem = Double.parseDouble(txtTiempo.getText().trim());
				double costo = Double.parseDouble(txtCosto.getText().trim());
				if (dist <= 0) { showError("La distancia debe ser mayor a 0"); return; }
				if (tiem <= 0) { showError("El tiempo debe ser mayor a 0"); return; }
				Ruta nuevaRuta = new Ruta(o, d, dist, tiem, costo);
				boolean ok = grafo.agregarRuta(nuevaRuta);
				if (ok) {
					showInfo("Ruta agregada entre " + o.getNombre() + " y " + d.getNombre());
					actualizarCombosParadas();
					if (mainApp != null) mainApp.smartAddRuta(o, d);
					popup.close();
				} else {
					showError("Ya existe una ruta entre " + o.getNombre() + " y " + d.getNombre());
				}
			} catch (NumberFormatException ex) {
				showError("Ingresa valores numéricos válidos");
			}
		});
		layout.getChildren().addAll(
				new Label("Origen:"), comboOrigen,
				new Label("Destino:"), comboDestino,
				new Label("Distancia:"), txtDistancia,
				new Label("Tiempo:"), txtTiempo,
				new Label("Costo:"), txtCosto,
				btnConfirmar
		);
		popup.setScene(new Scene(layout, 320, 400));
		popup.showAndWait();
	}

	@FXML
	private void onEditarRuta(ActionEvent event) {
		Stage popup = crearPopup("Modificar Ruta", 320, 420);
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));
		Label label = new Label("Selecciona la ruta a modificar:");
		ComboBox<Ruta> comboRutas = new ComboBox<>();
		comboRutas.getItems().setAll(grafo.getTodasLasRutas());
		comboRutas.setMaxWidth(Double.MAX_VALUE);
		TextField txtDistancia = new TextField();
		txtDistancia.setPromptText("Nueva distancia (km)");
		TextField txtTiempo = new TextField();
		txtTiempo.setPromptText("Nuevo tiempo (min)");
		TextField txtCosto = new TextField();
		txtCosto.setPromptText("Nuevo costo");
		Button btnConfirmar = new Button("Modificar");
		btnConfirmar.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white;");
		btnConfirmar.setMaxWidth(Double.MAX_VALUE);
		btnConfirmar.setOnAction(e -> {
			Ruta seleccionada = comboRutas.getValue();
			if (seleccionada == null) { showError("Selecciona una ruta"); return; }
			try {
				double dist = Double.parseDouble(txtDistancia.getText().trim());
				double tiem = Double.parseDouble(txtTiempo.getText().trim());
				double costo = Double.parseDouble(txtCosto.getText().trim());
				if (dist <= 0) { showError("La distancia debe ser mayor a 0"); return; }
				if (tiem <= 0) { showError("El tiempo debe ser mayor a 0"); return; }
				Ruta nuevaRuta = new Ruta(seleccionada.getOrigen(), seleccionada.getDestino(), dist, tiem, costo);
				boolean ok = grafo.modificarRuta(seleccionada, nuevaRuta);
				if (ok) {
					showInfo("Ruta modificada correctamente");
					actualizarCombosParadas();
					if (mainApp != null) mainApp.redraw();
					popup.close();
				} else {
					showError("No se pudo modificar la ruta");
				}
			} catch (NumberFormatException ex) {
				showError("Ingresa valores numéricos válidos");
			}
		});
		layout.getChildren().addAll(label, comboRutas,
				new Label("Distancia:"), txtDistancia,
				new Label("Tiempo:"), txtTiempo,
				new Label("Costo:"), txtCosto,
				btnConfirmar
		);
		popup.setScene(new Scene(layout, 320, 420));
		popup.showAndWait();
	}

	@FXML
	private void onRemoverRuta(ActionEvent event) {
		Stage popup = crearPopup("Eliminar Ruta", 300, 160);
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));
		Label label = new Label("Selecciona la ruta a eliminar:");
		ComboBox<Ruta> comboRutas = new ComboBox<>();
		comboRutas.getItems().setAll(grafo.getTodasLasRutas());
		comboRutas.setMaxWidth(Double.MAX_VALUE);
		Button btnConfirmar = new Button("Eliminar");
		btnConfirmar.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white;");
		btnConfirmar.setMaxWidth(Double.MAX_VALUE);
		btnConfirmar.setOnAction(e -> {
			Ruta seleccionada = comboRutas.getValue();
			if (seleccionada == null) { showError("Selecciona una ruta"); return; }
			boolean ok = grafo.eliminarRuta(seleccionada);
			if (ok) {
				showInfo("Ruta eliminada: " + seleccionada.getOrigen().getNombre() + " -> " + seleccionada.getDestino().getNombre());
				actualizarCombosParadas();
				if (mainApp != null) mainApp.redraw();
				popup.close();
			} else {
				showError("No se pudo eliminar la ruta");
			}
		});
		layout.getChildren().addAll(label, comboRutas, btnConfirmar);
		popup.setScene(new Scene(layout, 300, 160));
		popup.showAndWait();
	}

	@FXML
	private void onBuscarRuta(ActionEvent event) {
		if (grafo == null) { showError("Grafo no inicializado"); return; }
		Parada origen = comboCalcOrigen != null ? comboCalcOrigen.getValue() : null;
		Parada destino = comboCalcDestino != null ? comboCalcDestino.getValue() : null;
		String criterioStr = comboCriterio != null ? comboCriterio.getValue() : null;
		String algoritmoStr = comboAlgoritmo != null ? comboAlgoritmo.getValue() : null;
		String vehiculo = comboVehiculo != null ? comboVehiculo.getValue() : "Carro";
		if (origen == null || destino == null) { showError("Selecciona origen y destino"); return; }
		if (origen.equals(destino)) { showError("Origen y destino no pueden ser iguales"); return; }
		if (criterioStr == null) { showError("Selecciona un criterio"); return; }
		if (algoritmoStr == null) { showError("Selecciona un algoritmo"); return; }
		Criterio criterio = Criterio.valueOf(criterioStr);
		ResultadoCamino resultado = calcularRuta(origen, destino, criterio, algoritmoStr, vehiculo);
		if (resultado == null || resultado.getCamino().isEmpty()) {
			showError("No se encontró camino entre las paradas seleccionadas");
			return;
		}
		mostrarResultado(resultado, criterio, true);
		if (mainApp != null) {
			mainApp.highlightParadas(resultado.getCamino());
		}
	}

	@FXML
	private void onVerAlternativa(ActionEvent event) {
		if (grafo == null) { showError("Grafo no inicializado"); return; }
		Parada origen = comboCalcOrigen != null ? comboCalcOrigen.getValue() : null;
		Parada destino = comboCalcDestino != null ? comboCalcDestino.getValue() : null;
		String criterioStr = comboCriterio != null ? comboCriterio.getValue() : null;
		String algoritmoStr = comboAlgoritmo != null ? comboAlgoritmo.getValue() : null;
		String vehiculo = comboVehiculo != null ? comboVehiculo.getValue() : "Carro";
		if (origen == null || destino == null) { showError("Selecciona origen y destino"); return; }
		if (criterioStr == null) { showError("Selecciona un criterio"); return; }
		if (algoritmoStr == null) { showError("Selecciona un algoritmo"); return; }
		Criterio criterio = Criterio.valueOf(criterioStr);
		ResultadoCamino principal = calcularRuta(origen, destino, criterio, algoritmoStr, vehiculo);
		if (principal == null || principal.getCamino().isEmpty()) {
			showError("No se encontró ruta principal");
			return;
		}
		Ruta rutaABloquear = principal.getRutaABloquear(grafo);
		if (rutaABloquear == null) { showError("No hay ruta alternativa disponible"); return; }
		grafo.eliminarRuta(rutaABloquear);
		ResultadoCamino alternativa = calcularRuta(origen, destino, criterio, algoritmoStr, vehiculo);
		grafo.agregarRuta(rutaABloquear);
		if (alternativa == null || alternativa.getCamino().isEmpty()) {
			showError("No hay ruta alternativa disponible");
			return;
		}
		mostrarResultado(principal, criterio, true);
		mostrarResultado(alternativa, criterio, false);
		if (mainApp != null) {
			mainApp.highlightParadas(principal.getCamino());
			mainApp.highlightParadasAlternativa(alternativa.getCamino());
		}
	}

	private ResultadoCamino calcularRuta(Parada origen, Parada destino, Criterio criterio, String algoritmo, String vehiculo) {
		int idOrigen = origen.getId();
		int idDestino = destino.getId();
		return switch (algoritmo) {
			case "DIJKSTRA" -> {
				Dijkstra.Resultado r = Dijkstra.calcular(grafo, idOrigen, criterio, vehiculo);
				List<Parada> camino = r.reconstruirCamino(grafo, idOrigen, idDestino);
				double valor = r.getDistancias().get(idDestino);
				yield new ResultadoCamino(camino, valor);
			}
			case "BELLMAN-FORD" -> {
				BellmanFord bf = new BellmanFord();
				yield bf.calcularBellman(grafo, idOrigen, idDestino, criterio, vehiculo);
			}
			case "BFS" -> {
				BFS bfs = new BFS();
				yield bfs.calcularBFS(grafo, idOrigen, idDestino);
			}
			default -> null;
		};
	}

	private void mostrarResultado(ResultadoCamino resultado, Criterio criterio, boolean esPrincipal) {
		StringBuilder camino = new StringBuilder();
		List<Parada> paradas = resultado.getCamino();
		for (int i = 0; i < paradas.size(); i++) {
			camino.append(paradas.get(i).getNombre());
			if (i < paradas.size() - 1) camino.append(" -> ");
		}

		double valor = resultado.getValorTotal();

		String unidad = switch (criterio) {
			case TIEMPO -> " min";
			case DISTANCIA -> " km";
			case COSTO -> " RD$";
		};
		if (esPrincipal) {
			if (labelRutaPrincipal != null) labelRutaPrincipal.setText(camino.toString());
			if (labelValorPrincipal != null) labelValorPrincipal.setText(valor + unidad);
		} else {
			if (labelRutaAlternativa != null) labelRutaAlternativa.setText(camino.toString());
			if (labelValorAlternativo != null) labelValorAlternativo.setText(valor + unidad);
		}
	}

	@FXML
	private void onGuardarGrafo(ActionEvent event) {
		try {
			com.gestiontransporte.sistemadegestiondetransporte.persistencia.JsonData.guardar(grafo);
			showInfo("Grafo guardado correctamente");
		} catch (Exception e) {
			showError("No se pudo guardar el grafo: " + e.getMessage());
		}
	}

	@FXML
	private void onCalcularRuta(ActionEvent event) {
		onBuscarRuta(event);
	}
}
package com.gestiontransporte.sistemadegestiondetransporte.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import com.gestiontransporte.sistemadegestiondetransporte.MainApp;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

/**
 * Controller para la vista principal. Expone el contenedor donde se insertará el pane
 * que dibuja el grafo y algunos controles de la UI.
 */
public class MainController {

	// Este campo debe estar enlazado con el fx:id del FXML
	@FXML
	public AnchorPane graphContainer;

	@FXML
	public AnchorPane graphContainer2;

	@FXML
	public ToggleGroup Criterio;

	@FXML
	public Label statusLabel;

	// Controls for Paradas
	@FXML private TextField txtParadaNombre;
	@FXML private Button btnAgregarParada;
	@FXML private Button btnRemoverParada;
	@FXML private Button btnModificarParada;

	// Controls for Rutas
	@FXML private ComboBox<Parada> comboRutaOrigen;
	@FXML private ComboBox<Parada> comboRutaDestino;
	@FXML private ComboBox<Ruta> comboRutas;
	@FXML private Spinner<Double> spinnerDistancia;
	@FXML private Spinner<Double> spinnerTiempo;
	@FXML private Spinner<Double> spinnerCosto;
	@FXML private TextField txtLinea;
	@FXML private Button btnAgregarRuta;
	@FXML private Button btnEditarRuta;
	@FXML private Button btnRemoverRuta;

	// Control for calcular ruta
	@FXML private ComboBox<Parada> comboCalcOrigen;
	@FXML private ComboBox<Parada> comboCalcDestino;
	@FXML private ToggleGroup vehiculo;
	@FXML private Button btnCalcularRuta;

	// referencias que MainApp seteará
	private Grafo grafo;
	private MainApp mainApp;


	// Método que MainApp puede usar para insertar el pane donde se dibuja el grafo.
	public void setGraphPane(Pane pane) {
		if (graphContainer == null) return;
		graphContainer.getChildren().setAll(pane);
		AnchorPane.setTopAnchor(pane, 0.0);
		AnchorPane.setBottomAnchor(pane, 0.0);
		AnchorPane.setLeftAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);
	}

	public AnchorPane getGraphContainer() {
		return graphContainer;
	}

	// setters usados por MainApp
	public void setGrafo(Grafo grafo) {
		this.grafo = grafo;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setStatus(String text) {
		if (statusLabel != null) statusLabel.setText(text);
	}

	// Mostrar errores en un popup y en la barra de estado
	private void showError(String message) {
		// actualizar statusLabel también
		setStatus("Error: " + message);
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText(message);
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
			alert.showAndWait();
		});
	}

	@FXML
	private void initialize() {
		// Asegurar que los spinners tengan value factories para evitar NPE al obtener valores
		try {
			if (spinnerDistancia != null && spinnerDistancia.getValueFactory() == null) {
				spinnerDistancia.setValueFactory(new javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 10000.0, 1.0, 0.1));
			}
			if (spinnerTiempo != null && spinnerTiempo.getValueFactory() == null) {
				spinnerTiempo.setValueFactory(new javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 10000.0, 1.0, 0.1));
			}
			if (spinnerCosto != null && spinnerCosto.getValueFactory() == null) {
				spinnerCosto.setValueFactory(new javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0, 0.5));
			}
		} catch (Exception ignore) {
			// Si algún control no está presente en la vista preview, no hacemos nada
		}
	}

	// actualizar combos internos del controlador según el grafo
	public void actualizarCombosParadas() {

		if (grafo == null) return;
		if (comboRutaOrigen != null) {
			comboRutaOrigen.getItems().setAll(grafo.getParadas());
		}
		if (comboRutaDestino != null) {
			comboRutaDestino.getItems().setAll(grafo.getParadas());
		}
		if (comboRutas != null) {
			comboRutas.getItems().setAll(grafo.getTodasLasRutas());
		}

		if (comboCalcOrigen != null) comboCalcOrigen.getItems().setAll(grafo.getParadas());
		if (comboCalcDestino != null) comboCalcDestino.getItems().setAll(grafo.getParadas());
	}

	// ---------- Handlers ----------
	@FXML
	private void onAgregarParada(ActionEvent event) {
		if (grafo == null) {
			showError("Grafo no inicializado");
			return;
		}
		String nombre = txtParadaNombre != null ? txtParadaNombre.getText() : null;
		if (nombre == null || nombre.trim().isEmpty()) {
			showError("Ingrese un nombre válido para la parada");
			return;
		}
		Parada p = new Parada(nombre.trim());
		boolean ok = grafo.agregarParada(p);
		if (ok) {
			showInfo("Parada agregada: " + p.getNombre());
			actualizarCombosParadas();
			if (mainApp != null) {
				mainApp.smartAddParada(p);
			}
		} else {
			showError("Ya existe una parada con el nombre: " + nombre);		}
	}

	@FXML
	private void onRemoverParada(ActionEvent event) {
		if (grafo == null) {
			showError("Grafo no inicializado");
			return;
		}
		String nombre = txtParadaNombre != null ? txtParadaNombre.getText() : null;
		if (nombre == null || nombre.trim().isEmpty()) {
			showError("Ingrese el nombre de la parada a remover");
			return;
		}
		// buscar por nombre (case-insensitive)
		Parada encontrada = null;
		for (Parada p : grafo.getParadas()) {
			if (p.getNombre().equalsIgnoreCase(nombre.trim())) { encontrada = p; break; }
		}
		if (encontrada == null) {
			showError("Parada no encontrada: " + nombre);
			return;
		}
		boolean ok = grafo.eliminarParada(encontrada);
		if (ok) {
			showInfo("Parada eliminada: " + encontrada.getNombre());
			actualizarCombosParadas();
			if (mainApp != null) { mainApp.smartRemoveParada(encontrada); }
		} else {
			showError("No se pudo eliminar la parada");
		}
	}

	@FXML
	private void onModificarParada(ActionEvent event) {
		showInfo("Función modificar parada no implementada (puedes implementar un diálogo)");
	}

	@FXML
	private void onAgregarRuta(ActionEvent event) {
		if (grafo == null) { showError("Grafo no inicializado"); return; }

		Parada o = comboRutaOrigen != null ? comboRutaOrigen.getValue() : null;
		Parada d = comboRutaDestino != null ? comboRutaDestino.getValue() : null;

		if (o == null || d == null) { showError("Selecciona origen y destino"); return; }
		if (o.equals(d)){ showError("El origen y destino no pueden ser la misma parada"); return; }

		double dist = spinnerDistancia != null ? spinnerDistancia.getValue() : 1.0;
		double tiem = spinnerTiempo != null ? spinnerTiempo.getValue() : 1.0;
		double costo = spinnerCosto != null ? spinnerCosto.getValue() : 1.0;

		if (dist <= 0) { showError("La distancia debe ser mayor a 0"); return ; }
		if (tiem <= 0) { showError("El tiempo debe ser mayor a 0"); return; }

		Ruta nuevaRuta = new Ruta(o,d,dist,tiem,costo);

		boolean ok = grafo.agregarRuta(nuevaRuta);
		if (ok) {
			showInfo("Ruta agregada entre " + o.getNombre() + " y " + d.getNombre());
			actualizarCombosParadas();
			if (mainApp != null) mainApp.smartAddRuta(o, d);
		}
		else showError("Ya existe una ruta entre " + o.getId() + " y " + d.getNombre());
	}

	@FXML
	private void onEditarRuta(ActionEvent event) {
		showInfo("Editar ruta no implementado (usa seleccionar y formulario)");
	}

	@FXML
	private void onRemoverRuta(ActionEvent event) {
		if (grafo == null) { setStatus("Grafo no inicializado"); return; }

		// If the routes combo is present and a route is selected, remove that specific route
		try {
			if (comboRutas != null && comboRutas.getValue() != null) {
				Ruta seleccionada = comboRutas.getValue();
				boolean ok = grafo.eliminarRuta(seleccionada);
				if (ok) {
					showInfo("Ruta eliminada: " + seleccionada.getOrigen().getNombre() + " -> " + seleccionada.getDestino().getNombre());
					actualizarCombosParadas();
					if (mainApp != null) mainApp.smartRemoveRuta(seleccionada.getOrigen(), seleccionada.getDestino());
				} else {
					showError("No se pudo eliminar la ruta seleccionada");
				}
				return;
			}

			// Fallback: remove by origen/destino selection (existing behavior)
			Parada o = comboRutaOrigen != null ? comboRutaOrigen.getValue() : null;
			Parada d = comboRutaDestino != null ? comboRutaDestino.getValue() : null;
			if (o == null || d == null) { setStatus("Selecciona origen y destino"); return; }
			boolean ok = grafo.eliminarRutaDoble(o, d);
			if (ok) {
				showInfo("Ruta doble eliminada entre " + o.getNombre() + " y " + d.getNombre());
				if (mainApp != null) mainApp.smartRemoveRuta(o, d);
				actualizarCombosParadas();
			} else showError("No se pudo eliminar la ruta");
		} catch (Exception ex) {
			showError("Error al eliminar la ruta: " + ex.getMessage());
		}
	}

	@FXML
	private void onCalcularRuta(ActionEvent event) {
		// Aquí puedes disparar la lógica de cálculo (Dijkstra, etc.) usando mainApp o grafo
		showInfo("Calcular ruta: función delegada a MainApp");
		if (mainApp != null) mainApp.redraw();
	}

}


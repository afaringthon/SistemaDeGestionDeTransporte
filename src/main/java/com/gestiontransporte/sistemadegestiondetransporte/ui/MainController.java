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
import com.gestiontransporte.sistemadegestiondetransporte.algoritmos.BFS;
import com.gestiontransporte.sistemadegestiondetransporte.algoritmos.BellmanFord;
import com.gestiontransporte.sistemadegestiondetransporte.algoritmos.Criterio;
import com.gestiontransporte.sistemadegestiondetransporte.algoritmos.Dijkstra;
import com.gestiontransporte.sistemadegestiondetransporte.algoritmos.ResultadoCamino;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.List;

public class MainController {

	@FXML public AnchorPane graphContainer;
	@FXML public AnchorPane graphContainer2;
	@FXML public ToggleGroup toggleCriterio;
	@FXML public Label statusLabel;

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

	// Controls for calcular ruta
	@FXML private ComboBox<Parada> comboCalcOrigen;
	@FXML private ComboBox<Parada> comboCalcDestino;
	@FXML private ComboBox<String> comboCriterio;
	@FXML private ComboBox<String> comboAlgoritmo;
	@FXML private ComboBox<String> comboVehiculo;
	@FXML private Label labelRutaPrincipal;
	@FXML private Label labelValorPrincipal;
	@FXML private Label labelRutaAlternativa;
	@FXML private Label labelValorAlternativo;
	@FXML private Label labelParadaSeleccionada;
	@FXML private Label labelRutaSeleccionada;

	// parada y ruta seleccionadas al hacer click
	private Parada paradaSeleccionada;
	private Ruta rutaSeleccionada;

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

	// parada seleccionada al hacer click en el grafo
	public void setParadaSeleccionada(Parada p) {
		this.paradaSeleccionada = p;
		if (labelParadaSeleccionada != null) {
			labelParadaSeleccionada.setText(p != null ? p.getNombre() : "-");
		}
	}

	// ruta seleccionada al hacer click en el grafo
	public void setRutaSeleccionada(Ruta r) {
		this.rutaSeleccionada = r;
		if (labelRutaSeleccionada != null) {
			labelRutaSeleccionada.setText(r != null ? r.getOrigen().getNombre() + " -> " + r.getDestino().getNombre() : "-");
		}
	}

	private void showError(String message) {
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
			if (comboCriterio != null) comboCriterio.getItems().addAll("TIEMPO", "DISTANCIA", "COSTO");
			if (comboAlgoritmo != null) comboAlgoritmo.getItems().addAll("DIJKSTRA", "BELLMAN-FORD", "BFS", "FLOYD");
			if (comboVehiculo != null) comboVehiculo.getItems().addAll("Carro", "Moto", "A Pie");
		} catch (Exception ignore) {}
	}

	public void actualizarCombosParadas() {
		if (grafo == null) return;
		if (comboRutaOrigen != null) comboRutaOrigen.getItems().setAll(grafo.getParadas());
		if (comboRutaDestino != null) comboRutaDestino.getItems().setAll(grafo.getParadas());
		if (comboRutas != null) comboRutas.getItems().setAll(grafo.getTodasLasRutas());
		if (comboCalcOrigen != null) comboCalcOrigen.getItems().setAll(grafo.getParadas());
		if (comboCalcDestino != null) comboCalcDestino.getItems().setAll(grafo.getParadas());
	}

	@FXML
	private void onAgregarParada(ActionEvent event) {
		if (grafo == null) { showError("Grafo no inicializado"); return; }
		String nombre = txtParadaNombre != null ? txtParadaNombre.getText() : null;
		if (nombre == null || nombre.trim().isEmpty()) { showError("Ingrese un nombre válido para la parada"); return; }
		Parada p = new Parada(nombre.trim());
		boolean ok = grafo.agregarParada(p);
		if (ok) {
			showInfo("Parada agregada: " + p.getNombre());
			actualizarCombosParadas();
			if (mainApp != null) mainApp.smartAddParada(p);
		} else {
			showError("Ya existe una parada con el nombre: " + nombre);
		}
	}

	@FXML
	private void onRemoverParada(ActionEvent event) {
		if (grafo == null) { showError("Grafo no inicializado"); return; }

		// usa la parada seleccionada al hacer click
		if (paradaSeleccionada == null) {
			showError("Selecciona una parada en el grafo primero");
			return;
		}

		boolean ok = grafo.eliminarParada(paradaSeleccionada);
		if (ok) {
			showInfo("Parada eliminada: " + paradaSeleccionada.getNombre());
			paradaSeleccionada = null;
			if (labelParadaSeleccionada != null) labelParadaSeleccionada.setText("-");
			actualizarCombosParadas();
			if (mainApp != null) mainApp.redraw();
		} else {
			showError("No se pudo eliminar la parada");
		}
	}

	@FXML
	private void onModificarParada(ActionEvent event) {
		showInfo("Función modificar parada no implementada");
	}

	@FXML
	private void onAgregarRuta(ActionEvent event) {
		if (grafo == null) { showError("Grafo no inicializado"); return; }

		Parada o = comboRutaOrigen != null ? comboRutaOrigen.getValue() : null;
		Parada d = comboRutaDestino != null ? comboRutaDestino.getValue() : null;

		if (o == null || d == null) { showError("Selecciona origen y destino"); return; }
		if (o.equals(d)) { showError("El origen y destino no pueden ser la misma parada"); return; }

		double dist = spinnerDistancia != null ? spinnerDistancia.getValue() : 1.0;
		double tiem = spinnerTiempo != null ? spinnerTiempo.getValue() : 1.0;
		double costo = spinnerCosto != null ? spinnerCosto.getValue() : 0.0;

		if (dist <= 0) { showError("La distancia debe ser mayor a 0"); return; }
		if (tiem <= 0) { showError("El tiempo debe ser mayor a 0"); return; }

		Ruta nuevaRuta = new Ruta(o, d, dist, tiem, costo);
		boolean ok = grafo.agregarRuta(nuevaRuta);
		if (ok) {
			showInfo("Ruta agregada entre " + o.getNombre() + " y " + d.getNombre());
			actualizarCombosParadas();
			if (mainApp != null) mainApp.smartAddRuta(o, d);
		} else {
			showError("Ya existe una ruta entre " + o.getNombre() + " y " + d.getNombre());
		}
	}

	@FXML
	private void onEditarRuta(ActionEvent event) {
		showInfo("Editar ruta no implementado");
	}

	@FXML
	private void onRemoverRuta(ActionEvent event) {
		if (grafo == null) { showError("Grafo no inicializado"); return; }

		if (rutaSeleccionada == null) {
			showError("Selecciona una ruta en el grafo primero");
			return;
		}

		boolean ok = grafo.eliminarRuta(rutaSeleccionada);
		if (ok) {
			showInfo("Ruta eliminada: " + rutaSeleccionada.getOrigen().getNombre() + " -> " + rutaSeleccionada.getDestino().getNombre());
			rutaSeleccionada = null;
			if (labelRutaSeleccionada != null) labelRutaSeleccionada.setText("-");
			actualizarCombosParadas();
			if (mainApp != null) mainApp.redraw();
		} else {
			showError("No se pudo eliminar la ruta");
		}
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

		// mostrar ruta principal
		mostrarResultado(resultado, criterio, true);

		// highlight en el grafo
		if (mainApp != null) mainApp.highlightParadas(resultado.getCamino());
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

		// calcular ruta principal
		ResultadoCamino principal = calcularRuta(origen, destino, criterio, algoritmoStr, vehiculo);
		if (principal == null || principal.getCamino().isEmpty()) {
			showError("No se encontró ruta principal");
			return;
		}

		// bloquear primera ruta del camino principal
		Ruta rutaABloquear = principal.getRutaABloquear(grafo);
		if (rutaABloquear == null) {
			showError("No hay ruta alternativa disponible");
			return;
		}

		grafo.eliminarRuta(rutaABloquear);
		ResultadoCamino alternativa = calcularRuta(origen, destino, criterio, algoritmoStr, vehiculo);
		grafo.agregarRuta(rutaABloquear);

		if (alternativa == null || alternativa.getCamino().isEmpty()) {
			showError("No hay ruta alternativa disponible");
			return;
		}

		// mostrar ambas rutas
		mostrarResultado(principal, criterio, true);
		mostrarResultado(alternativa, criterio, false);
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

		double valor = criterio == Criterio.COSTO
				? resultado.getValorTotalConDescuento(criterio)
				: resultado.getValorTotal();

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
	private void onCalcularRuta(ActionEvent event) {
		onBuscarRuta(event);
	}
}
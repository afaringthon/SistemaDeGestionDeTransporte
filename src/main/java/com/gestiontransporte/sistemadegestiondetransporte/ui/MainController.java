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

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal de la interfaz grafica del sistema de transporte.
 * Maneja todos los eventos de la vista: agregar, modificar y eliminar paradas y rutas,
 * calcular rutas principales y alternativas, y actualizar el grafo visual.
 */
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

	/**
	 * Agrega el panel de SmartGraph al contenedor del grafo visual.
	 * @param pane panel de SmartGraph a mostrar
	 */
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

	/**
	 * Actualiza el label de estado en la parte inferior de la ventana.
	 * @param text texto a mostrar
	 */
	public void setStatus(String text) {
		if (statusLabel != null) statusLabel.setText(text);
	}

	/**
	 * Muestra un dialogo de error modal con el mensaje dado.
	 * @param message mensaje de error a mostrar
	 */
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

	/**
	 * Muestra un dialogo de informacion modal con el mensaje dado.
	 * @param message mensaje informativo a mostrar
	 */
	private void showInfo(String message) {
		setStatus(message);
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Informacion");
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.showAndWait();
		});
	}

	/**
	 * Inicializa los combos de criterio, algoritmo y vehiculo al arrancar la vista.
	 */
	@FXML
	private void initialize() {
		try {
			if (comboCriterio != null) comboCriterio.getItems().addAll("TIEMPO", "DISTANCIA", "COSTO", "TRASBORDO");
			if (comboAlgoritmo != null) comboAlgoritmo.getItems().addAll("DIJKSTRA", "BELLMAN-FORD", "BFS");
			if (comboVehiculo != null) comboVehiculo.getItems().addAll("Carro", "Moto", "A Pie");
		} catch (Exception ignore) {}
	}

	/**
	 * Actualiza los combos de origen y destino con las paradas actuales del grafo.
	 */
	public void actualizarCombosParadas() {
		if (grafo == null) return;
		if (comboCalcOrigen != null) comboCalcOrigen.getItems().setAll(grafo.getParadas());
		if (comboCalcDestino != null) comboCalcDestino.getItems().setAll(grafo.getParadas());
	}

	/**
	 * Crea una ventana popup modal con el titulo y dimensiones dados.
	 * @param titulo titulo de la ventana
	 * @param ancho ancho en pixeles
	 * @param alto alto en pixeles
	 * @return Stage configurado como popup modal
	 */
	private Stage crearPopup(String titulo, int ancho, int alto) {
		Stage popup = new Stage();
		popup.setTitle(titulo);
		popup.initModality(Modality.APPLICATION_MODAL);
		popup.setResizable(false);
		return popup;
	}

	/**
	 * Abre un popup para agregar una nueva parada al grafo.
	 * Verifica que el nombre no este vacio ni duplicado.
	 */
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
			if (nombre.isEmpty()) { showError("Ingrese un nombre valido"); return; }
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

	/**
	 * Abre un popup para modificar el nombre de una parada existente.
	 * Verifica que el nuevo nombre no este vacio ni duplicado.
	 */
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
			if (nuevoNombre.isEmpty()) { showError("Ingrese un nombre valido"); return; }
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

	/**
	 * Abre un popup para eliminar una parada del grafo.
	 * Verifica que el grafo siga siendo conexo antes de eliminar definitivamente.
	 * Pide confirmacion al usuario antes de proceder.
	 */
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

			// guarda las rutas salientes y entrantes antes de eliminar
			List<Ruta> rutasParada = new ArrayList<>(grafo.getRutasDesde(seleccionada));
			List<Ruta> todasRutas = new ArrayList<>(grafo.getTodasLasRutas());
			List<Ruta> rutasHaciaParada = new ArrayList<>();
			for (Ruta r : todasRutas) {
				if (r.getDestino().equals(seleccionada)) rutasHaciaParada.add(r);
			}

			// elimina temporalmente para verificar conexidad
			grafo.eliminarParada(seleccionada);

			// si el grafo deja de ser conexo, restaura y cancela
			if (!grafo.esConexo()) {
				grafo.agregarParada(seleccionada);
				for (Ruta r : rutasParada) grafo.agregarRuta(r);
				for (Ruta r : rutasHaciaParada) grafo.agregarRuta(r);
				showError("No se puede eliminar la parada, el grafo dejaria de ser conexo");
				return;
			}

			// restaura para pedir confirmacion al usuario
			grafo.agregarParada(seleccionada);
			for (Ruta r : rutasParada) grafo.agregarRuta(r);
			for (Ruta r : rutasHaciaParada) grafo.agregarRuta(r);

			// pide confirmacion antes de eliminar definitivamente
			Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
			confirmacion.setTitle("Confirmar eliminacion");
			confirmacion.setHeaderText(null);
			confirmacion.setContentText("Esta seguro que desea eliminar la parada: " + seleccionada.getNombre() + "?");
			confirmacion.initModality(Modality.APPLICATION_MODAL);
			if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

			// elimina definitivamente
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

	/**
	 * Abre un popup para agregar una nueva ruta dirigida al grafo.
	 * Verifica que los valores sean validos y que no exista ya la ruta.
	 */
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
				showError("Ingresa valores numericos validos");
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

	/**
	 * Abre un popup para modificar los atributos de una ruta existente.
	 * Verifica que los nuevos valores sean validos.
	 */
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
				showError("Ingresa valores numericos validos");
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

	/**
	 * Abre un popup para eliminar una ruta del grafo.
	 * Verifica conexidad antes de eliminar y pide confirmacion al usuario.
	 */
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

			// elimina temporalmente para verificar conexidad
			grafo.eliminarRuta(seleccionada);

			// si el grafo deja de ser conexo, restaura y cancela
			if (!grafo.esConexo()) {
				grafo.agregarRuta(seleccionada);
				showError("No se puede eliminar la ruta, el grafo dejaria de ser conexo");
				return;
			}

			// restaura para pedir confirmacion
			grafo.agregarRuta(seleccionada);

			// pide confirmacion antes de eliminar definitivamente
			Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
			confirmacion.setTitle("Confirmar eliminacion");
			confirmacion.setHeaderText(null);
			confirmacion.setContentText("Esta seguro que desea eliminar la ruta: " + seleccionada.getOrigen().getNombre() + " -> " + seleccionada.getDestino().getNombre() + "?");
			confirmacion.initModality(Modality.APPLICATION_MODAL);
			if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

			// elimina definitivamente
			grafo.eliminarRuta(seleccionada);
			showInfo("Ruta eliminada: " + seleccionada.getOrigen().getNombre() + " -> " + seleccionada.getDestino().getNombre());
			actualizarCombosParadas();
			if (mainApp != null) mainApp.redraw();
			popup.close();
		});
		layout.getChildren().addAll(label, comboRutas, btnConfirmar);
		popup.setScene(new Scene(layout, 300, 160));
		popup.showAndWait();
	}

	/**
	 * Calcula y muestra la ruta principal entre dos paradas usando el algoritmo
	 * y criterio seleccionados. Si el criterio es TRASBORDO, siempre usa BFS.
	 * Verifica pesos negativos si el algoritmo es Dijkstra.
	 */
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
			showError("No se encontro camino entre las paradas seleccionadas");
			return;
		}
		mostrarResultado(resultado, criterio, true);
		if (mainApp != null) mainApp.highlightParadas(resultado.getCamino());
	}

	/**
	 * Calcula y muestra la ruta principal y una ruta alternativa.
	 * Deshabilita temporalmente la primera arista del camino principal
	 * para forzar al algoritmo a encontrar otro camino.
	 */
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
			showError("No se encontro ruta principal");
			return;
		}
		Ruta rutaABloquear = principal.getRutaABloquear(grafo);
		if (rutaABloquear == null) { showError("No hay ruta alternativa disponible"); return; }

		// deshabilita la primera arista del camino para calcular la alternativa
		rutaABloquear.setHabilitada(false);
		ResultadoCamino alternativa = calcularRuta(origen, destino, criterio, algoritmoStr, vehiculo);
		rutaABloquear.setHabilitada(true);

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

	/**
	 * Ejecuta el algoritmo seleccionado para encontrar el camino optimo.
	 * Si el criterio es TRASBORDO, siempre usa BFS independientemente del algoritmo.
	 * Si el algoritmo es Dijkstra y hay pesos negativos, muestra un error.
	 * @param origen parada de origen
	 * @param destino parada de destino
	 * @param criterio criterio de optimizacion
	 * @param algoritmo nombre del algoritmo seleccionado
	 * @param vehiculo tipo de vehiculo del usuario
	 * @return ResultadoCamino con el camino y valor total, o null si no se encontro
	 */
	private ResultadoCamino calcularRuta(Parada origen, Parada destino, Criterio criterio, String algoritmo, String vehiculo) {
		int idOrigen = origen.getId();
		int idDestino = destino.getId();

		// si el criterio es trasbordos, siempre usa BFS sin importar el algoritmo
		if (criterio == Criterio.TRASBORDO) {
			return new BFS().calcularBFS(grafo, idOrigen, idDestino);
		}

		// si el algoritmo es Dijkstra, verifica que no haya pesos negativos
		if (algoritmo.equals("DIJKSTRA")) {
			for (Ruta r : grafo.getTodasLasRutas()) {
				double peso = switch (criterio) {
					case TIEMPO -> r.getTiempo();
					case DISTANCIA -> r.getDistancia();
					case COSTO -> r.getCosto();
					default -> 0.0;
				};
				if (peso < 0) {
					showError("Hay rutas con valores negativos. Usa Bellman-Ford para este caso.");
					return null;
				}
			}
		}

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
			case "BFS" -> new BFS().calcularBFS(grafo, idOrigen, idDestino);
			default -> null;
		};
	}

	/**
	 * Muestra el resultado del camino en los labels de la interfaz.
	 * @param resultado resultado del algoritmo con camino y valor total
	 * @param criterio criterio usado para determinar la unidad de medida
	 * @param esPrincipal true si es la ruta principal, false si es la alternativa
	 */
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
			case TRASBORDO -> " saltos";
		};
		if (esPrincipal) {
			if (labelRutaPrincipal != null) labelRutaPrincipal.setText(camino.toString());
			if (labelValorPrincipal != null) labelValorPrincipal.setText(valor + unidad);
		} else {
			if (labelRutaAlternativa != null) labelRutaAlternativa.setText(camino.toString());
			if (labelValorAlternativo != null) labelValorAlternativo.setText(valor + unidad);
		}
	}

	/**
	 * Guarda el estado actual del grafo en el archivo JSON.
	 */
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
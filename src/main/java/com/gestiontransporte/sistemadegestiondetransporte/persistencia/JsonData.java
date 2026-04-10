package com.gestiontransporte.sistemadegestiondetransporte.persistencia;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase utilitaria para guardar y cargar el grafo en formato JSON.
 * Usa la libreria Gson para serializar y deserializar el grafo.
 * El archivo se guarda en la carpeta home del usuario como grafo.json.
 */
public class JsonData {

    /** ruta del archivo JSON en la carpeta home del usuario */
    static String home = System.getProperty("user.home");
    static String ruta = home + File.separator + "grafo.json";

    /**
     * Clase interna que representa la estructura del JSON.
     * Contiene las listas de paradas y rutas del grafo.
     */
    private static class GrafoData {
        List<Parada> paradas;
        List<Ruta> rutas;
    }

    /** instancia de Gson con formato de impresion legible */
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Guarda el grafo en un archivo JSON en la carpeta home del usuario.
     * @param grafo grafo a guardar
     * @throws IOException si hay un error al escribir el archivo
     */
    public static void guardar(Grafo grafo) throws IOException {
        GrafoData data = new GrafoData();
        data.paradas = new ArrayList<>(grafo.getParadas());
        data.rutas = grafo.getTodasLasRutas();

        try (Writer writer = new FileWriter(ruta)) {
            gson.toJson(data, writer);
        }
    }

    /**
     * Carga el grafo desde el archivo JSON en la carpeta home del usuario.
     * Si el archivo no existe, devuelve un grafo vacio.
     * Restaura el serial de Parada para evitar ids duplicados.
     * Fuerza habilitada = true en todas las rutas porque Gson no serializa
     * valores booleanos por defecto correctamente.
     * @return grafo cargado desde el archivo, o grafo vacio si no existe
     * @throws IOException si hay un error al leer el archivo
     */
    public static Grafo cargar() throws IOException {
        File file = new File(ruta);
        if (!file.exists()) return new Grafo();

        try (Reader reader = new FileReader(file)) {
            GrafoData data = gson.fromJson(reader, GrafoData.class);

            if (data == null || data.paradas == null || data.rutas == null) {
                return new Grafo();
            }

            Grafo grafo = new Grafo();

            // agrega las paradas primero
            for (Parada p : data.paradas) {
                grafo.agregarParada(p);
            }

            // ajusta el serial para que los proximos ids no se repitan
            int maxId = data.paradas.stream()
                    .mapToInt(Parada::getId)
                    .max()
                    .orElse(0);
            Parada.setSerial(maxId + 1);

            // agrega las rutas forzando habilitada = true
            for (Ruta r : data.rutas) {
                r.setHabilitada(true);
                grafo.agregarRuta(r);
            }

            return grafo;
        }
    }
}
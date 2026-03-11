package com.gestiontransporte.sistemadegestiondetransporte.persistencia;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonData {
    static String home = System.getProperty("user.home");
    static String ruta = home + File.separator + "grafo.json";

    private static class GrafoData {
        List<Parada> paradas;
        List<Ruta> rutas;
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void guardar(Grafo grafo) throws IOException {
        GrafoData data = new GrafoData();
        data.paradas = new ArrayList<>(grafo.getParadas()); //copiar archivo
        data.rutas = grafo.getTodasLasRutas();

        try(Writer writer = new FileWriter(ruta)) {
            gson.toJson(data, writer);
        }
    }

    public static Grafo cargar() throws IOException{
        try(Reader reader = new FileReader(ruta)){
            GrafoData data = gson.fromJson(reader, GrafoData.class);
            //reconstruir
            Grafo grafo = new Grafo();

            for(Parada p : data.paradas){
                grafo.agregarParada(p);
            }

            for(Ruta r : data.rutas){
                grafo.agregarRuta(r);
            }
            return grafo;
        }
    }
}

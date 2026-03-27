package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.*;

public class Dijkstra {

    public enum Criterio {TIEMPO, DISTANCIA, COSTO}

    private final Grafo grafo;

    public Dijkstra(Grafo grafo) {
        this.grafo = grafo;
    }

    public ResultadoCamino encontrarCamino(Parada origen, Parada destino, Criterio criterio) {
        if (origen == null || destino == null) {
            return new ResultadoCamino(List.of(), Double.POSITIVE_INFINITY);
        }
        if (origen.equals(destino)) {
            return new ResultadoCamino(List.of(origen), 0.0);
        }

        Map<Parada, Double> distancias = new HashMap<>();
        Map<Parada, Parada> predecesores = new HashMap<>();
        Set<Parada> visitados = new HashSet<>();

        // inicializar distancias
        for (Parada parada : grafo.getParadas()) {
            distancias.put(parada, Double.POSITIVE_INFINITY);
        }
        distancias.put(origen, 0.0);

        PriorityQueue<Parada> cola =
                new PriorityQueue<>(Comparator.comparingDouble(distancias::get));
        cola.add(origen);

        while (!cola.isEmpty()) {
            Parada actual = cola.poll();

            // verificar si ya ha sido visitado
            if (!visitados.add(actual)) {
                continue;
            }

            if (actual.equals(destino)) {
                break;
            }

            for (Ruta ruta : grafo.getRutasDesde(actual)) {
                Parada vecino = ruta.getDestino();

                double peso;
                switch (criterio) {
                    case TIEMPO -> peso = ruta.getTiempo();
                    case DISTANCIA -> peso = ruta.getDistancia();
                    case COSTO -> peso = ruta.getCosto();
                    default -> throw new IllegalStateException("Criterio desconocido: " + criterio);
                }

                if (peso < 0) {
                    continue;
                }

                double distActual = distancias.get(actual);
                double distVecino = distancias.getOrDefault(vecino, Double.POSITIVE_INFINITY);
                double nuevaDist = distActual + peso;

                if (nuevaDist < distVecino) {
                    distancias.put(vecino, nuevaDist);
                    predecesores.put(vecino, actual);
                    cola.add(vecino);
                }
            }
        }

        double valorFinal = distancias.getOrDefault(destino, Double.POSITIVE_INFINITY);
        if (valorFinal == Double.POSITIVE_INFINITY) {
            return new ResultadoCamino(List.of(), Double.POSITIVE_INFINITY);
        }

        // reconstruir camino
        List<Parada> camino = new ArrayList<>();
        Parada paso = destino;
        while (paso != null) {
            camino.add(paso);
            paso = predecesores.get(paso);
        }
        Collections.reverse(camino);

        return new ResultadoCamino(camino, valorFinal);
    }
}
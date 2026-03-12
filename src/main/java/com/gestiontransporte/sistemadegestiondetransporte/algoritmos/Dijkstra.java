package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.*;

public class Dijkstra {

    public enum Criterio { TIEMPO, DISTANCIA }

    public static class Resultado {
        private final Map<Integer, Double> distancias;
        private final Map<Integer, Integer> anteriores;

        public Resultado(Map<Integer, Double> distancias, Map<Integer, Integer> anteriores) {
            this.distancias = distancias;
            this.anteriores = anteriores;
        }

        public List<Parada> reconstruirCamino(Grafo grafo, int idOrigen, int idDestino) {
            List<Parada> camino = new ArrayList<>();
            // si la distancia al destino es infinita, no hay camino
            if (!distancias.containsKey(idDestino)
                    || distancias.get(idDestino) == Double.POSITIVE_INFINITY) {
                return camino;
            }

            Integer actual = idDestino;
            while (actual != null) {
                Parada p = null;
                for (Parada parada : grafo.getParadas()) {
                    if (parada.getId() == actual) {
                        p = parada;
                        break;
                    }
                }
                if (p != null) {
                    camino.add(p);
                }
                if (actual.equals(idOrigen)) break;
                actual = anteriores.get(actual);
            }
            Collections.reverse(camino);
            return camino;
        }
    }

    private static class NodoDistancia {
        int id;
        double distancia;

        NodoDistancia(int id, double distancia) {
            this.id = id;
            this.distancia = distancia;
        }
    }

    public static Resultado calcular(Grafo grafo, int idOrigen, Criterio criterio) {
        Map<Integer, List<Ruta>> listaAdyacencia = new HashMap<>();
        for (Parada p : grafo.getParadas()) {
            listaAdyacencia.putIfAbsent(p.getId(), new ArrayList<>());
        }
        for (Ruta r : grafo.getTodasLasRutas()) {
            int u = r.getOrigen().getId();
            listaAdyacencia.putIfAbsent(u, new ArrayList<>());
            listaAdyacencia.get(u).add(r);
        }

        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        Map<Integer, Boolean> procesado = new HashMap<>();

        for (Parada p : grafo.getParadas()) {
            dist.put(p.getId(), Double.POSITIVE_INFINITY);
            prev.put(p.getId(), null);
            procesado.put(p.getId(), false);
        }
        dist.put(idOrigen, 0.0);

        PriorityQueue<NodoDistancia> cola =
                new PriorityQueue<>(Comparator.comparingDouble(n -> n.distancia));
        cola.add(new NodoDistancia(idOrigen, 0.0));

        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            int u = actual.id;

            if (Boolean.TRUE.equals(procesado.get(u))) {
                continue;
            }
            procesado.put(u, true);

            List<Ruta> vecinos = listaAdyacencia.get(u);
            if (vecinos == null) continue;

            for (Ruta ruta : vecinos) {
                int v = ruta.getDestino().getId();
                double peso = (criterio == Criterio.TIEMPO)
                        ? ruta.getTiempo()
                        : ruta.getDistancia();

                double nuevaDist = dist.get(u) + peso;
                if (nuevaDist < dist.get(v)) {
                    dist.put(v, nuevaDist);
                    prev.put(v, u);
                    cola.add(new NodoDistancia(v, nuevaDist));
                }
            }
        }

        return new Resultado(dist, prev);
    }
}
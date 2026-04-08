package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.*;

public class Dijkstra {

    public enum Criterio { TIEMPO, DISTANCIA }

    public static class Resultado {
        private final Map<Integer, Double> distancias; // Cambiar nombre mas claros, como peso
        private final Map<Integer, Integer> anteriores;

        //Constructor
        public Resultado(Map<Integer, Double> distancias, Map<Integer, Integer> anteriores) {
            this.distancias = distancias;
            this.anteriores = anteriores;
        }


        public List<Parada> reconstruirCamino(Grafo grafo, int idOrigen, int idDestino) {
            List<Parada> camino = new ArrayList<>();

            //Pregunta si el destino existe o si es infinito(no camino posible)
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

                if( actual.equals(idOrigen) ) break;
                    actual = anteriores.get(actual);
            }

            //Funcion de Listas
            Collections.reverse(camino);
            return camino;
        }
    }
//record
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
        //Si su arreglo no existe, la crea

        //Colocamos cada ruta a cada parada establecida
        for (Ruta r : grafo.getTodasLasRutas()) {
            int u = r.getOrigen().getId();
            listaAdyacencia.putIfAbsent(u, new ArrayList<>());
            listaAdyacencia.get(u).add(r);
            // 1 -> [ 1->2 , 1-> 3]
            //Nos ayuda a saber saber los vecino que se puede ir desde 1
        }


        //Cada idParada, distancia minima desde el origen
        Map<Integer, Double> dist = new HashMap<>();
        //Parada actual, parada anterior
        Map<Integer, Integer> prev = new HashMap<>();

        Map<Integer, Boolean> procesado = new HashMap<>();

        //Distancia = infinito indica que no hay caminos conocidos
        //Anterior = null
        // Procesado = false

        for (Parada p : grafo.getParadas()) {
            dist.put(p.getId(), Double.POSITIVE_INFINITY);
            prev.put(p.getId(), null);
            procesado.put(p.getId(), false);
        }
        dist.put(idOrigen, 0.0);
        //Comienza del origen

        //Comparator.comparingDouble, ordena los nodos segun la menor distancia
        PriorityQueue<NodoDistancia> cola = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distancia));

        cola.add(new NodoDistancia(idOrigen, 0.0));
        //Mete nodo inicial en la cola

        //Mientras haya nodos pendientes en cola, sigue
        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            int nodoActual = actual.id;
            // Poll consigue el primer elemento de la cola


            //Si este nodo fue procesado, lo salta, el mismo nodo puede entrar varias veces en la cola
            if (Boolean.TRUE.equals(procesado.get(nodoActual))) {
                continue;
            }
            procesado.put(nodoActual, true);

            //Busca las rutas que salen del nodo actual, sino tiene, sigue con el otro
            List<Ruta> vecinos = listaAdyacencia.get(nodoActual);
            if (vecinos == null) continue;

            //Recorre rutas desde vecinos = nodoActual
            for (Ruta ruta : vecinos) {

                //Consigo el nodo destino de esa ruta
                int nodoVecino = ruta.getDestino().getId();

                //Pregunto si es Tiempo
                double peso = (criterio == Criterio.TIEMPO)
                        ? ruta.getTiempo()
                        : ruta.getDistancia();

                // Calcular cuanto costaria llegar al vecino pasando por nodoActual + su criterio
                double nuevaDist = dist.get(nodoActual) + peso;

                //Si nueva distancia es mejor de su vecino, actualiza

                if (nuevaDist < dist.get(nodoVecino)) {

                    //Guarda la nmejor distancia
                    dist.put(nodoVecino, nuevaDist);

                    //Cambia el vecino con el que tiene mejor camino
                    prev.put(nodoVecino, nodoActual);

                    //Volvemos a colocar el vecino, porque con la nueva distancia, puede que por este sea un mejor camino
                    cola.add(new NodoDistancia(nodoVecino, nuevaDist));
                }
            }
        }

        //Cuando termina devuelve un resultado con:
        // Todas las distancias minimas y todos los nodos anteriores
        return new Resultado(dist, prev);
    }
}
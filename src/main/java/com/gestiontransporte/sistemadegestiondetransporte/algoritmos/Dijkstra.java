package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.*;

/**
 * Implementacion del algoritmo Dijkstra para encontrar el camino mas corto
 * en un grafo dirigido con pesos no negativos.
 * Utiliza una cola de prioridad para procesar siempre el nodo con menor
 * costo acumulado, garantizando el camino optimo.
 */
public class Dijkstra {

    /**
     * Clase interna que almacena el resultado de la ejecucion de Dijkstra.
     * Contiene las distancias minimas desde el origen y el mapa de anteriores
     * necesario para reconstruir el camino.
     */
    public static class Resultado {
        private final Map<Integer, Double> distancias;
        private final Map<Integer, Integer> anteriores;

        /**
         * Constructor del resultado.
         * @param distancias mapa de id de parada -> distancia minima desde el origen
         * @param anteriores mapa de id de parada -> id de parada anterior en el camino optimo
         */
        public Resultado(Map<Integer, Double> distancias, Map<Integer, Integer> anteriores) {
            this.distancias = distancias;
            this.anteriores = anteriores;
        }

        /**
         * Devuelve el mapa de distancias minimas desde el origen.
         * @return mapa de id de parada -> distancia minima
         */
        public Map<Integer, Double> getDistancias() {
            return distancias;
        }

        /**
         * Reconstruye el camino desde el origen hasta el destino
         * siguiendo el mapa de anteriores generado por Dijkstra.
         * @param grafo grafo con las paradas
         * @param idOrigen id de la parada de origen
         * @param idDestino id de la parada de destino
         * @return lista de paradas en orden desde origen hasta destino
         */
        public List<Parada> reconstruirCamino(Grafo grafo, int idOrigen, int idDestino) {
            List<Parada> camino = new ArrayList<>();

            // si el destino no fue alcanzado, no hay camino
            if (!distancias.containsKey(idDestino)
                    || distancias.get(idDestino) == Double.POSITIVE_INFINITY) {
                return camino;
            }

            // recorre hacia atras desde el destino hasta el origen
            Integer actual = idDestino;
            while (actual != null) {
                Parada p = null;

                for (Parada parada : grafo.getParadas()) {
                    if (parada.getId() == actual) {
                        p = parada;
                        break;
                    }
                }

                if (p != null) camino.add(p);

                if (actual.equals(idOrigen)) break;
                actual = anteriores.get(actual);
            }

            // invierte para obtener el orden correcto origen -> destino
            Collections.reverse(camino);
            return camino;
        }
    }

    /**
     * Clase interna que representa un nodo con su distancia acumulada
     * para ser usado en la cola de prioridad.
     */
    private static class NodoDistancia {
        int id;
        double distancia;

        NodoDistancia(int id, double distancia) {
            this.id = id;
            this.distancia = distancia;
        }
    }

    /**
     * Ejecuta el algoritmo Dijkstra desde un origen hacia todos los demas nodos.
     * Construye una lista de adyacencia con las rutas habilitadas y procesa
     * los nodos en orden de menor costo acumulado.
     * @param grafo grafo sobre el que se ejecuta el algoritmo
     * @param idOrigen id de la parada de origen
     * @param criterio criterio de optimizacion (TIEMPO, DISTANCIA, COSTO, TRASBORDO)
     * @param tipoDeVehiculo tipo de vehiculo del usuario
     * @return Resultado con las distancias minimas y el mapa de anteriores
     */
    public static Resultado calcular(Grafo grafo, int idOrigen, Criterio criterio, String tipoDeVehiculo) {
        Map<Integer, List<Ruta>> listaAdyacencia = new HashMap<>();

        // inicializa la lista de adyacencia para todas las paradas
        for (Parada p : grafo.getParadas()) {
            listaAdyacencia.putIfAbsent(p.getId(), new ArrayList<>());
        }

        // agrega solo las rutas habilitadas a la lista de adyacencia
        for (Ruta r : grafo.getTodasLasRutas()) {
            if (!r.esHabilitada()) continue;
            int u = r.getOrigen().getId();
            listaAdyacencia.putIfAbsent(u, new ArrayList<>());
            listaAdyacencia.get(u).add(r);
        }

        // inicializa distancias en infinito, anteriores en null y procesado en false
        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        Map<Integer, Boolean> procesado = new HashMap<>();

        for (Parada p : grafo.getParadas()) {
            dist.put(p.getId(), Double.POSITIVE_INFINITY);
            prev.put(p.getId(), null);
            procesado.put(p.getId(), false);
        }

        // el origen comienza con distancia 0
        dist.put(idOrigen, 0.0);

        // cola de prioridad ordenada por menor distancia acumulada
        PriorityQueue<NodoDistancia> cola = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distancia));
        cola.add(new NodoDistancia(idOrigen, 0.0));

        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            int nodoActual = actual.id;

            // si ya fue procesado, lo salta
            // un nodo puede entrar varias veces a la cola con distintas distancias
            if (Boolean.TRUE.equals(procesado.get(nodoActual))) continue;
            procesado.put(nodoActual, true);

            // obtiene los vecinos del nodo actual
            List<Ruta> vecinos = listaAdyacencia.get(nodoActual);
            if (vecinos == null) continue;

            for (Ruta ruta : vecinos) {

                // salta las rutas deshabilitadas (ruta alternativa)
                if (!ruta.esHabilitada()) continue;

                int nodoVecino = ruta.getDestino().getId();

                // calcula el peso segun el criterio seleccionado
                double peso = switch (criterio) {
                    case TIEMPO -> CalcularTiempo.calcular(ruta, tipoDeVehiculo);
                    case DISTANCIA -> ruta.getDistancia();
                    case COSTO -> tipoDeVehiculo.equalsIgnoreCase("a pie") ? 0.0 : ruta.getCosto();
                    case TRASBORDO -> 1.0;
                };

                // calcula el costo acumulado de llegar al vecino por este camino
                double nuevaDist = dist.get(nodoActual) + peso;

                // si es mejor que el costo actual, actualiza y vuelve a encolar
                if (nuevaDist < dist.get(nodoVecino)) {
                    dist.put(nodoVecino, nuevaDist);
                    prev.put(nodoVecino, nodoActual);
                    cola.add(new NodoDistancia(nodoVecino, nuevaDist));
                }
            }
        }

        // devuelve todas las distancias minimas y el mapa de anteriores
        return new Resultado(dist, prev);
    }
}
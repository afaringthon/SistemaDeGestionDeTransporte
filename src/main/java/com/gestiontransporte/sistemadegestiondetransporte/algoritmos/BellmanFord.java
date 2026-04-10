package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.*;

/**
 * Implementacion del algoritmo Bellman-Ford para encontrar el camino mas corto
 * en un grafo dirigido que puede contener pesos negativos.
 * A diferencia de Dijkstra, Bellman-Ford puede manejar aristas con pesos negativos
 * y detectar ciclos negativos en el grafo.
 */
public class BellmanFord {

    /**
     * Devuelve el peso de una ruta segun el criterio de optimizacion seleccionado.
     * @param r ruta a evaluar
     * @param criterio criterio de optimizacion (TIEMPO, DISTANCIA, COSTO, TRASBORDO)
     * @param tipoDeVehiculo tipo de vehiculo del usuario
     * @return peso de la ruta segun el criterio
     */
    private double getPeso(Ruta r, Criterio criterio, String tipoDeVehiculo){
        return switch (criterio) {
            case TIEMPO -> CalcularTiempo.calcular(r, tipoDeVehiculo);
            case DISTANCIA -> r.getDistancia();
            case COSTO -> tipoDeVehiculo.equalsIgnoreCase("a pie") ? 0.0 : r.getCosto();
            case TRASBORDO -> 1.0;
        };
    }

    /**
     * Ejecuta el algoritmo Bellman-Ford desde un origen hasta un destino.
     * Realiza V-1 iteraciones sobre todas las aristas para garantizar
     * encontrar el camino optimo, y una iteracion adicional para detectar ciclos negativos.
     * @param grafo grafo sobre el que se ejecuta el algoritmo
     * @param idOrigen id de la parada de origen
     * @param idDestino id de la parada de destino
     * @param criterio criterio de optimizacion
     * @param tipoDeVehiculo tipo de vehiculo del usuario
     * @return ResultadoCamino con el camino encontrado y su valor total
     */
    public ResultadoCamino calcularBellman(Grafo grafo, int idOrigen, int idDestino, Criterio criterio, String tipoDeVehiculo){

        Map<Integer, Double> costoCriterio = new HashMap<>();
        Map<Integer, Integer> anteriores = new HashMap<>();

        // inicializa todas las paradas con costo infinito menos el origen
        for(Parada p : grafo.getParadas()){
            if(p.getId() == idOrigen){
                costoCriterio.put(idOrigen, 0.0);
            }
            else{
                costoCriterio.put(p.getId(), Double.POSITIVE_INFINITY);
            }
            anteriores.put(p.getId(), null);
        }

        // el camino mas largo sin ciclos tiene V-1 aristas
        int rep = grafo.getParadas().size() - 1;

        List<Ruta> todasLasRutas = grafo.getTodasLasRutas();

        // repite V-1 veces relajando todas las aristas
        for(int ind = 0 ; ind < rep ; ind++){
            for(Ruta r : todasLasRutas){

                // salta las rutas deshabilitadas (ruta alternativa)
                if(!r.esHabilitada()) continue;

                int origenActual = r.getOrigen().getId();
                int destinoActual = r.getDestino().getId();

                // si el origen no ha sido alcanzado, no tiene sentido relajar
                if(costoCriterio.get(origenActual) == Double.POSITIVE_INFINITY) continue;

                // calcula el costo de llegar al destino pasando por esta arista
                double costoNuevo = costoCriterio.get(origenActual) + getPeso(r, criterio, tipoDeVehiculo);

                // si el nuevo costo es menor, actualiza el camino optimo
                if(costoNuevo < costoCriterio.get(destinoActual)){
                    costoCriterio.put(destinoActual, costoNuevo);
                    anteriores.put(destinoActual, origenActual);
                }
            }
        }

        // iteracion adicional para detectar ciclos negativos
        // si algun costo sigue mejorando, existe un ciclo negativo
        for(Ruta r : todasLasRutas){
            int origenActual = r.getOrigen().getId();
            int destinoActual = r.getDestino().getId();

            if(costoCriterio.get(origenActual) == Double.POSITIVE_INFINITY) continue;

            double costoNuevo = costoCriterio.get(origenActual) + getPeso(r, criterio, tipoDeVehiculo);

            if(costoNuevo < costoCriterio.get(destinoActual)){
                return new ResultadoCamino(new ArrayList<>(), -1);
            }
        }

        double valorTotal = costoCriterio.get(idDestino);
        List<Parada> camino = reconstruirCamino(grafo, anteriores, idDestino);

        return new ResultadoCamino(camino, valorTotal);
    }

    /**
     * Reconstruye el camino desde el origen hasta el destino
     * siguiendo el mapa de anteriores generado por Bellman-Ford.
     * @param grafo grafo con las paradas
     * @param anteriores mapa de parada -> parada anterior en el camino optimo
     * @param idDestino id de la parada destino
     * @return lista de paradas en orden desde origen hasta destino
     */
    public List<Parada> reconstruirCamino(Grafo grafo, Map<Integer, Integer> anteriores, int idDestino){

        // si el destino no tiene anterior, no existe camino
        if(anteriores.get(idDestino) == null){
            return new ArrayList<>();
        }

        List<Parada> caminoOrdenado = new ArrayList<>();
        Integer actual = idDestino;

        // recorre hacia atras desde el destino hasta el origen
        while(actual != null){
            caminoOrdenado.add(grafo.getParadaPorId(actual));
            actual = anteriores.get(actual);
        }

        // invierte para obtener el orden correcto origen -> destino
        Collections.reverse(caminoOrdenado);

        return caminoOrdenado;
    }
}
package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.*;

/**
 * Implementacion del algoritmo BFS (Breadth-First Search) para encontrar
 * el camino con menor numero de transbordos entre dos paradas.
 * Explora el grafo nivel por nivel, garantizando que el primer camino
 * encontrado al destino es el que tiene menos saltos.
 */
public class BFS {

    /**
     * Ejecuta el algoritmo BFS desde un origen hasta un destino.
     * Utiliza una cola para explorar los nodos nivel por nivel,
     * garantizando el camino con menor numero de transbordos.
     * @param grafo grafo sobre el que se ejecuta el algoritmo
     * @param idOrigen id de la parada de origen
     * @param idDestino id de la parada de destino
     * @return ResultadoCamino con el camino encontrado y el numero de saltos
     */
    public ResultadoCamino calcularBFS(Grafo grafo, int idOrigen, int idDestino){

        Queue<Integer> cola = new LinkedList<>();
        Map<Integer, Boolean> visitados = new HashMap<>();
        Map<Integer, Integer> anteriores = new HashMap<>();

        // inicializa todos los nodos como no visitados y sin anterior
        for(Parada p : grafo.getParadas()){
            visitados.put(p.getId(), false);
        }

        for(Parada p : grafo.getParadas()){
            anteriores.put(p.getId(), null);
        }

        // marca el origen como visitado y lo agrega a la cola
        visitados.put(idOrigen, true);
        cola.add(idOrigen);

        while( !cola.isEmpty() ){

            Integer dato = cola.poll();

            // si llegue al destino, el camino con menos saltos fue encontrado
            if( dato.equals(idDestino) ){
                break;
            }

            // obtiene las rutas salientes del nodo actual
            List<Ruta> vecinos = grafo.getVecinosPorID(dato);

            for(Ruta r : vecinos){

                // salta las rutas deshabilitadas (ruta alternativa)
                if(!r.esHabilitada()) continue;

                int idVecino = r.getDestino().getId();

                // si el vecino no fue visitado, lo descubre
                if(!visitados.get(idVecino)){

                    visitados.put(idVecino, true);

                    // guarda de donde viene cada vecino para reconstruir el camino
                    anteriores.put(idVecino, dato);

                    // agrega el vecino a la cola para procesarlo
                    cola.add(idVecino);
                }
            }
        }

        // reconstruye el camino usando el mapa de anteriores
        List<Parada> camino = reconstruirCamino(grafo, anteriores, idOrigen, idDestino);

        // si no hay camino valido, devuelve lista vacia con valor -1
        if(camino.isEmpty()){
            return new ResultadoCamino(camino, -1);
        }

        // el numero de saltos es la cantidad de paradas menos 1
        int saltos = camino.size() - 1;

        return new ResultadoCamino(camino, saltos);
    }

    /**
     * Reconstruye el camino desde el origen hasta el destino
     * siguiendo el mapa de anteriores generado por BFS.
     * @param grafo grafo con las paradas
     * @param anteriores mapa de parada -> parada anterior en el camino
     * @param idOrigen id de la parada de origen
     * @param idDestino id de la parada de destino
     * @return lista de paradas en orden desde origen hasta destino
     */
    public List<Parada> reconstruirCamino(Grafo grafo, Map<Integer, Integer> anteriores, int idOrigen, int idDestino){

        // si el destino no tiene anterior, no existe camino
        if(anteriores.get(idDestino) == null){
            return new ArrayList<>();
        }

        List<Parada> caminoOrdenado = new ArrayList<>();
        Integer actual = idDestino;

        // recorre hacia atras desde el destino hasta el origen
        while(actual != idOrigen){
            caminoOrdenado.add(grafo.getParadaPorId(actual));
            actual = anteriores.get(actual);
        }

        // agrega el origen al final antes de invertir
        caminoOrdenado.add(grafo.getParadaPorId(actual));

        // invierte para obtener el orden correcto origen -> destino
        Collections.reverse(caminoOrdenado);

        return caminoOrdenado;
    }
}
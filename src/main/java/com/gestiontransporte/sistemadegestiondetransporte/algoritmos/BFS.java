package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.*;

public class BFS {

    public ResultadoCamino calcularBFS(Grafo grafo, int idOrigen, int idDestino){

        Queue<Integer> cola = new LinkedList<>();
        Map<Integer, Boolean> visitados = new HashMap<>();
        Map<Integer, Integer> anteriores = new HashMap<>();

        // inicializa cada visitado y anteriores
        for(Parada p : grafo.getParadas()){
            visitados.put(p.getId(),false);
        }

        for(Parada p : grafo.getParadas()){
            anteriores.put(p.getId(), null);
        }

        visitados.put(idOrigen, true);
        cola.add(idOrigen);

        while( !cola.isEmpty() ){

            Integer dato = cola.poll();

            // si llegue al ultimo, ya hice la ruta con menor numero de saltos
            if( dato.equals(idDestino) ){
                break;
            }


            // rutas que salen por este nodo
            List<Ruta> vecinos = grafo.getVecinosPorID(dato);

            // recorrer rutas salientes
            for(Ruta r : vecinos){

                // descubro sus vecinos
                int idVecino = r.getDestino().getId();

                // pregunto si fue visitado
                if(!visitados.get(idVecino)){

                    visitados.put(idVecino,true);

                    // guardo de donde cada vecino
                    anteriores.put(idVecino,dato);

                    // los agrego en la cola para procesarlos tambien
                    cola.add(idVecino);
                }

            }

        }

        // Guardo los anteriores para convertirla en la secuencia de paradas
        List<Parada> camino = reconstruirCamino(grafo, anteriores, idOrigen, idDestino);

        // si no hay camino valido, devuelve una lista vacia
        if(camino.isEmpty()){
            return new ResultadoCamino(camino, -1);
        }

        // los saltos siempre son la cantidad de nodos - 1
        int saltos = camino.size() - 1;

        return new ResultadoCamino(camino, saltos);
    }


    public List<Parada> reconstruirCamino(Grafo grafo, Map<Integer, Integer> anteriores, int idOrigen, int idDestino){

        // si destino no tiene anterior, no hay camino
        if(anteriores.get(idDestino) == null){
            return new ArrayList<>();
        }

        List<Parada> caminoOrdenado = new ArrayList<>();

        Integer actual = idDestino;

        while(actual != idOrigen){

            // se busca la parada actual y se pone en caminoOrdenado
            caminoOrdenado.add(grafo.getParadaPorId(actual));

            // actual se vuelve el anterior de actual
            actual = anteriores.get(actual);
        }

        // al salir del while, actual es el origen, se agrega también
        caminoOrdenado.add(grafo.getParadaPorId(actual));

        // organizamos la lista
        Collections.reverse(caminoOrdenado);

        return caminoOrdenado;
    }

}

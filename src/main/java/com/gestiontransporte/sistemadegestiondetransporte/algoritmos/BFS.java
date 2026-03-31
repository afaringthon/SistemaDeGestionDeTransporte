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
        Map<Integer, List<Ruta>> adyacenciaPorID = new HashMap<>();

        for(Parada p : grafo.getParadas()){
            adyacenciaPorID.put(p.getId(),new ArrayList<>());
        }

        List<Ruta> rutas = grafo.getTodasLasRutas();

        for (Ruta rutaActual : rutas) {

            int idActual = rutaActual.getOrigen().getId();

            adyacenciaPorID.get(idActual).add(rutaActual);
        }

        //inicializa cada visitado y anteriores
        for(Parada p : grafo.getParadas()){
            visitados.put(p.getId(),false);
        }

        for(Parada p : grafo.getParadas()){
            anteriores.put(p.getId(), null);
        }

        visitados.put(idOrigen,true);
        cola.add(idOrigen);

        while( !cola.isEmpty() ){

            Integer dato = cola.poll();

            //si llegue al ultimo, ya hice la ruta con menor numero de saltos
            if( !dato.equals(idDestino) ){
                break;
            }


            //rutas que salen por este nodo
            List<Ruta> vecinos = adyacenciaPorID.get(dato);

            //recorrer rutas salientes

            for(Ruta r : vecinos){

                //descubro sus vecinos
                int idVecino = r.getDestino().getId();

                //pregunto si fue visitado
                if(!visitados.get(idVecino)){

                    visitados.put(idVecino,true);

                    //guardo de donde cada vecino
                    anteriores.put(idVecino,dato);

                    //los agrego en la cola para procesarlos tambien
                    cola.add(idVecino);
                }

            }

        }

        //Guardo los anteriores para convertirla en la secuencia de paradas
        List<Parada> camino = reconstruirCamino(grafo, anteriores, idOrigen, idDestino);

        //si no encontro camino valida, devuelvo una lista vacia
        if(camino.isEmpty()){
            return new ResultadoCamino(camino, -1);
        }

        double saltos = camino.size() - 1;

        return new ResultadoCamino(camino, saltos);
    }


}

package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BellmanFord {

    // Para conseguir el peso de cada criterio de una ruta
    private double getPeso(Ruta r, Criterio criterio){
        return switch (criterio) {
            case TIEMPO -> CalcularTiempo.calcular(r, r.getTipoDeVehiculo());
            case DISTANCIA -> r.getDistancia();
            case COSTO -> r.getCosto();
        };
    }


    public ResultadoCamino calcularBellman(Grafo grafo, int idOrigen, int idDestino, Criterio criterio){

        Map<Integer, Double> costoCriterio = new HashMap<>();
        Map<Integer, Integer> anteriores = new HashMap<>();

        // costoCriterio y anteriores inicializa asi porque no se el costo del camino todavia
        // se guardan todas las paradas por que no se el destino durante el proceso
        for(Parada p : grafo.getParadas()){
            if(p.getId() == idOrigen){
                costoCriterio.put(idOrigen, 0.0);
            }
            else{
                costoCriterio.put(p.getId(), Double.POSITIVE_INFINITY);
            }

            anteriores.put(p.getId(), null);
        }

        // Bellman trabaja con las rutas, y siempre son paradas - 1
        // Parada - 1 para garantizar explorar todos las rutas
        int rep = grafo.getParadas().size() - 1;

        List<Ruta> todasLasRutas = grafo.getTodasLasRutas();

        // repite parada-1 vece para explorar todos los caminos posibles
        for(int ind = 0 ; ind < rep ; ind++){

            // revisa todas las rutas para mejor el costo
            for(Ruta r : todasLasRutas){

                int origenActual = r.getOrigen().getId();
                int destinoActual = r.getDestino().getId();
                double costoNuevo = costoCriterio.get(origenActual) + getPeso(r, criterio);

                if(costoCriterio.get(origenActual) == Double.POSITIVE_INFINITY){
                    continue;
                }else if(costoNuevo < costoCriterio.get(destinoActual)){

                    // actualizo el mejor costo para llegar a destino
                    costoCriterio.put(destinoActual, costoNuevo);
                    // se guarda el destino, y de donde llego al destino
                    anteriores.put(destinoActual, origenActual);
                }
            }
        }

        // Hacemos otro bucle, si el camino mejora, hay un ciclo negativo
        for(Ruta r : todasLasRutas){
            int origenActual = r.getOrigen().getId();
            int destinoActual = r.getDestino().getId();
            double costoNuevo = costoCriterio.get(origenActual) + getPeso(r, criterio);

            if(costoCriterio.get(origenActual) == Double.POSITIVE_INFINITY){
                continue;
            }

            if(costoNuevo < costoCriterio.get(destinoActual)){
                // hay ciclo negativo, el resultado no es confiable
                return new ResultadoCamino(new ArrayList<>(), -1);
            }
        }




    }


}

package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.*;

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

            // suma el costo de cada ruta, desde el origen
            double costoNuevo = costoCriterio.get(origenActual) + getPeso(r, criterio);

            // no debe haber ningun camino mas caro que el costo que tiene llegar al destino
            if(costoNuevo < costoCriterio.get(destinoActual)){
                // hay ciclo negativo, el resultado no es confiable
                return new ResultadoCamino(new ArrayList<>(), -1);
            }
        }

        double valorTotal = costoCriterio.get(idDestino);
        List<Parada> camino = reconstruirCamino(grafo, anteriores, idDestino);

        return new ResultadoCamino(camino, valorTotal);
    }

    public List<Parada> reconstruirCamino(Grafo grafo, Map<Integer, Integer> anteriores, int idDestino){

        // verifica que pueda llegar destino desde su anterior
        if(anteriores.get(idDestino) == null){
            return new ArrayList<>();
        }

        List<Parada> caminoOrdenado = new ArrayList<>();
        Integer actual = idDestino;

        while(actual != null){

            // llego al actual y lo guardo
            for(Parada p : grafo.getParadas()){
                if(p.getId() == actual){
                    caminoOrdenado.add(p);
                    break;
                }
            }
            actual = anteriores.get(actual);
        }

        Collections.reverse(caminoOrdenado);

        return caminoOrdenado;
    }
}

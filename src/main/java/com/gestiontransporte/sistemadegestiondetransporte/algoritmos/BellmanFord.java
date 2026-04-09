package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.*;

public class BellmanFord {

    // Para conseguir el peso de cada criterio de una ruta
    private double getPeso(Ruta r, Criterio criterio, String tipoDeVehiculo){
        return switch (criterio) {
            case TIEMPO -> CalcularTiempo.calcular(r, tipoDeVehiculo);
            case DISTANCIA -> r.getDistancia();
            case COSTO -> r.getCosto(); // poner que si es a pie, cuesta 0
        };
    }


    public ResultadoCamino calcularBellman(Grafo grafo, int idOrigen, int idDestino, Criterio criterio, String tipoDeVehiculo){

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

        int rep = grafo.getParadas().size() - 1;

        List<Ruta> todasLasRutas = grafo.getTodasLasRutas();

        // repite parada-1 vece para explorar todos los caminos posibles
        for(int ind = 0 ; ind < rep ; ind++){

            // revisa todas las rutas para mejor el costo
            for(Ruta r : todasLasRutas){

                int origenActual = r.getOrigen().getId();
                int destinoActual = r.getDestino().getId();

                // si no ha sido descubierto, que lo salte
                if(costoCriterio.get(origenActual) == Double.POSITIVE_INFINITY){
                    continue;
                }

                // solo calcula si el origen ya fue descubierto
                double costoNuevo = costoCriterio.get(origenActual) + getPeso(r, criterio, tipoDeVehiculo);

                if(costoNuevo < costoCriterio.get(destinoActual)){
                    costoCriterio.put(destinoActual, costoNuevo);
                    anteriores.put(destinoActual, origenActual);
                }
            }
        }

        // Hacemos otro bucle, si el camino mejora, hay un ciclo negativo
        for(Ruta r : todasLasRutas){
            int origenActual = r.getOrigen().getId();
            int destinoActual = r.getDestino().getId();

            // suma el costo de cada ruta, desde el origen
            double costoNuevo = costoCriterio.get(origenActual) + getPeso(r, criterio, tipoDeVehiculo);

            // no debe haber ningun camino mas caro que el costo que tiene llegar al destino
            if(costoNuevo < costoCriterio.get(destinoActual)){
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

            caminoOrdenado.add(grafo.getParadaPorId(actual));
            actual = anteriores.get(actual);
        }

        Collections.reverse(caminoOrdenado);

        return caminoOrdenado;
    }
}

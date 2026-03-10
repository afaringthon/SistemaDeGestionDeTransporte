package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Grafo {
    private Map<Parada, List<Ruta>> adyacencia;
    // Costo de memoria con una Lista de Adyacencia es O(V+E) y si fuera mastriz es de O(V^2)

    public Grafo(Map<Parada, List<Ruta>> adyacencia) {
        this.adyacencia = adyacencia;
    }

    public Map<Parada, List<Ruta>> getAdyacencia() {
        return adyacencia;
    }

    public void setAdyacencia(Map<Parada, List<Ruta>> adyacencia) {
        this.adyacencia = adyacencia;
    }

    public boolean agregarParada(Parada parada){

        if(parada == null){
            return false;
        }

        if( this.adyacencia.containsKey(parada) ) {
            return false;
        }

        // ArrayList porque es lo mas eficiente a la hora de recorrer varias veces los caminos
        adyacencia.put(parada, new ArrayList<>());
        return true;

    }

    public boolean borrarParada(Parada parada){

    }

    public boolean agregarRuta(Ruta ruta) {

        if (ruta == null) {
            return false;
        }

        if (ruta.getOrigen() == null || ruta.getDestino() == null) {
            return false;
        }

        if (!adyacencia.containsKey(ruta.getOrigen()) || !adyacencia.containsKey(ruta.getDestino())) {
            return false;
        }

        for (Ruta r : adyacencia.get(ruta.getOrigen())) {
            if (r.getDestino().equals(ruta.getDestino())) {
                return false;
            }
        }

        adyacencia.get(ruta.getOrigen()).add(ruta);
        return true;
    }

    public boolean eliminarRuta(Ruta ruta){

    }

    public void mostrarGrafo(){

    }

}

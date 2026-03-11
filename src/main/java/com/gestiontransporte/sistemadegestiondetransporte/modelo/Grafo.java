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

    public boolean modificarParada(Parada parada) {

    }

    public boolean borrarParada(Parada parada){

    }

    public boolean agregarRuta(Parada origen, Parada destino, Map<Criterio, Double> criterios) {

        if (origen == null || destino == null || criterios == null) {
            return false;
        }

        if (!this.adyacencia.containsKey(origen) || !this.adyacencia.containsKey(destino)) {
            return false;
        }

        for (Ruta rutaExistente : this.adyacencia.get(origen)) {
            if (rutaExistente.getDestino().equals(destino)) {
                return false;
            }
        }

        Ruta nuevaRuta = new Ruta(destino, criterios);
        this.adyacencia.get(origen).add(nuevaRuta);

        return true;
    }

    public boolean modificarRuta(Parada origen, Parada destino){

    }

    public boolean eliminarRuta(Parada origen, Parada destino){
        if (origen == null || destino == null) {
            return false;
        }

        if (!this.adyacencia.containsKey(origen) || !this.adyacencia.containsKey(destino)) {
            return false;
        }

        for( Ruta r: this.adyacencia.get(origen) ){
            if(r.getDestino().equals(destino)){
                this.adyacencia.get(origen).remove(r);
                return true;
            }
        }

        return false;
    }

    public void mostrarGrafo(){

    }

}

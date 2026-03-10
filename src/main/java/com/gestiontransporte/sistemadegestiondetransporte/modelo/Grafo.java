package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.List;
import java.util.Map;

public class Grafo {
    private Map<Parada, List<Ruta>> adyacencia;

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

    }

    public boolean borrarParada(Parada parada){

    }

    public boolean agregarRuta(Ruta ruta){

    }

    public boolean eliminarRuta(Ruta ruta){

    }

    public void mostrarGrafo(){

    }

}

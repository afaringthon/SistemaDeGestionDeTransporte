package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.*;

public class Grafo {
    private final Map<Integer, Parada> paradas;
    private final Map<Parada, Set<Ruta>> listaAdj;

    public Grafo() {
        this.paradas = new HashMap<>();
        this.listaAdj = new HashMap<>();
    }

    public void agregarParada(Parada parada){
        paradas.put(parada.getId(), parada);
    }

    public boolean agregarRuta(Ruta ruta){
        Parada origen = ruta.getOrigen();
        Parada destino = ruta.getDestino();

        if(origen == null || destino == null) return false;

        Set<Ruta> vecinos = listaAdj.get(origen);
        if(vecinos == null){
            vecinos = new HashSet<>();
            listaAdj.put(origen, vecinos);
        }
        return vecinos.add(ruta);
    }

    public void eliminarParada(Parada parada){
        paradas.remove(parada.getId());
        listaAdj.remove(parada);

        for(Set<Ruta> rutas : listaAdj.values()){
            rutas.removeIf(ruta -> ruta.getDestino().equals(parada));
        }
    }

    public void eliminarRuta(Ruta ruta){
        Parada origen = ruta.getOrigen();
        Set<Ruta> vecinos = listaAdj.get(origen);
        if(vecinos != null){
            vecinos.remove(ruta);
        }
    }
}
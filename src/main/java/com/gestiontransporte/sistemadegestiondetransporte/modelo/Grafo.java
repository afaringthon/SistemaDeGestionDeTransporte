package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.*;

public class Grafo {
    List<Parada> paradas = new ArrayList<>();
    private Map<Integer, List<Ruta>> adyacencia = new HashMap<>(); //parada.GetId

    public void agregarParada(Parada parada){
        paradas.add(parada);
        adyacencia.putIfAbsent(parada.getId(), new ArrayList<>());
    }

    public void agregarRuta(Ruta ruta) {
        int origen = ruta.getOrigen().getId();
        adyacencia.putIfAbsent(origen, new ArrayList<>());
        adyacencia.get(origen).add(ruta);
    }

    public List<Parada> getParadas() {
        return paradas;
    }

    public List<Ruta> getTodasLasRutas(){
        List<Ruta> resultado = new ArrayList<>();
        for(List<Ruta> list : adyacencia.values()){
            resultado.addAll(list);
        }
        return resultado;
    }
    public List<Ruta> getRutasDesde(int idParada) {
        return adyacencia.getOrDefault(idParada, Collections.emptyList());
    }
}

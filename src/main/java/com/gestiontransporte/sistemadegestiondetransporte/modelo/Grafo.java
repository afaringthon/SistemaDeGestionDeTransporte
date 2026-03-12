package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.*;

public class Grafo {
    private final List<Parada> paradas = new ArrayList<>();
    private final Map<Integer, List<Ruta>> adyacencia = new HashMap<>(); //parada.GetId

    public void agregarParada(Parada parada){
        paradas.add(parada);
        adyacencia.putIfAbsent(parada.getId(), new ArrayList<>());
    }

    public boolean modificarParada(int id, Parada parada){
        for (Parada p : paradas){
            if(p.getId() == id){
                p.setNombre(parada.getNombre());
                return true;
            }
        }
        return false;
    }

    public void eliminarParada(int id){
        // eliminar la parada de la lista
        for(int i = 0; i < paradas.size(); i++){
            if(paradas.get(i).getId() == id){
                paradas.remove(i);
                break;
            }
        }

        // eliminar las rutas que salen de esa parada
        adyacencia.remove(id);


        // eliminar las rutas que llegan a esa parada
        for(List<Ruta> rutas : adyacencia.values()){
            for(int i = 0; i < rutas.size(); i++){
                if(rutas.get(i).getDestino().getId() == id){
                    rutas.remove(i);
                    i--; // ajustar indice porque la lista retrocedio
                }
            }
        }
    }


    public void agregarRuta(Ruta ruta) {
        int origen = ruta.getOrigen().getId();
        adyacencia.putIfAbsent(origen, new ArrayList<>());
        adyacencia.get(origen).add(ruta);
    }

    public void modificarRuta(Ruta ruta){}


    public void eliminarRuta(Ruta ruta){

    }


    public List<Parada> getParadas() {
        return new ArrayList<>(paradas);
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

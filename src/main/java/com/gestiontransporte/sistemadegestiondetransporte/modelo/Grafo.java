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

    public void modificarRuta(Ruta viejaRuta, Ruta nuevaRuta){

        int idOrigen = viejaRuta.getOrigen().getId(); // Encontramos el id de la parada de origen de la ruta vieja
        List<Ruta> rutasDelOrigen = adyacencia.get(idOrigen); // movemos las rutas de esa parada de origen a una lista

        for(int ind = 0 ; ind < rutasDelOrigen.size(); ind++){ // recorremos esa lista de rutas.
            if(rutasDelOrigen.get(ind).equals(viejaRuta)){ // preguntamos si en la ruta en la que estamos en la lista es igual a la vieja ruta a modificar
                rutasDelOrigen.set(ind, nuevaRuta); // cambiamos la vieja ruta por la nueva ruta.
                break;
            }
        }
    }


    public void eliminarRuta(Ruta ruta){
        int idOrigen = ruta.getOrigen().getId();
        List<Ruta> rutas = adyacencia.get(idOrigen);

        for(int ind = 0 ; ind < rutas.size() ; ind++){
            if(rutas.get(ind).equals(ruta) ){
                rutas.remove(ind);
            }
        }

    }

    public void crearRutaDoble(Parada o, Parada d, double dist, double tiem) {
        Ruta ida = new Ruta();
        ida.setOrigen(o);
        ida.setDestino(d);
        ida.setDistancia(dist);
        ida.setTiempo(tiem);
        agregarRuta(ida);

        Ruta vuelta = new Ruta();
        vuelta.setOrigen(d);
        vuelta.setDestino(o);
        vuelta.setDistancia(dist);
        vuelta.setTiempo(tiem);
        agregarRuta(vuelta);
    }

    public void eliminarRutaDoble(Parada origen, Parada destino){
        if(origen == null || destino == null){
            return;
        }
        int idOrigen = origen.getId();
        int idDestino = destino.getId();

        // eliminar origen a destino
        List<Ruta> rutasOrigen = adyacencia.get(idOrigen);
        if(rutasOrigen != null){
            for(int i = 0; i < rutasOrigen.size(); i++){
                Ruta r = rutasOrigen.get(i);

                if(r.getDestino().getId() == idDestino){
                    rutasOrigen.remove(i);
                    break;
                }
            }
        }

        // eliminar destino a origen
        List<Ruta> rutasDestino = adyacencia.get(idDestino);
        if(rutasDestino != null){
            for(int i = 0; i < rutasDestino.size(); i++){
                Ruta r = rutasDestino.get(i);

                if(r.getDestino().getId() == idOrigen){
                    rutasDestino.remove(i);
                    break;
                }
            }
        }
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

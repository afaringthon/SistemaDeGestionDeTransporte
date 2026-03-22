package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.*;

public class Grafo {
    private final Map<Parada, List<Ruta>> adyacencia;

    public Grafo() {
        adyacencia = new HashMap<>();
    }

    public boolean agregarParada(Parada parada){

        if(parada == null){
            return false;
        }

        if(adyacencia.containsKey(parada)){
            return false;
        }

        adyacencia.put(parada, new ArrayList<>());
        return true;
    }

    public boolean modificarParada(int idViejaParada, Parada nuevaParada){

        if(nuevaParada == null){
            return false;
        }

        for(Parada p : adyacencia.keySet()){
            if(p.getId() == idViejaParada){
                p.setNombre(nuevaParada.getNombre());
                return true;
            }
        }
        return false;
    }

    public boolean eliminarParada(Parada paradaAEliminar){

        if(paradaAEliminar == null || !adyacencia.containsKey(paradaAEliminar)){
            return false;
        }

        adyacencia.remove(paradaAEliminar);

        for(List<Ruta> rutas : adyacencia.values()){
            rutas.removeIf(ruta -> ruta.getDestino().getId() == paradaAEliminar.getId());
        }

        return true;
    }


    public boolean agregarRuta(Ruta ruta) {
        if(ruta == null){
            return false;
        }

        Parada origen = ruta.getOrigen();
        Parada destino = ruta.getDestino();

        if(origen == null || destino == null){
            return false;
        }

        if(!adyacencia.containsKey(origen) || !adyacencia.containsKey(destino)){
            return false;
        }

        List<Ruta> rutasOrigen = adyacencia.get(origen);

        //verifica que no haya una ruta entre existente entre las paradas
        for(Ruta r : rutasOrigen){
            if(r.getDestino().equals(destino)){
                return false;
            }
        }

        rutasOrigen.add(ruta);

        return true;
    }

    public boolean modificarRuta(Ruta viejaRuta, Ruta nuevaRuta){

        if(viejaRuta == null || nuevaRuta == null){
            return false;
        }

        Parada viejoOrigen = nuevaRuta.getOrigen();
        Parada viejoDestino = nuevaRuta.getDestino();

        Parada nuevoOrigen = nuevaRuta.getOrigen();
        Parada nuevoDestino = nuevaRuta.getDestino();

        if( !adyacencia.containsKey(nuevoOrigen) || !adyacencia.containsKey(nuevoDestino) ||
                !adyacencia.containsKey(viejoOrigen) || !adyacencia.containsKey(viejoDestino)){
            return false;
        }





    }

//Validar todas estas funciones

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


    //Funcion inecesaria, hacer un return simple de todas las listas
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

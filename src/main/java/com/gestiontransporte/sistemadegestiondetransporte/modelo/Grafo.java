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

        if(origen == null || destino == null || origen.equals(destino)){
            return false;
        }

        if(ruta.getDistancia() <= 0 || ruta.getTiempo() <= 0 || (ruta.getTipoDeVehiculo() == null) || ruta.getTipoDeVehiculo().isBlank()){
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

        Parada viejoOrigen = viejaRuta.getOrigen();
        Parada viejoDestino = viejaRuta.getDestino();

        Parada nuevoOrigen = nuevaRuta.getOrigen();
        Parada nuevoDestino = nuevaRuta.getDestino();

        if( !adyacencia.containsKey(nuevoOrigen) || !adyacencia.containsKey(nuevoDestino) ||
                !adyacencia.containsKey(viejoOrigen) || !adyacencia.containsKey(viejoDestino)){
            return false;
        }

        List<Ruta> viejasRutasOrigen = adyacencia.get(viejoOrigen);

        //verifica que la ruta vieja exista
        if(!viejasRutasOrigen.contains(viejaRuta)){
            return false;
        }

        List<Ruta> nuevasRutasOrigen = adyacencia.get(nuevoOrigen);

        //verifica que no exista mas de 1 misma ruta
        for(Ruta r : nuevasRutasOrigen){
            if (r != viejaRuta && r.getDestino().equals(nuevoDestino)) {
                return false;
            }
        }

        //Si la ruta no sale de la misma parada de origen, la pone donde va
        if( !viejoOrigen.equals(nuevoOrigen) ){
            viejasRutasOrigen.remove(viejaRuta);
            nuevasRutasOrigen.add(viejaRuta);
        }

        viejaRuta.setOrigen(nuevoOrigen);
        viejaRuta.setDestino(nuevoDestino);
        viejaRuta.setDistancia(nuevaRuta.getDistancia());
        viejaRuta.setTiempo(nuevaRuta.getTiempo());
        viejaRuta.setCosto(nuevaRuta.getCosto());
        viejaRuta.setTipoDeVehiculo(nuevaRuta.getTipoDeVehiculo());

        return true;
    }

    public boolean eliminarRuta(Ruta ruta){

        if(ruta == null){
            return false;
        }

        Parada origen = ruta.getOrigen();

        if(origen == null || !adyacencia.containsKey(origen)){
            return false;
        }

        List<Ruta> rutas = adyacencia.get(origen);

        for(int ind = 0 ; ind < rutas.size() ; ind++){
            if(rutas.get(ind).equals(ruta) ){
                rutas.remove(ind);
                return true;
            }
        }

        return false;
    }

    public boolean crearRutaDoble(Parada o, Parada d, double dist, double tiem, double costo, String tipoDeVehiculo) {

        if(o == null || d == null || o.equals(d) || tipoDeVehiculo == null){
            return false;
        }

        if(dist <= 0 || tiem <= 0 || costo < 0){
            return false;
        }

        Ruta ida = new Ruta(o, d, dist, tiem, costo, tipoDeVehiculo);

        Ruta vuelta = new Ruta(d, o, dist, tiem, costo, tipoDeVehiculo);

        if( !agregarRuta(ida) ){
            return false;
        }

        //intenta agregar vuelta
        if( !agregarRuta(vuelta) ){
            // quita la de ida para que existen las 2 o ninguna
            eliminarRuta(ida);
            return false;
        }

        return true;
    }

    public boolean eliminarRutaDoble(Parada origen, Parada destino){

        if(origen == null || destino == null){
            return false;
        }

        if(!adyacencia.containsKey(origen) || !adyacencia.containsKey(destino)){
            return false;
        }

        boolean idaEliminado = false;
        boolean vueltaEliminado = false;

        // eliminar origen a destino
        List<Ruta> rutasOrigen = adyacencia.get(origen);

        for(int ind = 0; ind < rutasOrigen.size(); ind++){

            Ruta r = rutasOrigen.get(ind);

            if(r.getDestino().equals(destino)){
                rutasOrigen.remove(ind);
                idaEliminado = true;
                break;
            }
        }
        // eliminar destino a origen
        List<Ruta> rutasDestino = adyacencia.get(destino);

        for(int ind = 0; ind < rutasDestino.size(); ind++){

            Ruta r = rutasDestino.get(ind);

            if(r.getDestino().equals(origen)){
                rutasDestino.remove(ind);
                vueltaEliminado = true;
                break;
            }
        }

        return idaEliminado || vueltaEliminado;
    }

    //para JSON
    public List<Ruta> getTodasLasRutas(){
        List<Ruta> resultado = new ArrayList<>();
        for(List<Ruta> list : adyacencia.values()){
            resultado.addAll(list);
        }
        return resultado;
    }

    public Parada getParadaPorId(int id) {
        for (Parada p : adyacencia.keySet()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }


    //Funcion para saber los vecinos, usada en BFS
    //getOrDefault devuelve la lista de Rutas de una clave
    public List<Ruta> getVecinosPorID(int id){

        Parada parada = getParadaPorId(id);

        if(parada == null) return new ArrayList<>();

        return new ArrayList<>(adyacencia.getOrDefault(parada, new ArrayList<>()));

    }

    public List<Parada> getParadas() {
        return new ArrayList<>(adyacencia.keySet());
    }

    public List<Ruta> getRutasDesde(Parada parada) {
        return new ArrayList<>(adyacencia.getOrDefault(parada, Collections.emptyList()));
    }
}
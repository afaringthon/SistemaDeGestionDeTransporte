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

    public boolean modificarParada(int id, String nuevoNombre) {
        Parada p = paradas.get(id);
        if (p == null) {
            return false;
        }

        if (nuevoNombre != null && !nuevoNombre.isBlank()) {
            p.setNombre(nuevoNombre);
        }

        return true;
    }

    private Ruta buscarRuta(int idOrigen, int idDestino) {
        Parada origen = paradas.get(idOrigen);
        Parada destino = paradas.get(idDestino);
        if (origen == null || destino == null) return null;

        Set<Ruta> vecinos = listaAdj.get(origen);
        if (vecinos == null) return null;

        for (Ruta r : vecinos) {
            if (r.getDestino().equals(destino)) {
                return r;
            }
        }
        return null;
    }

    public boolean modificarRuta(int idOrigen, int idDestino,
                                 Double nuevaDistancia,
                                 Double nuevoTiempo,
                                 Double nuevoCosto,
                                 String nuevaLinea) {
        Ruta r = buscarRuta(idOrigen, idDestino);
        if (r == null) {
            return false; // no existe esa ruta
        }

        if (nuevaDistancia != null) {
            r.setDistancia(nuevaDistancia);
        }
        if (nuevoTiempo != null) {
            r.setTiempo(nuevoTiempo);
        }
        if (nuevoCosto != null) {
            r.setCosto(nuevoCosto);
        }
        if (nuevaLinea != null && !nuevaLinea.isBlank()) {
            r.setLinea(nuevaLinea);
        }

        return true;
    }

    public Collection<Parada> getParadas() {
        return Collections.unmodifiableCollection(paradas.values());
    }

    public Parada getParadaPorId(int id) {
        return paradas.get(id);
    }

    public Set<Ruta> getRutasDesde(Parada origen) {
        return Collections.unmodifiableSet(
                listaAdj.getOrDefault(origen, Collections.emptySet())
        );
    }

    public boolean existeRuta(int idOrigen, int idDestino) {
        Ruta r = buscarRuta(idOrigen, idDestino);
        return r != null;
    }

    public List<Ruta> getTodasLasRutas() {
        List<Ruta> todas = new ArrayList<>();
        for (Set<Ruta> rutas : listaAdj.values()) {
            todas.addAll(rutas);
        }
        return todas;
    }
}
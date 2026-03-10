package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.Map;
import java.util.Objects;

public class Ruta {

    private Parada origen;
    private Parada destino;
    Map<Criterio, Double> criterios;

    public Ruta(Parada origen, Parada destino, Map<Criterio, Double> criterios) {
        this.origen = origen;
        this.destino = destino;
        this.criterios = criterios;
    }

    public Map<Criterio, Double> getCriterios() {
        return criterios;
    }

    public void setCriterios(Map<Criterio, Double> criterios) {
        this.criterios = criterios;
    }

    public Parada getDestino() {
        return destino;
    }

    public void setDestino(Parada destino) {
        this.destino = destino;
    }

    public Parada getOrigen() {
        return origen;
    }

    public void setOrigen(Parada origen) {
        this.origen = origen;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "origen=" + origen +
                ", destino=" + destino +
                ", criterios=" + criterios +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ruta ruta = (Ruta) o;
        return Objects.equals(origen, ruta.origen) && Objects.equals(destino, ruta.destino) && Objects.equals(criterios, ruta.criterios);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origen, destino, criterios);
    }
}

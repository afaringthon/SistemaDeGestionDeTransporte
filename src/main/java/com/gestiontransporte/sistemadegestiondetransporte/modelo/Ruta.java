package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.Map;
import java.util.Objects;

public class Ruta {
    private Parada origen;
    private Parada destino;
    private double distancia;
    private double tiempo;

    public Parada getOrigen() {
        return origen;
    }

    public void setOrigen(Parada origen) {
        this.origen = origen;
    }

    public Parada getDestino() {
        return destino;
    }

    public void setDestino(Parada destino) {
        this.destino = destino;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public double getTiempo() {
        return tiempo;
    }

    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "origen=" + origen +
                ", destino=" + destino +
                '}';
    }

}

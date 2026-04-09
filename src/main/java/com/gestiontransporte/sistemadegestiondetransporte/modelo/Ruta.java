package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import com.gestiontransporte.sistemadegestiondetransporte.algoritmos.Criterio;

import java.util.Objects;

public class Ruta {
    private Parada origen;
    private Parada destino;
    private double distancia;
    private double tiempo;
    private double costo;

    public Ruta(Parada origen, Parada destino, double distancia, double tiempo, double costo) {
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
        this.tiempo = tiempo;
        this.costo = costo;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getTiempo() {
        return tiempo;
    }

    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ruta ruta = (Ruta) o;
        // Dos rutas son iguales si tienen mismo origen y destino
        return Objects.equals(origen, ruta.origen)
                && Objects.equals(destino, ruta.destino);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origen, destino);
    }

    @Override
    public String toString() {
        return getOrigen().getId() + "->" + getDestino().getId();
    }






    @com.brunomnsilva.smartgraph.graphview.SmartLabelSource
    public String getEtiqueta() {
        return getOrigen().getId() + "->" + getDestino().getId();
    }

}
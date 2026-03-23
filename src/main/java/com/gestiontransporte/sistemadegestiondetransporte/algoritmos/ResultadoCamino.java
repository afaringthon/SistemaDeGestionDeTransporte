package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;

import java.util.List;

public class ResultadoCamino {
    private final List<Parada> camino;
    private final double distanaciaTotal;
    private final double costoTotal;
    private final int transbordos;

    public ResultadoCamino(List<Parada> camino, double distanaciaTotal, double costoTotal, int transbordos) {
        this.camino = camino;
        this.distanaciaTotal = distanaciaTotal;
        this.costoTotal = costoTotal;
        this.transbordos = transbordos;
    }

    public List<Parada> getCamino() {
        return camino;
    }

    public double getDistanaciaTotal() {
        return distanaciaTotal;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    public int getTransbordos() {
        return transbordos;
    }
}

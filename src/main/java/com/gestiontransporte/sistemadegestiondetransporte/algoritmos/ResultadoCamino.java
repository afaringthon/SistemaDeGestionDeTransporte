package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;

import java.util.List;

public class ResultadoCamino {
    private final List<Parada> camino;
    private final double valorTotal;

    public ResultadoCamino(List<Parada> camino, double valorTotal) {
        this.camino = camino;
        this.valorTotal = valorTotal;
    }

    public List<Parada> getCamino() {
        return camino;
    }

    public double getValorTotal() {
        return valorTotal;
    }
}

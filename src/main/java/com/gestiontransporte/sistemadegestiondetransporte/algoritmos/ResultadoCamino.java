package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

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


    // Para buscar rutas alternativas
    public Ruta getRutaABloquear(Grafo grafo){

        if(camino.size() < 2) return null;

        Parada p0 = camino.get(0);
        Parada p1 = camino.get(1);

        for(Ruta r: grafo.getRutasDesde(p0)){
            if(r.getDestino().equals(p1)){
                return r;
            }
        }
        return null;
    }


}

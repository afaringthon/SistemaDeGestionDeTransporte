package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Grafo;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Parada;
import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.util.List;

/**
 * Clase que encapsula el resultado de ejecutar un algoritmo de camino minimo.
 * Contiene el camino encontrado como lista de paradas y el valor total
 * segun el criterio de optimizacion utilizado.
 */
public class ResultadoCamino {
    private final List<Parada> camino;
    private final double valorTotal;

    /**
     * Constructor del resultado del camino.
     * @param camino lista de paradas en orden desde origen hasta destino
     * @param valorTotal valor total del camino segun el criterio (tiempo, distancia, costo o saltos)
     */
    public ResultadoCamino(List<Parada> camino, double valorTotal) {
        this.camino = camino;
        this.valorTotal = valorTotal;
    }

    /**
     * Devuelve la lista de paradas del camino encontrado.
     * @return lista de paradas en orden desde origen hasta destino
     */
    public List<Parada> getCamino() {
        return camino;
    }

    /**
     * Devuelve el valor total del camino segun el criterio utilizado.
     * @return valor total (minutos, kilometros, costo o saltos)
     */
    public double getValorTotal() {
        return valorTotal;
    }

    /**
     * Busca la primera ruta del camino principal para ser deshabilitada
     * temporalmente y calcular una ruta alternativa.
     * @param grafo grafo con las rutas disponibles
     * @return primera ruta del camino, o null si el camino tiene menos de 2 paradas
     */
    public Ruta getRutaABloquear(Grafo grafo){

        // el camino necesita al menos 2 paradas para tener una ruta que bloquear
        if(camino.size() < 2) return null;

        Parada p0 = camino.get(0);
        Parada p1 = camino.get(1);

        // busca la ruta entre la primera y segunda parada del camino
        for(Ruta r: grafo.getRutasDesde(p0)){
            if(r.getDestino().equals(p1)){
                return r;
            }
        }
        return null;
    }
}
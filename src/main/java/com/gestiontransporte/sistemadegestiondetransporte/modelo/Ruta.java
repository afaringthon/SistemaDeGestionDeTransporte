package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.Objects;

/**
 * Clase que representa una ruta dirigida entre dos paradas del sistema de transporte.
 * Contiene los atributos de distancia, tiempo y costo, ademas de un estado
 * de habilitacion usado para calcular rutas alternativas sin eliminar la ruta.
 */
public class Ruta {
    private Parada origen;
    private Parada destino;
    private double distancia;
    private double tiempo;
    private double costo;

    /** indica si la ruta esta disponible para ser usada por los algoritmos */
    private boolean habilitada = true;

    /**
     * Constructor que crea una ruta dirigida entre dos paradas.
     * La ruta comienza habilitada por defecto.
     * @param origen parada de origen
     * @param destino parada de destino
     * @param distancia distancia en kilometros
     * @param tiempo tiempo base en minutos
     * @param costo costo monetario en RD$
     */
    public Ruta(Parada origen, Parada destino, double distancia, double tiempo, double costo) {
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
        this.tiempo = tiempo;
        this.costo = costo;
    }

    /**
     * Devuelve el costo monetario de la ruta.
     * @return costo en RD$
     */
    public double getCosto() { return costo; }

    /**
     * Modifica el costo monetario de la ruta.
     * @param costo nuevo costo en RD$
     */
    public void setCosto(double costo) { this.costo = costo; }

    /**
     * Devuelve el tiempo base de la ruta sin factores de vehiculo.
     * @return tiempo en minutos
     */
    public double getTiempo() { return tiempo; }

    /**
     * Modifica el tiempo base de la ruta.
     * @param tiempo nuevo tiempo en minutos
     */
    public void setTiempo(double tiempo) { this.tiempo = tiempo; }

    /**
     * Devuelve la distancia de la ruta.
     * @return distancia en kilometros
     */
    public double getDistancia() { return distancia; }

    /**
     * Modifica la distancia de la ruta.
     * @param distancia nueva distancia en kilometros
     */
    public void setDistancia(double distancia) { this.distancia = distancia; }

    /**
     * Devuelve la parada de destino de la ruta.
     * @return parada destino
     */
    public Parada getDestino() { return destino; }

    /**
     * Modifica la parada de destino de la ruta.
     * @param destino nueva parada destino
     */
    public void setDestino(Parada destino) { this.destino = destino; }

    /**
     * Devuelve la parada de origen de la ruta.
     * @return parada origen
     */
    public Parada getOrigen() { return origen; }

    /**
     * Modifica la parada de origen de la ruta.
     * @param origen nueva parada origen
     */
    public void setOrigen(Parada origen) { this.origen = origen; }

    /**
     * Indica si la ruta esta habilitada para ser usada por los algoritmos.
     * Una ruta deshabilitada es ignorada al calcular caminos.
     * @return true si esta habilitada, false si esta deshabilitada
     */
    public boolean esHabilitada() { return habilitada; }

    /**
     * Habilita o deshabilita la ruta.
     * Usado para calcular rutas alternativas sin eliminar la ruta del grafo.
     * @param habilitada true para habilitar, false para deshabilitar
     */
    public void setHabilitada(boolean habilitada) { this.habilitada = habilitada; }

    /**
     * Dos rutas son iguales si tienen el mismo origen y destino.
     * @param o objeto a comparar
     * @return true si tienen el mismo origen y destino
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ruta ruta = (Ruta) o;
        return Objects.equals(origen, ruta.origen)
                && Objects.equals(destino, ruta.destino);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origen, destino);
    }

    /**
     * Devuelve la representacion en texto de la ruta para los combos de la interfaz.
     * @return texto con origen y destino
     */
    @Override
    public String toString() {
        return origen.getNombre() + " -> " + destino.getNombre();
    }

    /**
     * Etiqueta mostrada en SmartGraph sobre cada arista del grafo visual.
     * Muestra los criterios de la ruta: distancia, tiempo y costo.
     * @return texto con los criterios de la ruta
     */
    @com.brunomnsilva.smartgraph.graphview.SmartLabelSource
    public String getEtiqueta() {
        return distancia + " km | " + tiempo + " min | $" + costo;
    }
}
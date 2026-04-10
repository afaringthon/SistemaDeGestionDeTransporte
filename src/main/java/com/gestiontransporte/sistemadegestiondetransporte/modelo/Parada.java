package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.Objects;

/**
 * Clase que representa una parada del sistema de transporte.
 * Cada parada tiene un id unico generado automaticamente por un serial
 * estatico que se incrementa con cada nueva instancia.
 */
public class Parada {

    private final int id;
    private String nombre;

    /** contador estatico que genera ids unicos para cada parada */
    private static int serial = 1;

    /**
     * Constructor que crea una nueva parada con un id unico autogenerado.
     * @param nombre nombre de la parada
     */
    public Parada(String nombre) {
        this.id = serial++;
        this.nombre = nombre;
    }

    /**
     * Devuelve el id unico de la parada.
     * @return id de la parada
     */
    public int getId() {
        return id;
    }

    /**
     * Devuelve el nombre de la parada.
     * @return nombre de la parada
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Modifica el nombre de la parada.
     * @param nombre nuevo nombre de la parada
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el nombre de la parada como representacion en texto.
     * Usado por SmartGraph para mostrar la etiqueta del vertice.
     * @return nombre de la parada
     */
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Dos paradas son iguales si tienen el mismo id.
     * @param o objeto a comparar
     * @return true si tienen el mismo id
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Parada parada = (Parada) o;
        return id == parada.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Ajusta el serial para que el proximo id generado sea el correcto.
     * Usado al cargar el grafo desde JSON para evitar ids duplicados.
     * @param nuevoSerial valor del proximo serial
     */
    public static void setSerial(int nuevoSerial) {
        serial = nuevoSerial;
    }
}
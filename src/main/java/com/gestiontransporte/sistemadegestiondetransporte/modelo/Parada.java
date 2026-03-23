package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.Objects;

public class Parada {

    private final int id;
    private String nombre;
    private static int serial = 1;

    public Parada(String nombre) {
        this.id = serial++;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }

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

}

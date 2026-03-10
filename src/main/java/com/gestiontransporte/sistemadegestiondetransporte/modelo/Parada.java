package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.Objects;

public class Parada {

    private int id;
    private String nombre;

    public Parada(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Parada{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
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

package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import java.util.Objects;

public class Parada {

    private int id;
    private String nombre;
    private static int serial = 1;

    public Parada(String nombre) {
        this.id = serial++;
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


    //Para no imprimir caracteres raros
    @Override
    public String toString() {
        return id + " - " + nombre;
    }


    // Definimos que 2 paradas son iguales cuando tienen el mismo id y nada mas
    //Para que no compare la memoria sino el contenido
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Parada parada = (Parada) o;
        return id == parada.id;
    }

    //Si se modifica la funcion equals, tambien debe hacerse el hash
    //Identificador usando en HashMap
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}

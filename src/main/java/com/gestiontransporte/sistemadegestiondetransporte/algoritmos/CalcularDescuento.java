package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class CalcularDescuento {

    public static double esDescuento() {
        DayOfWeek dia = LocalDate.now().getDayOfWeek();

        return switch (dia) {
            case MONDAY -> 0.90;
            case THURSDAY -> 0.85;
            case SATURDAY -> 0.75;
            default -> 0.0;
        };
    }

}

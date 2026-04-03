package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.time.LocalTime;

public class CalcularTiempo {

    // Factores tiempo normal
    private static final double FACTOR_CARRO_NORMAL = 1.0;
    private static final double FACTOR_MOTO_NORMAL = 0.6;
    private static final double FACTOR_TRANSPORTE_NORMAL = 1.3;
    private static final double FACTOR_PIE_NORMAL = 3.0;

    // Factores hora pico
    private static final double FACTOR_CARRO_PICO = 1.5;
    private static final double FACTOR_MOTO_PICO = 1.2;
    private static final double FACTOR_TRANSPORTE_PICO = 1.7;

    // static para no tener que crear un objeto nuevo
    // final porque nos aseguramos de que no van a cambiar durante el programa

    // Verifica si la hora actual es hora pico
    private static boolean esHoraPico() {
        LocalTime ahora = LocalTime.now();

        LocalTime mananaInicio = LocalTime.of(7, 0);
        LocalTime mananaFin = LocalTime.of(9, 0);

        LocalTime medioDiaInicio = LocalTime.of(12, 0);
        LocalTime medioDiaFin = LocalTime.of(14, 0);

        LocalTime tardeInicio = LocalTime.of(17, 0);
        LocalTime tardeFin = LocalTime.of(19, 0);

        // verifica si la hora cae entre los rangos, diciendo si o no
        return (ahora.isAfter(mananaInicio) && ahora.isBefore(mananaFin)) ||
                (ahora.isAfter(medioDiaInicio) && ahora.isBefore(medioDiaFin)) ||
                (ahora.isAfter(tardeInicio) && ahora.isBefore(tardeFin));

    }

    // Devuelve el factor segun el vehiculo y si es hora pico o no
    private static double getFactor(String tipoVehiculo) {
        boolean pico = esHoraPico();

        // to lower evitamos problemas de comparacion con las letras
        return switch (tipoVehiculo.toLowerCase()) {

            // si el caso es un vehiculo de estos, pregunta si es la horra pico
            // si es, devuelve el atributo con el valor que le dimos, y si no, con el normal
            case "carro" -> pico ? FACTOR_CARRO_PICO : FACTOR_CARRO_NORMAL;
            case "moto" -> pico ? FACTOR_MOTO_PICO : FACTOR_MOTO_NORMAL;
            case "transporte publico" -> pico ? FACTOR_TRANSPORTE_PICO : FACTOR_TRANSPORTE_NORMAL;
            case "a pie" -> FACTOR_PIE_NORMAL;
            // si se ingresa un tipo de vehiculo desconocido, lo dejamos en 1

            default -> 1.0;
        };
    }

    // Calcula el tiempo final de una ruta segun el vehiculo seleccionado
    public static double calcular(Ruta ruta, String tipoVehiculo) {
        return ruta.getTiempo() * getFactor(tipoVehiculo);
    }


}

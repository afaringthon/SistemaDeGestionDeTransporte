package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

import com.gestiontransporte.sistemadegestiondetransporte.modelo.Ruta;

import java.time.LocalTime;

/**
 * Clase utilitaria para calcular el tiempo de viaje de una ruta
 * segun el tipo de vehiculo y si es hora pico o no.
 * Los factores de hora pico aumentan el tiempo de viaje para simular
 * el trafico en los horarios de mayor congestion.
 */
public class CalcularTiempo {

    // factores de tiempo en condiciones normales por tipo de vehiculo
    private static final double CARRO_NORMAL = 1.0;
    private static final double MOTO_NORMAL = 0.6;
    private static final double TRANSPORTE_NORMAL = 1.3;
    private static final double PIE_NORMAL = 3.0;

    // factores de tiempo en hora pico por tipo de vehiculo
    // a pie no tiene hora pico porque no depende del trafico
    private static final double CARRO_PICO = 1.5;
    private static final double MOTO_PICO = 1.2;
    private static final double TRANSPORTE_PICO = 1.7;

    // static: pertenece a la clase, no a una instancia
    // final: su valor no cambia durante la ejecucion del programa

    /**
     * Verifica si la hora actual corresponde a una hora pico.
     * Las horas pico son: manana (7-9), mediodia (12-14) y tarde (17-19).
     * @return true si es hora pico, false si no lo es
     */
    private static boolean esHoraPico() {
        LocalTime ahora = LocalTime.now();

        LocalTime mananaInicio = LocalTime.of(7, 0);
        LocalTime mananaFin = LocalTime.of(9, 0);

        LocalTime medioDiaInicio = LocalTime.of(12, 0);
        LocalTime medioDiaFin = LocalTime.of(14, 0);

        LocalTime tardeInicio = LocalTime.of(17, 0);
        LocalTime tardeFin = LocalTime.of(19, 0);

        // devuelve true si la hora actual cae en alguno de los rangos pico
        return (ahora.isAfter(mananaInicio) && ahora.isBefore(mananaFin)) ||
                (ahora.isAfter(medioDiaInicio) && ahora.isBefore(medioDiaFin)) ||
                (ahora.isAfter(tardeInicio) && ahora.isBefore(tardeFin));
    }

    /**
     * Devuelve el factor multiplicador de tiempo segun el vehiculo y la hora.
     * Si es hora pico, el factor es mayor para simular el trafico.
     * @param tipoVehiculo tipo de vehiculo (carro, moto, transporte publico, a pie)
     * @return factor multiplicador del tiempo de viaje
     */
    public static double getFactor(String tipoVehiculo) {
        boolean pico = esHoraPico();

        // toLowerCase evita problemas de comparacion con mayusculas y minusculas
        return switch (tipoVehiculo.toLowerCase()) {
            case "carro" -> pico ? CARRO_PICO : CARRO_NORMAL;
            case "moto" -> pico ? MOTO_PICO : MOTO_NORMAL;
            case "transporte publico" -> pico ? TRANSPORTE_PICO : TRANSPORTE_NORMAL;
            // a pie no depende del trafico, siempre tiene el mismo factor
            case "a pie" -> PIE_NORMAL;
            // vehiculo desconocido, factor neutro
            default -> 1.0;
        };
    }

    /**
     * Calcula el tiempo de viaje de una ruta segun el vehiculo seleccionado.
     * Multiplica el tiempo base de la ruta por el factor del vehiculo.
     * @param ruta ruta a calcular
     * @param tipoVehiculo tipo de vehiculo del usuario
     * @return tiempo de viaje en minutos
     */
    public static double calcular(Ruta ruta, String tipoVehiculo) {
        return ruta.getTiempo() * getFactor(tipoVehiculo);
    }
}
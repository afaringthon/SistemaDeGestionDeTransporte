package com.gestiontransporte.sistemadegestiondetransporte.algoritmos;

/**
 * Enum que representa los criterios de optimizacion disponibles
 * para calcular el camino optimo entre dos paradas.
 * Cada criterio determina que atributo de la ruta se minimiza.
 */
public enum Criterio {

    /** minimiza el tiempo de viaje en minutos, considerando tipo de vehiculo y hora pico */
    TIEMPO,

    /** minimiza la distancia recorrida en kilometros */
    DISTANCIA,

    /** minimiza el costo monetario del viaje en RD$ */
    COSTO,

    /** minimiza el numero de transbordos, siempre usa BFS sin importar el algoritmo seleccionado */
    TRASBORDO
}
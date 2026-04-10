# Análisis Asintótico y Complejidad Algorítmica

Este documento entrega el análisis asintótico (temporal y espacial) de las principales operaciones y algoritmos presentes en el proyecto `SistemaDeGestionDeTransporte`.

Contenido:
- Operaciones sobre la estructura `Grafo` (`modelo/Grafo.java`)
- Algoritmos implementados (BFS, Dijkstra, Bellman-Ford)

--------------------------------------------------------------------------------

1) Convenciones

- |V| = número de vértices (paradas)
- |E| = número de aristas (rutas)
- n = |V|
- m = |E|

Cuando sea necesario se usa `deg(u)` para indicar el grado de salida (número de rutas salientes) de una parada `u`.

--------------------------------------------------------------------------------

2) Operaciones sobre la estructura `Grafo` (archivo `modelo/Grafo.java`)

Observación sobre la representación: `Grafo` usa un `Map<Parada, List<Ruta>> adyacencia` donde la clave es un objeto `Parada` y el valor la lista de `Ruta` salientes.

a) Inserción de una parada (`agregarParada(Parada parada)`)

- Algoritmo observado: comprueba `parada == null`, luego itera sobre `adyacencia.keySet()` para verificar si existe una parada con el mismo `nombre` (comparación case-insensitive), luego hace `adyacencia.put(parada, new ArrayList<>())`.
- Tiempo: O(n) por la iteración sobre todas las paradas para evitar duplicados por nombre. Las operaciones de `HashMap.put` son O(1) promedio, pero la verificación domina.
- Espacio: O(1) adicional más la nueva entrada en el mapa que añadirá O(1) en estructura; en total el grafo crece en O(1) por inserción (el almacenamiento global del grafo es O(n + m)).

b) Inserción de una ruta (`agregarRuta(Ruta ruta)`)

- Algoritmo observado: valida origen/destino y valores, verifica existencia de `origen` y `destino` en el mapa con `containsKey` (O(1) promedio), obtiene la lista `rutasOrigen` y recorre esa lista para asegurarse de que no exista ya una ruta con ese destino.
- Tiempo: O(deg(origen)) para revisar rutas salientes desde `origen`. En el peor caso, deg(origen) = O(m) (si todas las aristas salen del mismo vértice), por lo que en peor caso O(m). Más útilmente: O(1 + deg(origen)).
- Espacio: O(1) adicional (la ruta añadida incrementa m en 1).

c) Eliminación de una ruta (`eliminarRuta(Ruta ruta)`)

- Algoritmo observado: comprueba existencia del origen en el mapa y recorre la lista de rutas del origen buscando la ruta exacta y la elimina por índice.
- Tiempo: O(deg(origen)) para buscar en la lista; en peor caso O(m).
- Espacio: O(1) adicional.

d) Consulta de vecinos de una parada (`getVecinosPorID(int id)`)

- Algoritmo observado: primero llama a `getParadaPorId(id)` que itera sobre `adyacencia.keySet()` buscando la parada con ese `id` — O(n). Luego devuelve una copia de la lista de rutas salientes: `new ArrayList<>(adyacencia.getOrDefault(parada, new ArrayList<>()))`, lo cual cuesta O(deg(parada)).
- Tiempo total: O(n + deg(parada)). En el peor caso deg(parada) puede ser O(m) y el costo total O(n + m).
- Espacio: se crea una nueva lista con las rutas salientes, tamaño O(deg(parada)).

e) Verificación de existencia de una arista (por ejemplo, comprobación dentro de `agregarRuta` o `modificarRuta` que busca si ya existe una ruta entre dos paradas)

- Algoritmo observado: recorre la lista de rutas salientes de un vértice y compara destinos: O(deg(u)).
- Tiempo: O(deg(u)) (peor caso O(m)).
- Espacio: O(1).

Nota práctica: muchas de las operaciones sufren por el uso de `Parada` como clave y por la operación lineal `getParadaPorId`. Si se añadiera un `Map<Integer, Parada>` (indexado por id) y/o un `Map<Integer, List<Ruta>>` para la adyacencia por id, se reducirían varias operaciones a O(1) promedio (por ejemplo, consulta por id y acceso a lista de adyacencia).

--------------------------------------------------------------------------------

3) Método utilitario del grafo

- `getTodasLasRutas()` — recorre `adyacencia.values()` y concatena todas las listas en un `resultado`. Tiempo O(n + m) y espacio extra O(m) para la lista devuelta.

--------------------------------------------------------------------------------

4) Análisis de los algoritmos (temporal y espacial)

En el proyecto están implementados `BFS`, `Dijkstra` y `BellmanFord`. A continuación la complejidad y una breve justificación. También se incluyen `Floyd-Warshall`, `Prim` y `Kruskal` por ser algoritmos clásicos para grafos (si no están implementados, se describe su complejidad y motivo).

a) BFS (Breadth-First Search)

- Representación: si se usa lista de adyacencia.
- Tiempo: O(n + m). Justificación: cada vértice se encola/desencola una vez (O(n)) y cada arista se explora una vez (O(m)).
- Espacio: O(n) adicional (cola + arreglo `visited`/marcas). Si se guarda el orden/parentales puede sumar O(n).
- Uso en este proyecto: inicialmente se consideró BFS para:
  - verificar si el grafo es conexo (alcanzabilidad entre paradas), y
  - obtener una ruta con el **menor número de paradas** (menor cantidad de vértices en el camino),
  siguiendo la idea que habíamos comentado con algunos compañeros.
  Finalmente se mantuvo el enfoque anterior (basado en los pesos de las rutas, por ejemplo tiempo/distancia/costo) para el cálculo principal de rutas, y BFS quedó como referencia teórica y para el análisis de conectividad.

b) Dijkstra (implementado en `algoritmos/Dijkstra.java`)

- Resumen de la implementación actual: construye un `Map<Integer, List<Ruta>> listaAdyacencia` (coste O(n + m) para construirlo a partir de `getTodasLasRutas()`), luego usa una `PriorityQueue<NodoDistancia>` para seleccionar el siguiente vértice con menor distancia.
- Tiempo: O((n + m) log n) en la implementación con `PriorityQueue`. Explicación:
  - Construcción de la lista de adyacencia: O(n + m).
  - En el bucle principal cada extracción de la cola cuesta O(log n) y puede ocurrir hasta O(n + numberOfRelaxes) veces; relajaciones de aristas pueden insertar elementos en la cola (cada vez se añade un posible nuevo par (v,dist)), en el peor caso cada arista puede generar una inserción → O(m) inserciones. Así, las operaciones de cola costarán O((n + m) log n). Simplificando: O((n + m) log n). Muchas fuentes lo escriben como O(m log n) cuando m domina.
- Espacio: O(n + m) para estructuras auxiliares (mapas `dist`, `prev`, `procesado` que son O(n) y la lista de adyacencia O(n + m) temporal). La `PriorityQueue` ocupa hasta O(n) simultáneamente en la práctica.

c) Bellman-Ford (archivo `algoritmos/BellmanFord.java` en el proyecto)

- Tiempo: O(n * m). Justificación: el algoritmo relaja todas las aristas repetidamente (n-1) veces; cada iteración recorre todas las aristas O(m), por tanto O(n*m).
- Espacio: O(n) (vectores `dist` y `prev`), más O(m) si se mantiene la lista de aristas.


--------------------------------------------------------------------------------

5) Complejidades resumidas (tabla compacta)

- `agregarParada`: Tiempo O(n), Espacio extra O(1) (nota: por nombre duplicado se recorre todo el conjunto)
- `agregarRuta`: Tiempo O(deg(origen)) ⇒ peor O(m), Espacio O(1)
- `eliminarRuta`: Tiempo O(deg(origen)) ⇒ peor O(m), Espacio O(1)
- `getVecinosPorID`: Tiempo O(n + deg(parada)) ⇒ peor O(n + m), Espacio O(deg(parada))
- `verificar existencia arista`: Tiempo O(deg(origen)) ⇒ peor O(m)

- `BFS`/`DFS`: Tiempo O(n + m), Espacio O(n)
- `Dijkstra` (con PriorityQueue): Tiempo O((n + m) log n) (a menudo escrito O(m log n)), Espacio O(n + m)
- `Bellman-Ford`: Tiempo O(n * m), Espacio O(n)

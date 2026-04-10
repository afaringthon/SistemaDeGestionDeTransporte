package com.gestiontransporte.sistemadegestiondetransporte.modelo;

import com.gestiontransporte.sistemadegestiondetransporte.algoritmos.BFS;
import com.gestiontransporte.sistemadegestiondetransporte.algoritmos.ResultadoCamino;

import java.util.*;

/**
 * Clase que representa el grafo dirigido del sistema de transporte.
 * Implementa la estructura de lista de adyacencia usando un mapa donde
 * cada parada apunta a la lista de rutas que salen de ella.
 */
public class Grafo {
    private final Map<Parada, List<Ruta>> adyacencia;

    /**
     * Constructor que inicializa el grafo con una lista de adyacencia vacia.
     */
    public Grafo() {
        adyacencia = new HashMap<>();
    }

    /**
     * Agrega una parada al grafo si no existe ya una con el mismo nombre.
     * @param parada parada a agregar
     * @return true si fue agregada, false si ya existe o es nula
     */
    public boolean agregarParada(Parada parada){
        if(parada == null) return false;

        // verifica nombre duplicado sin importar mayusculas
        for(Parada p : adyacencia.keySet()){
            if(p.getNombre().equalsIgnoreCase(parada.getNombre())) return false;
        }

        if(adyacencia.containsKey(parada)) return false;

        adyacencia.put(parada, new ArrayList<>());
        return true;
    }

    /**
     * Modifica el nombre de una parada existente verificando que el nuevo
     * nombre no este duplicado en el grafo.
     * @param idViejaParada id de la parada a modificar
     * @param nuevaParada parada con el nuevo nombre
     * @return true si fue modificada, false si no existe o el nombre esta duplicado
     */
    public boolean modificarParada(int idViejaParada, Parada nuevaParada){
        if(nuevaParada == null) return false;

        // verifica que el nuevo nombre no exista en otra parada
        for(Parada p : adyacencia.keySet()){
            if(p.getNombre().equalsIgnoreCase(nuevaParada.getNombre()) && p.getId() != idViejaParada){
                return false;
            }
        }

        for(Parada p : adyacencia.keySet()){
            if(p.getId() == idViejaParada){
                p.setNombre(nuevaParada.getNombre());
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina una parada del grafo y todas las rutas que llegan o salen de ella.
     * @param paradaAEliminar parada a eliminar
     * @return true si fue eliminada, false si no existe o es nula
     */
    public boolean eliminarParada(Parada paradaAEliminar){
        if(paradaAEliminar == null || !adyacencia.containsKey(paradaAEliminar)) return false;

        // elimina la parada y sus rutas salientes
        adyacencia.remove(paradaAEliminar);

        // elimina todas las rutas que llegaban a esta parada
        for(List<Ruta> rutas : adyacencia.values()){
            rutas.removeIf(ruta -> ruta.getDestino().getId() == paradaAEliminar.getId());
        }

        return true;
    }

    /**
     * Agrega una ruta dirigida al grafo entre dos paradas existentes.
     * Verifica que no exista ya una ruta entre las mismas paradas.
     * @param ruta ruta a agregar
     * @return true si fue agregada, false si ya existe o los datos son invalidos
     */
    public boolean agregarRuta(Ruta ruta) {
        if(ruta == null) return false;

        Parada origen = ruta.getOrigen();
        Parada destino = ruta.getDestino();

        if(origen == null || destino == null || origen.equals(destino)) return false;
        if(ruta.getDistancia() <= 0 || ruta.getTiempo() <= 0) return false;
        if(!adyacencia.containsKey(origen) || !adyacencia.containsKey(destino)) return false;

        List<Ruta> rutasOrigen = adyacencia.get(origen);

        // verifica que no exista ya una ruta entre estas paradas
        for(Ruta r : rutasOrigen){
            if(r.getDestino().equals(destino)) return false;
        }

        rutasOrigen.add(ruta);
        return true;
    }

    /**
     * Modifica los atributos de una ruta existente.
     * Verifica que la ruta vieja exista y que no haya duplicados con la nueva.
     * @param viejaRuta ruta a modificar
     * @param nuevaRuta ruta con los nuevos valores
     * @return true si fue modificada, false si no existe o hay duplicados
     */
    public boolean modificarRuta(Ruta viejaRuta, Ruta nuevaRuta){
        if(viejaRuta == null || nuevaRuta == null) return false;

        Parada viejoOrigen = viejaRuta.getOrigen();
        Parada viejoDestino = viejaRuta.getDestino();
        Parada nuevoOrigen = nuevaRuta.getOrigen();
        Parada nuevoDestino = nuevaRuta.getDestino();

        if(!adyacencia.containsKey(nuevoOrigen) || !adyacencia.containsKey(nuevoDestino) ||
                !adyacencia.containsKey(viejoOrigen) || !adyacencia.containsKey(viejoDestino)){
            return false;
        }

        List<Ruta> viejasRutasOrigen = adyacencia.get(viejoOrigen);

        // verifica que la ruta vieja exista
        if(!viejasRutasOrigen.contains(viejaRuta)) return false;

        List<Ruta> nuevasRutasOrigen = adyacencia.get(nuevoOrigen);

        // verifica que no exista otra ruta con el mismo destino
        for(Ruta r : nuevasRutasOrigen){
            if(r != viejaRuta && r.getDestino().equals(nuevoDestino)) return false;
        }

        // si el origen cambia, mueve la ruta a la nueva lista
        if(!viejoOrigen.equals(nuevoOrigen)){
            viejasRutasOrigen.remove(viejaRuta);
            nuevasRutasOrigen.add(viejaRuta);
        }

        viejaRuta.setOrigen(nuevoOrigen);
        viejaRuta.setDestino(nuevoDestino);
        viejaRuta.setDistancia(nuevaRuta.getDistancia());
        viejaRuta.setTiempo(nuevaRuta.getTiempo());
        viejaRuta.setCosto(nuevaRuta.getCosto());
        return true;
    }

    /**
     * Elimina una ruta dirigida del grafo.
     * @param ruta ruta a eliminar
     * @return true si fue eliminada, false si no existe o es nula
     */
    public boolean eliminarRuta(Ruta ruta){
        if(ruta == null) return false;

        Parada origen = ruta.getOrigen();
        if(origen == null || !adyacencia.containsKey(origen)) return false;

        List<Ruta> rutas = adyacencia.get(origen);

        for(int ind = 0; ind < rutas.size(); ind++){
            if(rutas.get(ind).equals(ruta)){
                rutas.remove(ind);
                return true;
            }
        }
        return false;
    }

    /**
     * Crea dos rutas dirigidas entre dos paradas (ida y vuelta) con los mismos atributos.
     * Si no se puede agregar alguna de las dos, no agrega ninguna.
     * @param o parada origen
     * @param d parada destino
     * @param dist distancia en km
     * @param tiem tiempo en minutos
     * @param costo costo monetario
     * @return true si ambas rutas fueron agregadas, false si alguna fallo
     */
    public boolean crearRutaDoble(Parada o, Parada d, double dist, double tiem, double costo) {
        if(o == null || d == null || o.equals(d)) return false;
        if(dist <= 0 || tiem <= 0 || costo < 0) return false;

        Ruta ida = new Ruta(o, d, dist, tiem, costo);
        Ruta vuelta = new Ruta(d, o, dist, tiem, costo);

        if(!agregarRuta(ida)) return false;

        // si la vuelta falla, quita la ida para mantener consistencia
        if(!agregarRuta(vuelta)){
            eliminarRuta(ida);
            return false;
        }
        return true;
    }

    /**
     * Elimina las dos rutas dirigidas entre dos paradas (ida y vuelta).
     * @param origen parada origen
     * @param destino parada destino
     * @return true si al menos una ruta fue eliminada
     */
    public boolean eliminarRutaDoble(Parada origen, Parada destino){
        if(origen == null || destino == null) return false;
        if(!adyacencia.containsKey(origen) || !adyacencia.containsKey(destino)) return false;

        boolean idaEliminado = false;
        boolean vueltaEliminado = false;

        List<Ruta> rutasOrigen = adyacencia.get(origen);
        for(int ind = 0; ind < rutasOrigen.size(); ind++){
            if(rutasOrigen.get(ind).getDestino().equals(destino)){
                rutasOrigen.remove(ind);
                idaEliminado = true;
                break;
            }
        }

        List<Ruta> rutasDestino = adyacencia.get(destino);
        for(int ind = 0; ind < rutasDestino.size(); ind++){
            if(rutasDestino.get(ind).getDestino().equals(origen)){
                rutasDestino.remove(ind);
                vueltaEliminado = true;
                break;
            }
        }

        return idaEliminado || vueltaEliminado;
    }

    /**
     * Devuelve todas las rutas del grafo en una lista.
     * Usado principalmente para la persistencia en JSON.
     * @return lista con todas las rutas del grafo
     */
    public List<Ruta> getTodasLasRutas(){
        List<Ruta> resultado = new ArrayList<>();
        for(List<Ruta> list : adyacencia.values()){
            resultado.addAll(list);
        }
        return resultado;
    }

    /**
     * Busca y devuelve una parada por su id.
     * @param id id de la parada a buscar
     * @return parada encontrada, o null si no existe
     */
    public Parada getParadaPorId(int id) {
        for (Parada p : adyacencia.keySet()) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    /**
     * Devuelve la lista de rutas salientes de una parada identificada por su id.
     * @param id id de la parada
     * @return lista de rutas salientes, o lista vacia si no existe
     */
    public List<Ruta> getVecinosPorID(int id){
        Parada parada = getParadaPorId(id);
        if(parada == null) return new ArrayList<>();
        return new ArrayList<>(adyacencia.getOrDefault(parada, new ArrayList<>()));
    }

    /**
     * Devuelve todas las paradas del grafo.
     * @return lista de paradas
     */
    public List<Parada> getParadas() {
        return new ArrayList<>(adyacencia.keySet());
    }

    /**
     * Devuelve las rutas salientes de una parada.
     * @param parada parada de origen
     * @return lista de rutas salientes
     */
    public List<Ruta> getRutasDesde(Parada parada) {
        return new ArrayList<>(adyacencia.getOrDefault(parada, Collections.emptyList()));
    }

    /**
     * Verifica si el grafo es conexo usando BFS.
     * Comprueba que desde cada parada se pueda llegar a todas las demas.
     * @return true si el grafo es conexo, false si no lo es
     */
    public boolean esConexo() {
        List<Parada> paradas = getParadas();
        if (paradas.isEmpty()) return true;

        BFS bfs = new BFS();

        for (Parada origen : paradas) {
            for (Parada destino : paradas) {
                if (origen.equals(destino)) continue;
                ResultadoCamino resultado = bfs.calcularBFS(this, origen.getId(), destino.getId());
                if (resultado.getCamino().isEmpty()) return false;
            }
        }
        return true;
    }
}
package main.planificacion;

import main.modelo.Proceso;
import java.util.List;

/**
 * Implementación del algoritmo Shortest Job First (SJF)
 */
public class SJF implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        // TODO: Implementar selección SJF
        return null;
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        // TODO: Implementar reordenamiento SJF
    }

    @Override
    public String getNombre() {
        return "SJF (Shortest Job First)";
    }
}
package main.planificacion;

import main.modelo.Proceso;
import java.util.List;

/**
 * Implementación del algoritmo First Come First Served (FCFS)
 */
public class FCFS implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        // TODO: Implementar selección FCFS
        return null;
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        // TODO: Implementar reordenamiento FCFS
    }

    @Override
    public String getNombre() {
        return "FCFS (First Come First Served)";
    }
}
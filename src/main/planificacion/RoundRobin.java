package main.planificacion;

import main.modelo.Proceso;
import java.util.List;

/**
 * Implementación del algoritmo Round Robin
 */
public class RoundRobin implements AlgoritmoPlanificacion {

    private int quantum;

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        // TODO: Implementar selección Round Robin
        return null;
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        // TODO: Implementar reordenamiento Round Robin
    }

    @Override
    public String getNombre() {
        return "Round Robin (Quantum: " + quantum + ")";
    }
}
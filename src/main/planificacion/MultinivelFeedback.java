package main.planificacion;

import main.modelo.Proceso;
import java.util.List;

/**
 * Implementación del algoritmo de planificación multinivel con
 * retroalimentación
 */
public class MultinivelFeedback implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        // TODO: Implementar selección multinivel con feedback
        return null;
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        // TODO: Implementar reordenamiento multinivel con feedback
    }

    @Override
    public String getNombre() {
        return "Planificación Multinivel con Retroalimentación";
    }
}
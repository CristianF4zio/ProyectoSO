package main.planificacion;

import main.modelo.Proceso;
import java.util.List;

/**
 * Implementación del algoritmo de planificación por prioridades
 */
public class Prioridad implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        // TODO: Implementar selección por prioridad
        return null;
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        // TODO: Implementar reordenamiento por prioridad
    }

    @Override
    public String getNombre() {
        return "Planificación por Prioridades";
    }
}
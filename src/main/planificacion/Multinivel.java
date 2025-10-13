package main.planificacion;

import main.modelo.Proceso;
import java.util.List;

/**
 * Implementación del algoritmo de planificación multinivel
 */
public class Multinivel implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        // TODO: Implementar selección multinivel
        return null;
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        // TODO: Implementar reordenamiento multinivel
    }

    @Override
    public String getNombre() {
        return "Planificación Multinivel";
    }
}
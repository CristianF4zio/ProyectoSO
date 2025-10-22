package main.planificacion;

import main.modelo.Proceso;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class Prioridad implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return null;
        }

        // Prioridad selecciona el proceso con menor número de prioridad (mayor
        // prioridad)
        return procesosListos.stream()
                .min(Comparator.comparing(Proceso::getPrioridad)
                        .thenComparing(Proceso::getTiempoCreacion)
                        .thenComparing(Proceso::getId))
                .orElse(null);
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return;
        }

        // Ordenar por prioridad (menor número = mayor prioridad)
        Collections.sort(procesosListos, new Comparator<Proceso>() {
            @Override
            public int compare(Proceso p1, Proceso p2) {
                // Primero por prioridad (menor número = mayor prioridad)
                int comparacionPrioridad = Integer.compare(p1.getPrioridad(), p2.getPrioridad());

                if (comparacionPrioridad != 0) {
                    return comparacionPrioridad;
                }

                // Si tienen la misma prioridad, por tiempo de creación (FCFS)
                int comparacionTiempo = p1.getTiempoCreacion().compareTo(p2.getTiempoCreacion());
                if (comparacionTiempo != 0) {
                    return comparacionTiempo;
                }

                // Si todo es igual, por ID
                return Integer.compare(p1.getId(), p2.getId());
            }
        });
    }

    @Override
    public String getNombre() {
        return "Planificación por Prioridades";
    }

    public String getDescripcion() {
        return "Planificación por Prioridades: Los procesos se ejecutan en orden de prioridad. " +
                "Menor número de prioridad = mayor prioridad. Puede causar inanición de " +
                "procesos de baja prioridad si llegan constantemente procesos de alta prioridad.";
    }

    public boolean isApropiativo() {
        return false;
    }

    public boolean tieneMayorPrioridad(Proceso proceso1, Proceso proceso2) {
        if (proceso1 == null || proceso2 == null) {
            return false;
        }

        // Menor número de prioridad = mayor prioridad
        return proceso1.getPrioridad() < proceso2.getPrioridad();
    }

    public int obtenerPrioridadMasAlta(List<Proceso> procesos) {
        if (procesos == null || procesos.isEmpty()) {
            return Integer.MAX_VALUE;
        }

        return procesos.stream()
                .mapToInt(Proceso::getPrioridad)
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    public long contarProcesosPorPrioridad(List<Proceso> procesos, int prioridad) {
        if (procesos == null) {
            return 0;
        }

        return procesos.stream()
                .filter(p -> p.getPrioridad() == prioridad)
                .count();
    }
}

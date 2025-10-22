package main.planificacion;

import main.modelo.Proceso;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class FCFS implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return null;
        }
        
        // FCFS selecciona el proceso que llegó primero (menor tiempo de creación)
        return procesosListos.stream()
                .min(Comparator.comparing(Proceso::getTiempoCreacion))
                .orElse(null);
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return;
        }
        
        // Ordenar por tiempo de creación (FCFS: primero en llegar, primero en ser servido)
        Collections.sort(procesosListos, new Comparator<Proceso>() {
            @Override
            public int compare(Proceso p1, Proceso p2) {
                // Si los tiempos son iguales, usar ID como criterio de desempate
                int comparacionTiempo = p1.getTiempoCreacion().compareTo(p2.getTiempoCreacion());
                if (comparacionTiempo == 0) {
                    return Integer.compare(p1.getId(), p2.getId());
                }
                return comparacionTiempo;
            }
        });
    }

    @Override
    public String getNombre() {
        return "FCFS (First Come First Served)";
    }

    public String getDescripcion() {
        return "First Come First Served: Los procesos se ejecutan en el orden de llegada. " +
               "Es un algoritmo no apropiativo que garantiza equidad pero puede tener " +
               "problemas de convoy (procesos largos bloquean a los cortos).";
    }

    public boolean isApropiativo() {
        return false;
    }
}
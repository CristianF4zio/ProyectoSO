package main.planificacion;

import main.modelo.Proceso;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class SJF implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return null;
        }

        // SJF selecciona el proceso con menor número de instrucciones restantes
        return procesosListos.stream()
                .min(Comparator.comparing(Proceso::getInstruccionesRestantes)
                        .thenComparing(Proceso::getTiempoCreacion)
                        .thenComparing(Proceso::getId))
                .orElse(null);
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return;
        }

        // Ordenar por número de instrucciones restantes (SJF: trabajo más corto
        // primero)
        Collections.sort(procesosListos, new Comparator<Proceso>() {
            @Override
            public int compare(Proceso p1, Proceso p2) {
                // Primero por instrucciones restantes
                int comparacionInstrucciones = Integer.compare(
                        p1.getInstruccionesRestantes(),
                        p2.getInstruccionesRestantes());

                if (comparacionInstrucciones != 0) {
                    return comparacionInstrucciones;
                }

                // Si tienen las mismas instrucciones, por tiempo de creación (FCFS)
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
        return "SJF (Shortest Job First)";
    }

    public String getDescripcion() {
        return "Shortest Job First: Los procesos se ejecutan en orden de menor a mayor " +
                "número de instrucciones. Minimiza el tiempo de espera promedio pero " +
                "puede causar inanición de procesos largos.";
    }

    public boolean isApropiativo() {
        return false;
    }

    public double calcularTiempoEsperaPromedio(List<Proceso> procesos) {
        if (procesos == null || procesos.isEmpty()) {
            return 0.0;
        }

        // Ordenar procesos por número de instrucciones (SJF)
        List<Proceso> procesosOrdenados = procesos.stream()
                .sorted(Comparator.comparing(Proceso::getNumInstrucciones))
                .collect(java.util.stream.Collectors.toList());

        long tiempoEsperaTotal = 0;
        long tiempoCompletado = 0;

        for (Proceso proceso : procesosOrdenados) {
            tiempoEsperaTotal += tiempoCompletado;
            tiempoCompletado += proceso.getNumInstrucciones();
        }

        return (double) tiempoEsperaTotal / procesos.size();
    }
}
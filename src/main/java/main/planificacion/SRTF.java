package main.planificacion;

import main.modelo.Proceso;
import java.util.List;

/**
 * Algoritmo de planificación Shortest Remaining Time First (SRTF)
 * Es la versión preemptiva de SJF
 * Selecciona el proceso con el menor tiempo restante de ejecución
 */
public class SRTF implements AlgoritmoPlanificacion {

    @Override
    public String getNombre() {
        return "SRTF (Shortest Remaining Time First)";
    }


    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return null;
        }

        // Seleccionar el proceso con menos tiempo restante
        Proceso procesoMenorTiempo = procesosListos.get(0);
        int menorTiempoRestante = calcularTiempoRestante(procesoMenorTiempo);

        for (int i = 1; i < procesosListos.size(); i++) {
            Proceso proceso = procesosListos.get(i);
            int tiempoRestante = calcularTiempoRestante(proceso);

            if (tiempoRestante < menorTiempoRestante) {
                procesoMenorTiempo = proceso;
                menorTiempoRestante = tiempoRestante;
            } else if (tiempoRestante == menorTiempoRestante) {
                // En caso de empate, usar FCFS (el que llegó primero)
                if (proceso.getId() < procesoMenorTiempo.getId()) {
                    procesoMenorTiempo = proceso;
                }
            }
        }

        return procesoMenorTiempo;
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return;
        }

        // Ordenar por tiempo restante (ascendente)
        procesosListos.sort((p1, p2) -> {
            int tiempo1 = calcularTiempoRestante(p1);
            int tiempo2 = calcularTiempoRestante(p2);

            if (tiempo1 != tiempo2) {
                return Integer.compare(tiempo1, tiempo2);
            }

            // En caso de empate, ordenar por ID (FCFS)
            return Integer.compare(p1.getId(), p2.getId());
        });
    }

    /**
     * Calcula el tiempo restante de un proceso
     * 
     * @param proceso Proceso a evaluar
     * @return Tiempo restante en ciclos
     */
    private int calcularTiempoRestante(Proceso proceso) {
        // Tiempo restante = Total de instrucciones - PC (Program Counter)
        return proceso.getNumInstrucciones() - proceso.getProgramCounter();
    }

    /**
     * Determina si se debe hacer preempción
     * 
     * @param procesoActual Proceso en ejecución
     * @param procesosListos Cola de procesos listos
     * @return true si se debe hacer preempción
     */
    public boolean debeHacerPreempcion(Proceso procesoActual, List<Proceso> procesosListos) {
        if (procesoActual == null || procesosListos == null || procesosListos.isEmpty()) {
            return false;
        }

        int tiempoRestanteActual = calcularTiempoRestante(procesoActual);

        // Verificar si hay algún proceso con menor tiempo restante
        for (Proceso proceso : procesosListos) {
            int tiempoRestante = calcularTiempoRestante(proceso);
            if (tiempoRestante < tiempoRestanteActual) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "SRTF [Preemptivo=true, Criterio=Tiempo Restante Más Corto]";
    }
}


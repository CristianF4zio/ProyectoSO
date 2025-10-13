package main.planificacion;

import main.modelo.Proceso;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Implementación del algoritmo de planificación multinivel con
 * retroalimentación
 * Los procesos se mueven entre colas según su comportamiento y tiempo de
 * ejecución
 */
public class MultinivelFeedback implements AlgoritmoPlanificacion {

    private List<List<Proceso>> colasPorNivel;
    private List<Integer> quantumsPorNivel;
    private int numNiveles;
    private int nivelActual;

    public MultinivelFeedback(int numNiveles) {
        this.numNiveles = numNiveles;
        this.nivelActual = 0;
        this.colasPorNivel = new ArrayList<>();
        this.quantumsPorNivel = new ArrayList<>();

        // Inicializar colas y quantums para cada nivel
        for (int i = 0; i < numNiveles; i++) {
            colasPorNivel.add(new ArrayList<>());
            // Quantum aumenta con el nivel (nivel 0 = quantum pequeño, nivel alto = quantum
            // grande)
            quantumsPorNivel.add((int) Math.pow(2, i) * 2); // 2, 4, 8, 16, ...
        }
    }

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return null;
        }

        // Organizar procesos en colas según su nivel actual
        organizarProcesosEnColas(procesosListos);

        // Buscar el primer proceso disponible desde el nivel más alto (0)
        for (int nivel = 0; nivel < numNiveles; nivel++) {
            List<Proceso> colaNivel = colasPorNivel.get(nivel);
            if (!colaNivel.isEmpty()) {
                // En cada nivel, usar Round Robin
                return colaNivel.get(0);
            }
        }

        return null;
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return;
        }

        // Organizar procesos en colas según su nivel
        organizarProcesosEnColas(procesosListos);

        // Limpiar la lista original
        procesosListos.clear();

        // Reconstruir la lista en orden de nivel (nivel 0 = mayor prioridad)
        for (int nivel = 0; nivel < numNiveles; nivel++) {
            List<Proceso> colaNivel = colasPorNivel.get(nivel);

            // Ordenar cada cola por tiempo de llegada (FCFS dentro del nivel)
            Collections.sort(colaNivel, Comparator.comparing(Proceso::getTiempoCreacion));

            // Agregar procesos de este nivel a la lista principal
            procesosListos.addAll(colaNivel);
        }
    }

    /**
     * Organiza los procesos en colas según su nivel actual
     * 
     * @param procesos Lista de procesos a organizar
     */
    private void organizarProcesosEnColas(List<Proceso> procesos) {
        // Limpiar todas las colas
        for (List<Proceso> cola : colasPorNivel) {
            cola.clear();
        }

        // Distribuir procesos en colas según su nivel actual
        for (Proceso proceso : procesos) {
            int nivel = obtenerNivelProceso(proceso);
            if (nivel >= 0 && nivel < numNiveles) {
                colasPorNivel.get(nivel).add(proceso);
            }
        }
    }

    /**
     * Obtiene el nivel actual de un proceso basado en su comportamiento
     * 
     * @param proceso Proceso del cual obtener el nivel
     * @return Nivel actual del proceso
     */
    private int obtenerNivelProceso(Proceso proceso) {
        // Si el proceso no tiene nivel asignado, empezar en nivel 0
        if (proceso.getPrioridad() < 0) {
            return 0;
        }

        // El nivel se almacena en el campo prioridad temporalmente
        // En un sistema real, se usaría un campo específico para el nivel
        int nivel = Math.min(proceso.getPrioridad(), numNiveles - 1);
        return Math.max(0, nivel);
    }

    /**
     * Mueve un proceso al siguiente nivel (menor prioridad)
     * 
     * @param proceso Proceso a degradar
     */
    public void degradarProceso(Proceso proceso) {
        int nivelActual = obtenerNivelProceso(proceso);
        int nuevoNivel = Math.min(nivelActual + 1, numNiveles - 1);

        // Actualizar el nivel del proceso (usando el campo prioridad temporalmente)
        proceso.setPrioridad(nuevoNivel);

        // Asignar nuevo quantum
        proceso.setQuantumRestante(quantumsPorNivel.get(nuevoNivel));
    }

    /**
     * Mueve un proceso al nivel anterior (mayor prioridad)
     * 
     * @param proceso Proceso a promover
     */
    public void promoverProceso(Proceso proceso) {
        int nivelActual = obtenerNivelProceso(proceso);
        int nuevoNivel = Math.max(nivelActual - 1, 0);

        // Actualizar el nivel del proceso
        proceso.setPrioridad(nuevoNivel);

        // Asignar nuevo quantum
        proceso.setQuantumRestante(quantumsPorNivel.get(nuevoNivel));
    }

    /**
     * Verifica si un proceso ha terminado su quantum en su nivel actual
     * 
     * @param proceso Proceso a verificar
     * @return true si ha terminado su quantum
     */
    public boolean haTerminadoQuantum(Proceso proceso) {
        return proceso.getQuantumRestante() <= 0;
    }

    /**
     * Asigna el quantum correspondiente al nivel del proceso
     * 
     * @param proceso Proceso al cual asignar quantum
     */
    public void asignarQuantum(Proceso proceso) {
        int nivel = obtenerNivelProceso(proceso);
        proceso.setQuantumRestante(quantumsPorNivel.get(nivel));
    }

    /**
     * Reduce el quantum restante de un proceso
     * 
     * @param proceso Proceso del cual reducir quantum
     */
    public void reducirQuantum(Proceso proceso) {
        if (proceso.getQuantumRestante() > 0) {
            proceso.setQuantumRestante(proceso.getQuantumRestante() - 1);
        }
    }

    /**
     * Obtiene el quantum para un nivel específico
     * 
     * @param nivel Nivel de prioridad
     * @return Quantum para ese nivel
     */
    public int getQuantumNivel(int nivel) {
        if (nivel < 0 || nivel >= numNiveles) {
            return 1;
        }
        return quantumsPorNivel.get(nivel);
    }

    /**
     * Obtiene el número de procesos en un nivel específico
     * 
     * @param nivel Nivel de prioridad
     * @return Número de procesos en ese nivel
     */
    public int getProcesosEnNivel(int nivel) {
        if (nivel < 0 || nivel >= numNiveles) {
            return 0;
        }
        return colasPorNivel.get(nivel).size();
    }

    /**
     * Obtiene todos los procesos de un nivel específico
     * 
     * @param nivel Nivel de prioridad
     * @return Lista de procesos en ese nivel
     */
    public List<Proceso> getProcesosDelNivel(int nivel) {
        if (nivel < 0 || nivel >= numNiveles) {
            return new ArrayList<>();
        }
        return new ArrayList<>(colasPorNivel.get(nivel));
    }

    @Override
    public String getNombre() {
        return "Planificación Multinivel con Retroalimentación (" + numNiveles + " niveles)";
    }

    /**
     * Obtiene una descripción detallada del algoritmo
     * 
     * @return Descripción del algoritmo multinivel con feedback
     */
    public String getDescripcion() {
        return "Planificación Multinivel con Retroalimentación: Los procesos se organizan en " +
                numNiveles + " colas con quantums crecientes. Los procesos que terminan su " +
                "quantum bajan de nivel, y los que completan I/O pueden subir de nivel.";
    }

    /**
     * Verifica si el algoritmo es apropiativo
     * 
     * @return true - Multinivel con feedback es apropiativo
     */
    public boolean isApropiativo() {
        return true;
    }

    /**
     * Obtiene el número de niveles configurados
     * 
     * @return Número de niveles
     */
    public int getNumNiveles() {
        return numNiveles;
    }

    /**
     * Obtiene estadísticas de distribución por niveles
     * 
     * @return Array con el número de procesos por nivel
     */
    public int[] getEstadisticasNiveles() {
        int[] estadisticas = new int[numNiveles];
        for (int i = 0; i < numNiveles; i++) {
            estadisticas[i] = colasPorNivel.get(i).size();
        }
        return estadisticas;
    }

    /**
     * Obtiene los quantums configurados para cada nivel
     * 
     * @return Array con los quantums por nivel
     */
    public int[] getQuantumsNiveles() {
        int[] quantums = new int[numNiveles];
        for (int i = 0; i < numNiveles; i++) {
            quantums[i] = quantumsPorNivel.get(i);
        }
        return quantums;
    }
}

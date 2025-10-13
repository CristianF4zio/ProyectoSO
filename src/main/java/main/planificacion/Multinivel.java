package main.planificacion;

import main.modelo.Proceso;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Implementación del algoritmo de planificación multinivel
 * Los procesos se organizan en múltiples colas según su prioridad
 */
public class Multinivel implements AlgoritmoPlanificacion {

    private List<List<Proceso>> colasPorPrioridad;
    private int numNiveles;
    private int nivelActual;

    public Multinivel(int numNiveles) {
        this.numNiveles = numNiveles;
        this.nivelActual = 0;
        this.colasPorPrioridad = new ArrayList<>();

        // Inicializar colas para cada nivel de prioridad
        for (int i = 0; i < numNiveles; i++) {
            colasPorPrioridad.add(new ArrayList<>());
        }
    }

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return null;
        }

        // Organizar procesos en colas por prioridad
        organizarProcesosEnColas(procesosListos);

        // Buscar el primer proceso disponible desde el nivel más alto
        for (int nivel = 0; nivel < numNiveles; nivel++) {
            List<Proceso> colaNivel = colasPorPrioridad.get(nivel);
            if (!colaNivel.isEmpty()) {
                // En cada nivel, usar FCFS
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

        // Organizar procesos en colas por prioridad
        organizarProcesosEnColas(procesosListos);

        // Limpiar la lista original
        procesosListos.clear();

        // Reconstruir la lista en orden de prioridad (nivel 0 = mayor prioridad)
        for (int nivel = 0; nivel < numNiveles; nivel++) {
            List<Proceso> colaNivel = colasPorPrioridad.get(nivel);

            // Ordenar cada cola por tiempo de creación (FCFS dentro del nivel)
            Collections.sort(colaNivel, Comparator.comparing(Proceso::getTiempoCreacion));

            // Agregar procesos de este nivel a la lista principal
            procesosListos.addAll(colaNivel);
        }
    }

    /**
     * Organiza los procesos en colas según su prioridad
     * 
     * @param procesos Lista de procesos a organizar
     */
    private void organizarProcesosEnColas(List<Proceso> procesos) {
        // Limpiar todas las colas
        for (List<Proceso> cola : colasPorPrioridad) {
            cola.clear();
        }

        // Distribuir procesos en colas según su prioridad
        for (Proceso proceso : procesos) {
            int nivel = calcularNivel(proceso.getPrioridad());
            if (nivel >= 0 && nivel < numNiveles) {
                colasPorPrioridad.get(nivel).add(proceso);
            }
        }
    }

    /**
     * Calcula el nivel de cola basado en la prioridad del proceso
     * 
     * @param prioridad Prioridad del proceso
     * @return Nivel de cola (0 = mayor prioridad)
     */
    private int calcularNivel(int prioridad) {
        // Mapear prioridad a nivel de cola
        // Prioridad 1-3 = Nivel 0 (mayor prioridad)
        // Prioridad 4-6 = Nivel 1
        // Prioridad 7-9 = Nivel 2
        // etc.

        if (prioridad <= 0) {
            return 0; // Prioridad inválida va al nivel más alto
        }

        int nivel = (prioridad - 1) / 3;
        return Math.min(nivel, numNiveles - 1);
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
        return colasPorPrioridad.get(nivel).size();
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
        return new ArrayList<>(colasPorPrioridad.get(nivel));
    }

    /**
     * Obtiene el nivel con mayor prioridad que tiene procesos
     * 
     * @return Nivel con mayor prioridad, o -1 si no hay procesos
     */
    public int getNivelConProcesos() {
        for (int nivel = 0; nivel < numNiveles; nivel++) {
            if (!colasPorPrioridad.get(nivel).isEmpty()) {
                return nivel;
            }
        }
        return -1;
    }

    @Override
    public String getNombre() {
        return "Planificación Multinivel (" + numNiveles + " niveles)";
    }

    /**
     * Obtiene una descripción detallada del algoritmo
     * 
     * @return Descripción del algoritmo multinivel
     */
    public String getDescripcion() {
        return "Planificación Multinivel: Los procesos se organizan en " + numNiveles +
                " colas según su prioridad. Se ejecutan primero los procesos de mayor " +
                "prioridad (nivel 0), y dentro de cada nivel se usa FCFS.";
    }

    /**
     * Verifica si el algoritmo es apropiativo
     * 
     * @return false - Multinivel básico es no apropiativo
     */
    public boolean isApropiativo() {
        return false;
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
            estadisticas[i] = colasPorPrioridad.get(i).size();
        }
        return estadisticas;
    }
}

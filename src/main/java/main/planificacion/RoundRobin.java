package main.planificacion;

import main.modelo.Proceso;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

/**
 * Implementación del algoritmo Round Robin
 * Los procesos se ejecutan por un tiempo fijo (quantum) y luego pasan al final de la cola
 */
public class RoundRobin implements AlgoritmoPlanificacion {

    private int quantum;
    private int indiceActual;

    public RoundRobin(int quantum) {
        this.quantum = quantum;
        this.indiceActual = 0;
    }

    @Override
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return null;
        }
        
        // Round Robin selecciona el proceso en la posición actual
        if (indiceActual >= procesosListos.size()) {
            indiceActual = 0; // Volver al inicio si llegamos al final
        }
        
        return procesosListos.get(indiceActual);
    }

    @Override
    public void reordenarCola(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty()) {
            return;
        }
        
        // Round Robin mantiene el orden FCFS pero con rotación
        // No necesita reordenar, solo avanza el índice
        // El reordenamiento se hace cuando un proceso termina su quantum
    }
    
    /**
     * Avanza al siguiente proceso en la cola (para Round Robin)
     */
    public void avanzarSiguiente() {
        indiceActual++;
    }
    
    /**
     * Resetea el índice actual (cuando se agregan/eliminan procesos)
     */
    public void resetearIndice() {
        indiceActual = 0;
    }
    
    /**
     * Mueve el proceso actual al final de la cola (cuando termina su quantum)
     * @param procesosListos Lista de procesos
     */
    public void moverAlFinal(List<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.isEmpty() || indiceActual >= procesosListos.size()) {
            return;
        }
        
        // Mover el proceso actual al final
        Proceso procesoActual = procesosListos.remove(indiceActual);
        procesosListos.add(procesoActual);
        
        // El índice se mantiene en la misma posición (ahora apunta al siguiente proceso)
        if (indiceActual >= procesosListos.size()) {
            indiceActual = 0;
        }
    }

    @Override
    public String getNombre() {
        return "Round Robin (Quantum: " + quantum + ")";
    }
    
    /**
     * Obtiene el valor del quantum
     * @return Valor del quantum
     */
    public int getQuantum() {
        return quantum;
    }
    
    /**
     * Establece un nuevo valor para el quantum
     * @param quantum Nuevo valor del quantum
     */
    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }
    
    /**
     * Obtiene una descripción detallada del algoritmo
     * @return Descripción del algoritmo Round Robin
     */
    public String getDescripcion() {
        return "Round Robin: Los procesos se ejecutan por un tiempo fijo (quantum) y luego " +
               "pasan al final de la cola. Es un algoritmo apropiativo que garantiza " +
               "equidad y evita la inanición.";
    }
    
    /**
     * Verifica si el algoritmo es apropiativo
     * @return true - Round Robin es apropiativo
     */
    public boolean isApropiativo() {
        return true;
    }
    
    /**
     * Verifica si un proceso ha terminado su quantum
     * @param proceso Proceso a verificar
     * @return true si ha terminado su quantum
     */
    public boolean haTerminadoQuantum(Proceso proceso) {
        if (proceso == null) {
            return false;
        }
        
        // Un proceso termina su quantum cuando ha ejecutado 'quantum' instrucciones
        // desde que comenzó su turno actual
        return proceso.getQuantumRestante() <= 0;
    }
    
    /**
     * Asigna un nuevo quantum a un proceso
     * @param proceso Proceso al que asignar el quantum
     */
    public void asignarQuantum(Proceso proceso) {
        if (proceso != null) {
            proceso.setQuantumRestante(quantum);
        }
    }
    
    /**
     * Reduce el quantum restante de un proceso
     * @param proceso Proceso del cual reducir el quantum
     */
    public void reducirQuantum(Proceso proceso) {
        if (proceso != null && proceso.getQuantumRestante() > 0) {
            proceso.setQuantumRestante(proceso.getQuantumRestante() - 1);
        }
    }
}
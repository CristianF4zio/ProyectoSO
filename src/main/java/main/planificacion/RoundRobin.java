package main.planificacion;

import main.modelo.Proceso;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

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

    public void avanzarSiguiente() {
        indiceActual++;
    }

    public void resetearIndice() {
        indiceActual = 0;
    }

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

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public String getDescripcion() {
        return "Round Robin: Los procesos se ejecutan por un tiempo fijo (quantum) y luego " +
               "pasan al final de la cola. Es un algoritmo apropiativo que garantiza " +
               "equidad y evita la inanición.";
    }

    public boolean isApropiativo() {
        return true;
    }

    public boolean haTerminadoQuantum(Proceso proceso) {
        if (proceso == null) {
            return false;
        }
        
        // Un proceso termina su quantum cuando ha ejecutado 'quantum' instrucciones
        // desde que comenzó su turno actual
        return proceso.getQuantumRestante() <= 0;
    }

    public void asignarQuantum(Proceso proceso) {
        if (proceso != null) {
            proceso.setQuantumRestante(quantum);
        }
    }

    public void reducirQuantum(Proceso proceso) {
        if (proceso != null && proceso.getQuantumRestante() > 0) {
            proceso.setQuantumRestante(proceso.getQuantumRestante() - 1);
        }
    }
}
package main.planificacion;

import main.modelo.Proceso;
import main.estructuras.ListaSimple;

public class RoundRobin implements AlgoritmoPlanificacion {
    private int quantum;

    public RoundRobin() {
        this.quantum = 3; // Quantum por defecto
    }

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public Proceso seleccionarSiguiente(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return null;
        }

        // Round Robin selecciona el primer proceso de la cola
        return procesosListos.obtener(0);
    }

    @Override
    public void reordenarCola(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return;
        }

        // Round Robin mantiene el orden FIFO
        // No se reordena la cola
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

    public boolean haTerminadoQuantum(Proceso proceso) {
        return proceso.getInstruccionesEjecutadas() % quantum == 0;
    }

    public void reducirQuantum(Proceso proceso) {
        // En Round Robin, el quantum se maneja automáticamente
    }
}

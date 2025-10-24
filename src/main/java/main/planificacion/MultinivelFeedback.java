package main.planificacion;

import main.modelo.Proceso;
import main.estructuras.ListaSimple;
import main.estructuras.Ordenador;

public class MultinivelFeedback implements AlgoritmoPlanificacion {
    private int numNiveles;
    private ListaSimple<ListaSimple<Proceso>> colasPorNivel;
    private ListaSimple<Integer> quantumsPorNivel;

    public MultinivelFeedback() {
        this.numNiveles = 3;
        this.colasPorNivel = new ListaSimple<>();
        this.quantumsPorNivel = new ListaSimple<>();

        // Inicializar colas por nivel
        for (int i = 0; i < numNiveles; i++) {
            colasPorNivel.agregar(new ListaSimple<>());
        }

        // Quantum por nivel: 4, 8, 16
        quantumsPorNivel.agregar(4);
        quantumsPorNivel.agregar(8);
        quantumsPorNivel.agregar(16);
    }

    public MultinivelFeedback(int numNiveles) {
        this.numNiveles = numNiveles;
        this.colasPorNivel = new ListaSimple<>();
        this.quantumsPorNivel = new ListaSimple<>();

        // Inicializar colas por nivel
        for (int i = 0; i < numNiveles; i++) {
            colasPorNivel.agregar(new ListaSimple<>());
        }

        // Quantum por nivel: 4, 8, 16, etc.
        for (int i = 0; i < numNiveles; i++) {
            quantumsPorNivel.agregar(4 * (1 << i)); // 4, 8, 16, 32, ...
        }
    }

    @Override
    public Proceso seleccionarSiguiente(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return null;
        }

        // Distribuir procesos por niveles según comportamiento
        distribuirProcesosPorNivel(procesosListos);

        // Seleccionar del nivel de mayor prioridad que tenga procesos
        for (int nivel = 0; nivel < numNiveles; nivel++) {
            ListaSimple<Proceso> colaNivel = colasPorNivel.obtener(nivel);
            if (!colaNivel.estaVacia()) {
                // Ordenar por tiempo de llegada dentro del nivel
                Ordenador.ordenarPorTiempoLlegada(colaNivel);
                return colaNivel.obtener(0); // Retornar el primer proceso del nivel
            }
        }

        return null;
    }

    @Override
    public void reordenarCola(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return;
        }

        // Limpiar todas las colas
        for (int i = 0; i < numNiveles; i++) {
            colasPorNivel.obtener(i).limpiar();
        }

        // Distribuir procesos por niveles
        distribuirProcesosPorNivel(procesosListos);

        // Limpiar la lista original
        procesosListos.limpiar();

        // Reconstruir la lista ordenada por niveles
        for (int nivel = 0; nivel < numNiveles; nivel++) {
            ListaSimple<Proceso> colaNivel = colasPorNivel.obtener(nivel);
            for (int i = 0; i < colaNivel.tamaño(); i++) {
                procesosListos.agregar(colaNivel.obtener(i));
            }
        }
    }

    private void distribuirProcesosPorNivel(ListaSimple<Proceso> procesos) {
        for (int i = 0; i < procesos.tamaño(); i++) {
            Proceso proceso = procesos.obtener(i);
            int nivel = determinarNivelPorComportamiento(proceso);
            colasPorNivel.obtener(nivel).agregar(proceso);
        }
    }

    private int determinarNivelPorComportamiento(Proceso proceso) {
        // Procesos I/O bound van a niveles altos (más prioridad)
        // Procesos CPU bound van a niveles bajos (menos prioridad)
        if (proceso.getTipo().toString().equals("IO_BOUND")) {
            return 0; // Nivel más alto
        } else {
            // CPU bound: determinar por tiempo de CPU usado
            int tiempoCPUUsado = proceso.getInstruccionesEjecutadas();
            if (tiempoCPUUsado < 5)
                return 0;
            if (tiempoCPUUsado < 10)
                return 1;
            return 2; // Nivel más bajo
        }
    }

    @Override
    public String getNombre() {
        return "Multinivel Feedback (" + numNiveles + " niveles)";
    }

    public ListaSimple<Proceso> getProcesosEnNivel(int nivel) {
        if (nivel < 0 || nivel >= numNiveles) {
            return new ListaSimple<>();
        }
        return colasPorNivel.obtener(nivel);
    }

    public int getQuantumParaNivel(int nivel) {
        if (nivel < 0 || nivel >= numNiveles) {
            return 4; // Quantum por defecto
        }
        return quantumsPorNivel.obtener(nivel);
    }
}

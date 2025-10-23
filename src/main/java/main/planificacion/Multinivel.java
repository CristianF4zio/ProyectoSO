package main.planificacion;

import main.modelo.Proceso;
import main.estructuras.ListaSimple;
import main.estructuras.Ordenador;

public class Multinivel implements AlgoritmoPlanificacion {
    private int numNiveles;
    private ListaSimple<ListaSimple<Proceso>> colasPorPrioridad;

    public Multinivel() {
        this.numNiveles = 3;
        this.colasPorPrioridad = new ListaSimple<>();

        // Inicializar colas por nivel
        for (int i = 0; i < numNiveles; i++) {
            colasPorPrioridad.agregar(new ListaSimple<>());
        }
    }

    public Multinivel(int numNiveles) {
        this.numNiveles = numNiveles;
        this.colasPorPrioridad = new ListaSimple<>();

        // Inicializar colas por nivel
        for (int i = 0; i < numNiveles; i++) {
            colasPorPrioridad.agregar(new ListaSimple<>());
        }
    }

    @Override
    public Proceso seleccionarSiguiente(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return null;
        }

        // Distribuir procesos por niveles según prioridad
        distribuirProcesosPorNivel(procesosListos);

        // Seleccionar del nivel de mayor prioridad que tenga procesos
        for (int nivel = 0; nivel < numNiveles; nivel++) {
            ListaSimple<Proceso> colaNivel = colasPorPrioridad.obtener(nivel);
            if (!colaNivel.estaVacia()) {
                // Ordenar por tiempo de llegada dentro del nivel
                Ordenador.ordenarPorTiempoLlegada(colaNivel);

                // Agregar todos los procesos de este nivel a la lista principal
                for (int i = 0; i < colaNivel.tamaño(); i++) {
                    procesosListos.agregar(colaNivel.obtener(i));
                }
                break; // Solo procesar el primer nivel con procesos
            }
        }

        return procesosListos.estaVacia() ? null : procesosListos.obtener(0);
    }

    @Override
    public void reordenarCola(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return;
        }

        // Limpiar todas las colas
        for (int i = 0; i < numNiveles; i++) {
            colasPorPrioridad.obtener(i).limpiar();
        }

        // Distribuir procesos por niveles
        distribuirProcesosPorNivel(procesosListos);

        // Limpiar la lista original
        procesosListos.limpiar();

        // Reconstruir la lista ordenada por niveles
        for (int nivel = 0; nivel < numNiveles; nivel++) {
            ListaSimple<Proceso> colaNivel = colasPorPrioridad.obtener(nivel);
            for (int i = 0; i < colaNivel.tamaño(); i++) {
                procesosListos.agregar(colaNivel.obtener(i));
            }
        }
    }

    private void distribuirProcesosPorNivel(ListaSimple<Proceso> procesos) {
        for (int i = 0; i < procesos.tamaño(); i++) {
            Proceso proceso = procesos.obtener(i);
            int nivel = determinarNivel(proceso.getPrioridad());
            colasPorPrioridad.obtener(nivel).agregar(proceso);
        }
    }

    private int determinarNivel(int prioridad) {
        // Prioridad 1-3: nivel 0 (más alta)
        // Prioridad 4-6: nivel 1 (media)
        // Prioridad 7+: nivel 2 (más baja)
        if (prioridad <= 3)
            return 0;
        if (prioridad <= 6)
            return 1;
        return 2;
    }

    @Override
    public String getNombre() {
        return "Multinivel (" + numNiveles + " niveles)";
    }

    public ListaSimple<Proceso> getProcesosEnNivel(int nivel) {
        if (nivel < 0 || nivel >= numNiveles) {
            return new ListaSimple<>();
        }
        return colasPorPrioridad.obtener(nivel);
    }
}
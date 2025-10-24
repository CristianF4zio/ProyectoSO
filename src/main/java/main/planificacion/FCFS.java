package main.planificacion;

import main.modelo.Proceso;
import main.estructuras.ListaSimple;
import main.estructuras.Ordenador;

public class FCFS implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return null;
        }

        // FCFS selecciona el proceso que llegó primero (menor ID)
        Proceso primero = procesosListos.obtener(0);
        for (int i = 1; i < procesosListos.tamaño(); i++) {
            Proceso proceso = procesosListos.obtener(i);
            if (proceso.getId() < primero.getId()) {
                primero = proceso;
            }
        }
        return primero;
    }

    @Override
    public void reordenarCola(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return;
        }

        // Ordenar por ID (FCFS: primero en llegar, primero en ser servido)
        // Crear una copia para no modificar la lista original
        ListaSimple<Proceso> copia = new ListaSimple<>();
        for (int i = 0; i < procesosListos.tamaño(); i++) {
            copia.agregar(procesosListos.obtener(i));
        }
        
        Ordenador.ordenarPorTiempoLlegada(copia);
        
        // Reemplazar la lista original con la ordenada
        procesosListos.limpiar();
        for (int i = 0; i < copia.tamaño(); i++) {
            procesosListos.agregar(copia.obtener(i));
        }
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
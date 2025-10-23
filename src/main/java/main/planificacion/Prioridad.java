package main.planificacion;

import main.modelo.Proceso;
import main.estructuras.ListaSimple;
import main.estructuras.Ordenador;

public class Prioridad implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return null;
        }

        // Prioridad selecciona el proceso con mayor prioridad (menor número)
        Proceso mayorPrioridad = procesosListos.obtener(0);
        for (int i = 1; i < procesosListos.tamaño(); i++) {
            Proceso proceso = procesosListos.obtener(i);
            if (proceso.getPrioridad() < mayorPrioridad.getPrioridad()) {
                mayorPrioridad = proceso;
            } else if (proceso.getPrioridad() == mayorPrioridad.getPrioridad()) {
                if (proceso.getId() < mayorPrioridad.getId()) {
                    mayorPrioridad = proceso;
                }
            }
        }
        return mayorPrioridad;
    }

    @Override
    public void reordenarCola(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return;
        }

        // Ordenar por prioridad (menor número = mayor prioridad)
        Ordenador.ordenarPorPrioridad(procesosListos);
    }

    @Override
    public String getNombre() {
        return "Prioridad";
    }

    public boolean tieneMayorPrioridad(Proceso p1, Proceso p2) {
        return p1.getPrioridad() < p2.getPrioridad();
    }

    public int obtenerPrioridadMasAlta(ListaSimple<Proceso> procesos) {
        if (procesos == null || procesos.estaVacia()) {
            return Integer.MAX_VALUE;
        }

        int prioridadMasAlta = procesos.obtener(0).getPrioridad();
        for (int i = 1; i < procesos.tamaño(); i++) {
            int prioridad = procesos.obtener(i).getPrioridad();
            if (prioridad < prioridadMasAlta) {
                prioridadMasAlta = prioridad;
            }
        }
        return prioridadMasAlta;
    }
}
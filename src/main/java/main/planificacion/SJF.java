package main.planificacion;

import main.modelo.Proceso;
import main.estructuras.ListaSimple;
import main.estructuras.Ordenador;

public class SJF implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return null;
        }

        // SJF selecciona el proceso con menor número de instrucciones restantes
        Proceso menor = procesosListos.obtener(0);
        for (int i = 1; i < procesosListos.tamaño(); i++) {
            Proceso proceso = procesosListos.obtener(i);
            if (proceso.getInstruccionesRestantes() < menor.getInstruccionesRestantes()) {
                menor = proceso;
            } else if (proceso.getInstruccionesRestantes() == menor.getInstruccionesRestantes()) {
                if (proceso.getId() < menor.getId()) {
                    menor = proceso;
                }
            }
        }
        return menor;
    }

    @Override
    public void reordenarCola(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return;
        }

        // Ordenar por número de instrucciones restantes (SJF: trabajo más corto
        // primero)
        Ordenador.ordenarPorInstrucciones(procesosListos);
    }

    @Override
    public String getNombre() {
        return "SJF (Shortest Job First)";
    }

    public String getDescripcion() {
        return "Shortest Job First: Los procesos se ejecutan en orden de menor a mayor " +
                "número de instrucciones. Minimiza el tiempo de espera promedio pero " +
                "puede causar inanición de procesos largos.";
    }

    public boolean isApropiativo() {
        return false;
    }

    public double calcularTiempoEsperaPromedio(ListaSimple<Proceso> procesos) {
        if (procesos == null || procesos.estaVacia()) {
            return 0.0;
        }

        // Ordenar procesos por número de instrucciones (SJF)
        ListaSimple<Proceso> procesosOrdenados = new ListaSimple<>();
        for (int i = 0; i < procesos.tamaño(); i++) {
            procesosOrdenados.agregar(procesos.obtener(i));
        }
        Ordenador.ordenarPorInstrucciones(procesosOrdenados);

        long tiempoEsperaTotal = 0;
        long tiempoCompletado = 0;

        for (int i = 0; i < procesosOrdenados.tamaño(); i++) {
            Proceso proceso = procesosOrdenados.obtener(i);
            tiempoEsperaTotal += tiempoCompletado;
            tiempoCompletado += proceso.getNumInstrucciones();
        }

        return (double) tiempoEsperaTotal / procesos.tamaño();
    }
}
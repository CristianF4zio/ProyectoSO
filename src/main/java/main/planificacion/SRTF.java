package main.planificacion;

import main.modelo.Proceso;
import main.estructuras.ListaSimple;
import main.estructuras.Ordenador;

public class SRTF implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return null;
        }

        // SRTF selecciona el proceso con menor tiempo restante
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

        // Ordenar por tiempo restante (SRTF: tiempo restante más corto primero)
        Ordenador.ordenarPorInstrucciones(procesosListos);
    }

    @Override
    public String getNombre() {
        return "SRTF (Shortest Remaining Time First)";
    }
}
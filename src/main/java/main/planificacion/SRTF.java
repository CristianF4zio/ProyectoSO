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
        // Crear una copia para no modificar la lista original
        ListaSimple<Proceso> copia = new ListaSimple<>();
        for (int i = 0; i < procesosListos.tamaño(); i++) {
            copia.agregar(procesosListos.obtener(i));
        }
        
        Ordenador.ordenarPorInstrucciones(copia);
        
        // Reemplazar la lista original con la ordenada
        procesosListos.limpiar();
        for (int i = 0; i < copia.tamaño(); i++) {
            procesosListos.agregar(copia.obtener(i));
        }
    }

    @Override
    public String getNombre() {
        return "SRTF (Shortest Remaining Time First)";
    }
}
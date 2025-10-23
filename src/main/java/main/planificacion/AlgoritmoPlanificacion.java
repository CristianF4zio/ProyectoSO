package main.planificacion;

import main.modelo.Proceso;
import main.estructuras.ListaSimple;

public interface AlgoritmoPlanificacion {

    Proceso seleccionarSiguiente(ListaSimple<Proceso> procesosListos);

    void reordenarCola(ListaSimple<Proceso> procesosListos);

    String getNombre();
}

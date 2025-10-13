package main.gestor;

import main.modelo.Proceso;
import main.planificacion.AlgoritmoPlanificacion;
import java.util.List;

/**
 * Clase que gestiona la planificación de procesos
 */
public class Planificador {

    private AlgoritmoPlanificacion algoritmoActual;

    /**
     * Cambia el algoritmo de planificación
     * 
     * @param algoritmo Nuevo algoritmo a usar
     */
    public void cambiarAlgoritmo(AlgoritmoPlanificacion algoritmo) {
        // TODO: Implementar cambio de algoritmo
    }

    /**
     * Selecciona el siguiente proceso a ejecutar
     * 
     * @param procesosListos Lista de procesos listos
     * @return Proceso seleccionado
     */
    public Proceso seleccionarSiguiente(List<Proceso> procesosListos) {
        // TODO: Implementar selección de proceso
        return null;
    }

    /**
     * Reordena la cola de procesos listos
     * 
     * @param procesosListos Lista a reordenar
     */
    public void reordenarCola(List<Proceso> procesosListos) {
        // TODO: Implementar reordenamiento de cola
    }
}
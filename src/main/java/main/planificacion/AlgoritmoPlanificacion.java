package main.planificacion;

import main.modelo.Proceso;
import java.util.List;

/**
 * Interfaz que define el contrato para los algoritmos de planificación
 */
public interface AlgoritmoPlanificacion {

    /**
     * Selecciona el siguiente proceso a ejecutar de la lista de procesos listos
     * 
     * @param procesosListos Lista de procesos en estado LISTO
     * @return Proceso seleccionado para ejecutar, o null si no hay procesos
     */
    Proceso seleccionarSiguiente(List<Proceso> procesosListos);

    /**
     * Reordena la cola de procesos según el algoritmo de planificación
     * 
     * @param procesosListos Lista de procesos a reordenar
     */
    void reordenarCola(List<Proceso> procesosListos);

    /**
     * Obtiene el nombre del algoritmo
     * 
     * @return Nombre del algoritmo
     */
    String getNombre();
}

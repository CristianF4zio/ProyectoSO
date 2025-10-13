package main.planificacion;

import main.modelo.Proceso;
import java.util.List;

/**
 * Interfaz abstracta para algoritmos de planificación
 */
public interface AlgoritmoPlanificacion {

    /**
     * Selecciona el siguiente proceso a ejecutar
     * 
     * @param procesosListos Lista de procesos en estado listo
     * @return Proceso seleccionado para ejecución
     */
    Proceso seleccionarSiguiente(List<Proceso> procesosListos);

    /**
     * Reordena la cola de procesos listos según el algoritmo
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
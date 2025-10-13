package main.gestor;

import main.modelo.Proceso;

/**
 * Clase que gestiona todas las colas del sistema
 */
public class GestorColas {

    private ColaProcesos colaListos;
    private ColaProcesos colaBloqueados;
    private ColaProcesos colaSuspendidos;
    private ColaProcesos colaTerminados;

    /**
     * Constructor del gestor de colas
     */
    public GestorColas() {
        // TODO: Inicializar todas las colas
    }

    /**
     * Mueve un proceso de una cola a otra
     * 
     * @param proceso     Proceso a mover
     * @param colaOrigen  Cola de origen
     * @param colaDestino Cola de destino
     */
    public void moverProceso(Proceso proceso, String colaOrigen, String colaDestino) {
        // TODO: Implementar movimiento entre colas
    }

    /**
     * Obtiene la cola de procesos listos
     * 
     * @return Cola de procesos listos
     */
    public ColaProcesos getColaListos() {
        return colaListos;
    }

    /**
     * Obtiene la cola de procesos bloqueados
     * 
     * @return Cola de procesos bloqueados
     */
    public ColaProcesos getColaBloqueados() {
        return colaBloqueados;
    }

    /**
     * Obtiene la cola de procesos suspendidos
     * 
     * @return Cola de procesos suspendidos
     */
    public ColaProcesos getColaSuspendidos() {
        return colaSuspendidos;
    }

    /**
     * Obtiene la cola de procesos terminados
     * 
     * @return Cola de procesos terminados
     */
    public ColaProcesos getColaTerminados() {
        return colaTerminados;
    }
}
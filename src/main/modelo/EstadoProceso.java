package main.modelo;

/**
 * Enum que define los estados posibles de un proceso
 */
public enum EstadoProceso {
    NUEVO,
    LISTO,
    EJECUCION,
    BLOQUEADO,
    TERMINADO,
    SUSPENDIDO
}
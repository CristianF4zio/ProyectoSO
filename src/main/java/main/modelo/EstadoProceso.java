package main.modelo;

/**
 * Enum que representa los posibles estados de un proceso
 */
public enum EstadoProceso {
    NUEVO, // Proceso recién creado
    LISTO, // Proceso listo para ejecutar
    EJECUCION, // Proceso ejecutándose en CPU
    BLOQUEADO, // Proceso esperando por I/O
    TERMINADO, // Proceso completado
    SUSPENDIDO // Proceso suspendido (en memoria secundaria)
}

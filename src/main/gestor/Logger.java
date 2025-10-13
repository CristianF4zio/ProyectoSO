package main.gestor;

/**
 * Clase que maneja el logging de eventos del sistema
 */
public class Logger {

    private static Logger instancia;
    private StringBuilder logBuffer;

    private Logger() {
        this.logBuffer = new StringBuilder();
    }

    /**
     * Obtiene la instancia singleton del logger
     * 
     * @return Instancia del logger
     */
    public static Logger getInstance() {
        if (instancia == null) {
            instancia = new Logger();
        }
        return instancia;
    }

    /**
     * Registra un evento en el log
     * 
     * @param evento Descripción del evento
     */
    public void log(String evento) {
        // TODO: Implementar logging de eventos
    }

    /**
     * Registra la selección de un proceso por el planificador
     * 
     * @param nombreProceso Nombre del proceso seleccionado
     * @param algoritmo     Algoritmo utilizado
     */
    public void logSeleccionProceso(String nombreProceso, String algoritmo) {
        // TODO: Implementar log de selección
    }

    /**
     * Registra un cambio de estado de proceso
     * 
     * @param nombreProceso  Nombre del proceso
     * @param estadoAnterior Estado anterior
     * @param estadoNuevo    Estado nuevo
     */
    public void logCambioEstado(String nombreProceso, String estadoAnterior, String estadoNuevo) {
        // TODO: Implementar log de cambio de estado
    }

    /**
     * Obtiene el contenido completo del log
     * 
     * @return Contenido del log
     */
    public String getLogCompleto() {
        return logBuffer.toString();
    }

    /**
     * Limpia el log
     */
    public void limpiarLog() {
        logBuffer.setLength(0);
    }
}
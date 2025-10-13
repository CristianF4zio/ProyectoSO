package main.excepciones;

/**
 * Excepción específica para errores de procesos
 */
public class ProcesoException extends SimuladorException {

    public ProcesoException(String mensaje) {
        super(mensaje);
    }

    public ProcesoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

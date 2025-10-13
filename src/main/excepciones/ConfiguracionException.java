package main.excepciones;

/**
 * Excepción específica para errores de configuración
 */
public class ConfiguracionException extends SimuladorException {

    public ConfiguracionException(String mensaje) {
        super(mensaje);
    }

    public ConfiguracionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

package main.excepciones;

/**
 * Excepción personalizada para el simulador
 */
public class SimuladorException extends Exception {

    public SimuladorException(String mensaje) {
        super(mensaje);
    }

    public SimuladorException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

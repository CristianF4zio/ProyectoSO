package main.utilidades;

/**
 * Clase de utilidades generales del sistema
 */
public class Utilidades {

    /**
     * Genera un ID único para procesos
     * 
     * @return ID único
     */
    public static int generarIdUnico() {
        // TODO: Implementar generación de ID único
        return 0;
    }

    /**
     * Valida que un valor esté en un rango específico
     * 
     * @param valor Valor a validar
     * @param min   Valor mínimo
     * @param max   Valor máximo
     * @return true si está en el rango
     */
    public static boolean validarRango(int valor, int min, int max) {
        return valor >= min && valor <= max;
    }

    /**
     * Formatea tiempo en milisegundos a formato legible
     * 
     * @param milisegundos Tiempo en milisegundos
     * @return String formateado
     */
    public static String formatearTiempo(long milisegundos) {
        // TODO: Implementar formateo de tiempo
        return "";
    }

    /**
     * Calcula el promedio de una lista de valores
     * 
     * @param valores Lista de valores
     * @return Promedio calculado
     */
    public static double calcularPromedio(double[] valores) {
        // TODO: Implementar cálculo de promedio
        return 0.0;
    }
}

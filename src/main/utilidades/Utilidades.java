package main.utilidades;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase de utilidades generales del sistema
 */
public class Utilidades {

    private static AtomicInteger generadorId = new AtomicInteger(1);

    /**
     * Genera un ID único para procesos
     * 
     * @return ID único
     */
    public static int generarIdUnico() {
        return generadorId.getAndIncrement();
    }
    
    /**
     * Reinicia el generador de IDs
     */
    public static void reiniciarGeneradorId() {
        generadorId.set(1);
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
     * @return String formateado (HH:MM:SS.mmm)
     */
    public static String formatearTiempo(long milisegundos) {
        long segundos = milisegundos / 1000;
        long minutos = segundos / 60;
        long horas = minutos / 60;
        
        segundos = segundos % 60;
        minutos = minutos % 60;
        long ms = milisegundos % 1000;
        
        return String.format("%02d:%02d:%02d.%03d", horas, minutos, segundos, ms);
    }
    
    /**
     * Formatea tiempo en milisegundos a formato corto
     * 
     * @param milisegundos Tiempo en milisegundos
     * @return String formateado (MM:SS)
     */
    public static String formatearTiempoCorto(long milisegundos) {
        long segundos = milisegundos / 1000;
        long minutos = segundos / 60;
        segundos = segundos % 60;
        
        return String.format("%02d:%02d", minutos, segundos);
    }

    /**
     * Calcula el promedio de una lista de valores
     * 
     * @param valores Lista de valores
     * @return Promedio calculado
     */
    public static double calcularPromedio(double[] valores) {
        if (valores == null || valores.length == 0) {
            return 0.0;
        }
        
        double suma = 0.0;
        for (double valor : valores) {
            suma += valor;
        }
        
        return suma / valores.length;
    }
    
    /**
     * Calcula el promedio de una lista de valores enteros
     * 
     * @param valores Lista de valores
     * @return Promedio calculado
     */
    public static double calcularPromedio(int[] valores) {
        if (valores == null || valores.length == 0) {
            return 0.0;
        }
        
        int suma = 0;
        for (int valor : valores) {
            suma += valor;
        }
        
        return (double) suma / valores.length;
    }
    
    /**
     * Calcula la desviación estándar de una lista de valores
     * 
     * @param valores Lista de valores
     * @return Desviación estándar calculada
     */
    public static double calcularDesviacionEstandar(double[] valores) {
        if (valores == null || valores.length == 0) {
            return 0.0;
        }
        
        double promedio = calcularPromedio(valores);
        double sumaCuadrados = 0.0;
        
        for (double valor : valores) {
            double diferencia = valor - promedio;
            sumaCuadrados += diferencia * diferencia;
        }
        
        return Math.sqrt(sumaCuadrados / valores.length);
    }
    
    /**
     * Genera un número aleatorio en un rango
     * 
     * @param min Valor mínimo (inclusivo)
     * @param max Valor máximo (inclusivo)
     * @return Número aleatorio en el rango
     */
    public static int numeroAleatorio(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }
    
    /**
     * Redondea un valor a N decimales
     * 
     * @param valor Valor a redondear
     * @param decimales Número de decimales
     * @return Valor redondeado
     */
    public static double redondear(double valor, int decimales) {
        double multiplicador = Math.pow(10, decimales);
        return Math.round(valor * multiplicador) / multiplicador;
    }
    
    /**
     * Formatea un porcentaje
     * 
     * @param valor Valor entre 0 y 100
     * @return String formateado con símbolo %
     */
    public static String formatearPorcentaje(double valor) {
        return String.format("%.2f%%", valor);
    }
    
    /**
     * Valida que una cadena no sea nula ni vacía
     * 
     * @param cadena Cadena a validar
     * @return true si es válida
     */
    public static boolean validarCadena(String cadena) {
        return cadena != null && !cadena.trim().isEmpty();
    }
}

package main.gestor;

import main.modelo.Proceso;

/**
 * Clase que maneja las excepciones de I/O
 */
public class ExcepcionIO {

    private Proceso proceso;
    private int ciclosParaCompletar;
    private int ciclosTranscurridos;

    /**
     * Constructor de excepción I/O
     * 
     * @param proceso             Proceso que generó la excepción
     * @param ciclosParaCompletar Ciclos necesarios para completar la operación
     */
    public ExcepcionIO(Proceso proceso, int ciclosParaCompletar) {
        this.proceso = proceso;
        this.ciclosParaCompletar = ciclosParaCompletar;
        this.ciclosTranscurridos = 0;
    }

    /**
     * Procesa un ciclo de la excepción
     * 
     * @return true si la excepción ha sido completada
     */
    public boolean procesarCiclo() {
        // TODO: Implementar procesamiento de excepción I/O
        return false;
    }

    /**
     * Verifica si la excepción está completada
     * 
     * @return true si está completada
     */
    public boolean isCompletada() {
        return ciclosTranscurridos >= ciclosParaCompletar;
    }
}
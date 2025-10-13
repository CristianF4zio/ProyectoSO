package main.hilos;

import main.modelo.Proceso;

/**
 * Hilo que representa la ejecución de un proceso
 */
public class ProcesoThread extends Thread {

    private Proceso proceso;
    private boolean ejecutando;

    /**
     * Constructor del hilo de proceso
     * 
     * @param proceso Proceso a ejecutar
     */
    public ProcesoThread(Proceso proceso) {
        this.proceso = proceso;
        this.ejecutando = false;
    }

    @Override
    public void run() {
        // TODO: Implementar ejecución del proceso
    }

    /**
     * Inicia la ejecución del proceso
     */
    public void iniciarEjecucion() {
        this.ejecutando = true;
        this.start();
    }

    /**
     * Pausa la ejecución del proceso
     */
    public void pausarEjecucion() {
        this.ejecutando = false;
    }

    /**
     * Detiene la ejecución del proceso
     */
    public void detenerEjecucion() {
        this.ejecutando = false;
        this.interrupt();
    }
}

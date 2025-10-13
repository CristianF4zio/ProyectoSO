package main.hilos;

import main.gestor.ExcepcionIO;

/**
 * Hilo que maneja las operaciones de I/O
 */
public class IOThread extends Thread {

    private ExcepcionIO excepcionIO;
    private boolean ejecutando;

    /**
     * Constructor del hilo de I/O
     * 
     * @param excepcionIO Excepción I/O a procesar
     */
    public IOThread(ExcepcionIO excepcionIO) {
        this.excepcionIO = excepcionIO;
        this.ejecutando = false;
    }

    @Override
    public void run() {
        // TODO: Implementar procesamiento de I/O
    }

    /**
     * Inicia el procesamiento de I/O
     */
    public void iniciarProcesamiento() {
        this.ejecutando = true;
        this.start();
    }

    /**
     * Pausa el procesamiento de I/O
     */
    public void pausarProcesamiento() {
        this.ejecutando = false;
    }

    /**
     * Detiene el procesamiento de I/O
     */
    public void detenerProcesamiento() {
        this.ejecutando = false;
        this.interrupt();
    }
}

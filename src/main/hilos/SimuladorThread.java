package main.hilos;

import main.SistemaOperativo;

/**
 * Hilo principal que ejecuta la simulación
 */
public class SimuladorThread extends Thread {

    private boolean ejecutando;
    private SistemaOperativo sistemaOperativo;

    /**
     * Constructor del hilo simulador
     * 
     * @param sistemaOperativo Sistema operativo a simular
     */
    public SimuladorThread(SistemaOperativo sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
        this.ejecutando = false;
    }

    @Override
    public void run() {
        // TODO: Implementar lógica principal de simulación
    }

    /**
     * Inicia la simulación
     */
    public void iniciarSimulacion() {
        this.ejecutando = true;
        this.start();
    }

    /**
     * Pausa la simulación
     */
    public void pausarSimulacion() {
        this.ejecutando = false;
    }

    /**
     * Detiene la simulación
     */
    public void detenerSimulacion() {
        this.ejecutando = false;
        this.interrupt();
    }
}

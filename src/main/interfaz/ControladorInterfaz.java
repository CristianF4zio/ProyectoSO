package main.interfaz;

import main.SistemaOperativo;

/**
 * Controlador que maneja la interacción entre la interfaz y el modelo
 */
public class ControladorInterfaz {

    private VentanaPrincipal ventanaPrincipal;
    private SistemaOperativo sistemaOperativo;

    /**
     * Constructor del controlador
     * 
     * @param ventanaPrincipal Ventana principal de la interfaz
     * @param sistemaOperativo Sistema operativo simulado
     */
    public ControladorInterfaz(VentanaPrincipal ventanaPrincipal, SistemaOperativo sistemaOperativo) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.sistemaOperativo = sistemaOperativo;
    }

    /**
     * Inicia la simulación
     */
    public void iniciarSimulacion() {
        // TODO: Implementar inicio de simulación
    }

    /**
     * Pausa la simulación
     */
    public void pausarSimulacion() {
        // TODO: Implementar pausa de simulación
    }

    /**
     * Detiene la simulación
     */
    public void detenerSimulacion() {
        // TODO: Implementar detención de simulación
    }

    /**
     * Cambia el algoritmo de planificación
     * 
     * @param algoritmo Nuevo algoritmo
     */
    public void cambiarAlgoritmo(String algoritmo) {
        // TODO: Implementar cambio de algoritmo
    }
}

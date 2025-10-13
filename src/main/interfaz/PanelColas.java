package main.interfaz;

import javax.swing.*;
import java.awt.*;

/**
 * Panel que muestra las colas de procesos
 */
public class PanelColas extends JPanel {

    private JList<String> listaListos;
    private JList<String> listaBloqueados;
    private JList<String> listaSuspendidos;
    private JList<String> listaTerminados;

    /**
     * Constructor del panel de colas
     */
    public PanelColas() {
        inicializarComponentes();
        configurarLayout();
    }

    /**
     * Inicializa los componentes del panel
     */
    private void inicializarComponentes() {
        // TODO: Inicializar listas de colas
    }

    /**
     * Configura el layout del panel
     */
    private void configurarLayout() {
        // TODO: Configurar layout de colas
    }

    /**
     * Actualiza las colas mostradas
     */
    public void actualizarColas() {
        // TODO: Implementar actualización de colas
    }
}
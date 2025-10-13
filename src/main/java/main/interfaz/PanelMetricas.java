package main.interfaz;

import javax.swing.*;
import java.awt.*;

/**
 * Panel que muestra las métricas de rendimiento
 */
public class PanelMetricas extends JPanel {

    private JLabel lblThroughput;
    private JLabel lblUtilizacionCPU;
    private JLabel lblTiempoRespuesta;
    private JLabel lblEquidad;
    private JButton btnGenerarGrafica;

    /**
     * Constructor del panel de métricas
     */
    public PanelMetricas() {
        inicializarComponentes();
        configurarLayout();
    }

    /**
     * Inicializa los componentes del panel
     */
    private void inicializarComponentes() {
        // TODO: Inicializar componentes de métricas
    }

    /**
     * Configura el layout del panel
     */
    private void configurarLayout() {
        // TODO: Configurar layout de métricas
    }

    /**
     * Actualiza las métricas mostradas
     */
    public void actualizarMetricas() {
        // TODO: Implementar actualización de métricas
    }
}
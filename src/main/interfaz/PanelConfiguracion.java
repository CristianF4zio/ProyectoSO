package main.interfaz;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de configuración de parámetros del simulador
 */
public class PanelConfiguracion extends JPanel {

    private JSpinner spinnerDuracionCiclo;
    private JSpinner spinnerInstrucciones;
    private JSpinner spinnerCiclosExcepcion;
    private JSpinner spinnerCiclosCompletar;
    private JComboBox<String> comboAlgoritmo;
    private JButton btnAplicarConfig;
    private JButton btnGuardarConfig;

    /**
     * Constructor del panel de configuración
     */
    public PanelConfiguracion() {
        inicializarComponentes();
        configurarLayout();
    }

    /**
     * Inicializa los componentes del panel
     */
    private void inicializarComponentes() {
        // TODO: Inicializar componentes de configuración
    }

    /**
     * Configura el layout del panel
     */
    private void configurarLayout() {
        // TODO: Configurar layout de configuración
    }

    /**
     * Aplica la configuración actual
     */
    public void aplicarConfiguracion() {
        // TODO: Implementar aplicación de configuración
    }

    /**
     * Guarda la configuración actual
     */
    public void guardarConfiguracion() {
        // TODO: Implementar guardado de configuración
    }
}
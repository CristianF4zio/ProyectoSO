package main.interfaz;

import main.modelo.TipoProceso;
import javax.swing.*;
import java.awt.*;

/**
 * Panel de configuración de parámetros del simulador
 * Permite cambiar algoritmo, duración de ciclo y crear procesos
 */
public class PanelConfiguracion extends JPanel {

    private ControladorInterfaz controlador;
    
    private JComboBox<String> comboAlgoritmo;
    private JSpinner spinnerDuracionCiclo;
    private JButton btnAplicarAlgoritmo;
    private JButton btnAplicarDuracion;
    private JButton btnGuardarConfig;
    
    private JTextField txtNombreProceso;
    private JSpinner spinnerInstrucciones;
    private JComboBox<TipoProceso> comboTipoProceso;
    private JSpinner spinnerPrioridad;
    private JButton btnCrearProceso;

    /**
     * Constructor del panel de configuración
     * 
     * @param controlador Controlador de la interfaz
     */
    public PanelConfiguracion(ControladorInterfaz controlador) {
        this.controlador = controlador;
        inicializarComponentes();
        configurarLayout();
    }

    /**
     * Inicializa los componentes del panel
     */
    private void inicializarComponentes() {
        // Algoritmo de planificación
        String[] algoritmos = {"FCFS", "SJF", "SRTF", "PRIORIDAD", "ROUND_ROBIN", "MULTINIVEL", "MULTINIVEL_FEEDBACK"};
        comboAlgoritmo = new JComboBox<>(algoritmos);
        comboAlgoritmo.setSelectedItem(controlador.getConfiguracion().getPoliticaPlanificacion());
        
        btnAplicarAlgoritmo = new JButton("Aplicar");
        btnAplicarAlgoritmo.addActionListener(e -> aplicarAlgoritmo());
        
        // Duración de ciclo
        int duracionActual = controlador.getConfiguracion().getDuracionCicloMs();
        spinnerDuracionCiclo = new JSpinner(new SpinnerNumberModel(duracionActual, 100, 5000, 100));
        
        btnAplicarDuracion = new JButton("Aplicar");
        btnAplicarDuracion.addActionListener(e -> aplicarDuracionCiclo());
        
        // Guardar configuración
        btnGuardarConfig = new JButton("Guardar Configuración");
        btnGuardarConfig.addActionListener(e -> guardarConfiguracion());
        
        // Creación de procesos
        txtNombreProceso = new JTextField("Proceso_" + System.currentTimeMillis() % 1000);
        spinnerInstrucciones = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        comboTipoProceso = new JComboBox<>(TipoProceso.values());
        spinnerPrioridad = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        
        btnCrearProceso = new JButton("Crear Proceso");
        btnCrearProceso.addActionListener(e -> crearProceso());
    }

    /**
     * Configura el layout del panel
     */
    private void configurarLayout() {
        setBorder(BorderFactory.createTitledBorder("Configuración"));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Panel de algoritmo
        JPanel panelAlgoritmo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAlgoritmo.add(new JLabel("Algoritmo:"));
        panelAlgoritmo.add(comboAlgoritmo);
        panelAlgoritmo.add(btnAplicarAlgoritmo);
        
        // Panel de duración de ciclo
        JPanel panelCiclo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCiclo.add(new JLabel("Ciclo (ms):"));
        panelCiclo.add(spinnerDuracionCiclo);
        panelCiclo.add(btnAplicarDuracion);
        
        // Panel de crear proceso
        JPanel panelCrearProceso = new JPanel(new GridLayout(5, 2, 5, 5));
        panelCrearProceso.setBorder(BorderFactory.createTitledBorder("Crear Proceso"));
        panelCrearProceso.add(new JLabel("Nombre:"));
        panelCrearProceso.add(txtNombreProceso);
        panelCrearProceso.add(new JLabel("Instrucciones:"));
        panelCrearProceso.add(spinnerInstrucciones);
        panelCrearProceso.add(new JLabel("Tipo:"));
        panelCrearProceso.add(comboTipoProceso);
        panelCrearProceso.add(new JLabel("Prioridad:"));
        panelCrearProceso.add(spinnerPrioridad);
        panelCrearProceso.add(new JLabel(""));
        panelCrearProceso.add(btnCrearProceso);
        
        // Agregar al panel principal
        add(panelAlgoritmo);
        add(panelCiclo);
        add(Box.createVerticalStrut(10));
        add(panelCrearProceso);
        add(Box.createVerticalStrut(10));
        add(btnGuardarConfig);
        add(Box.createVerticalGlue());
    }

    /**
     * Aplica el algoritmo seleccionado
     */
    private void aplicarAlgoritmo() {
        String algoritmo = (String) comboAlgoritmo.getSelectedItem();
        controlador.cambiarAlgoritmo(algoritmo);
        JOptionPane.showMessageDialog(this,
                "Algoritmo cambiado a: " + algoritmo,
                "Configuración",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Aplica la duración de ciclo seleccionada
     */
    private void aplicarDuracionCiclo() {
        int duracion = (Integer) spinnerDuracionCiclo.getValue();
        controlador.cambiarDuracionCiclo(duracion);
        JOptionPane.showMessageDialog(this,
                "Duración de ciclo cambiada a: " + duracion + " ms",
                "Configuración",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Crea un nuevo proceso con los parámetros especificados
     */
    private void crearProceso() {
        String nombre = txtNombreProceso.getText();
        int instrucciones = (Integer) spinnerInstrucciones.getValue();
        TipoProceso tipo = (TipoProceso) comboTipoProceso.getSelectedItem();
        int prioridad = (Integer) spinnerPrioridad.getValue();
        
        controlador.crearProceso(nombre, instrucciones, tipo, prioridad);
        
        // Actualizar nombre para el siguiente proceso
        txtNombreProceso.setText("Proceso_" + System.currentTimeMillis() % 1000);
        
        JOptionPane.showMessageDialog(this,
                "Proceso creado: " + nombre,
                "Creación de Proceso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Guarda la configuración actual
     */
    private void guardarConfiguracion() {
        controlador.guardarConfiguracion();
        JOptionPane.showMessageDialog(this,
                "Configuración guardada exitosamente",
                "Guardar Configuración",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

package main.interfaz;

import main.core.SistemaOperativoSimulado;
import main.modelo.Proceso;
import main.modelo.TipoProceso;
import javax.swing.*;
import java.awt.*;

/**
 * Panel principal que muestra la simulación en tiempo real
 * Incluye controles para iniciar, pausar y detener la simulación
 */
public class PanelSimulacion extends JPanel {

    private ControladorInterfaz controlador;
    
    private JLabel lblCicloActual;
    private JLabel lblProcesoEjecutando;
    private JLabel lblProgramCounter;
    private JLabel lblAlgoritmo;
    private JLabel lblEstadoCPU;
    
    private JButton btnIniciar;
    private JButton btnPausar;
    private JButton btnDetener;
    private JButton btnCrearProceso;

    /**
     * Constructor del panel de simulación
     * 
     * @param controlador Controlador de la interfaz
     */
    public PanelSimulacion(ControladorInterfaz controlador) {
        this.controlador = controlador;
        inicializarComponentes();
        configurarLayout();
    }

    /**
     * Inicializa los componentes del panel
     */
    private void inicializarComponentes() {
        lblCicloActual = new JLabel("Ciclo: 0");
        lblCicloActual.setFont(new Font("Monospaced", Font.BOLD, 16));
        
        lblAlgoritmo = new JLabel("Algoritmo: FCFS");
        lblAlgoritmo.setFont(new Font("Arial", Font.BOLD, 12));
        
        lblProcesoEjecutando = new JLabel("CPU: IDLE");
        lblProcesoEjecutando.setFont(new Font("Arial", Font.PLAIN, 12));
        
        lblProgramCounter = new JLabel("PC: 0");
        lblProgramCounter.setFont(new Font("Arial", Font.PLAIN, 12));
        
        lblEstadoCPU = new JLabel("Estado: Detenido");
        lblEstadoCPU.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Botones de control
        btnIniciar = new JButton("Iniciar");
        btnIniciar.addActionListener(e -> controlador.iniciarSimulacion());
        
        btnPausar = new JButton("Pausar");
        btnPausar.addActionListener(e -> controlador.pausarSimulacion());
        btnPausar.setEnabled(false);
        
        btnDetener = new JButton("Detener");
        btnDetener.addActionListener(e -> controlador.detenerSimulacion());
        btnDetener.setEnabled(false);
        
        btnCrearProceso = new JButton("Crear Proceso");
        btnCrearProceso.addActionListener(e -> mostrarDialogoCrearProceso());
    }

    /**
     * Configura el layout del panel
     */
    private void configurarLayout() {
        setBorder(BorderFactory.createTitledBorder("Simulación"));
        setLayout(new BorderLayout(10, 10));
        
        // Panel de información
        JPanel panelInfo = new JPanel(new GridLayout(5, 1, 5, 5));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelInfo.add(lblCicloActual);
        panelInfo.add(lblAlgoritmo);
        panelInfo.add(lblProcesoEjecutando);
        panelInfo.add(lblProgramCounter);
        panelInfo.add(lblEstadoCPU);
        
        // Panel de controles
        JPanel panelControles = new JPanel(new GridLayout(2, 2, 5, 5));
        panelControles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelControles.add(btnIniciar);
        panelControles.add(btnPausar);
        panelControles.add(btnDetener);
        panelControles.add(btnCrearProceso);
        
        add(panelInfo, BorderLayout.CENTER);
        add(panelControles, BorderLayout.SOUTH);
    }
    
    /**
     * Muestra el diálogo para crear un nuevo proceso
     */
    private void mostrarDialogoCrearProceso() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        JTextField txtNombre = new JTextField("Proceso_" + System.currentTimeMillis() % 1000);
        JSpinner spnInstrucciones = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JComboBox<TipoProceso> cmbTipo = new JComboBox<>(TipoProceso.values());
        JSpinner spnPrioridad = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Instrucciones:"));
        panel.add(spnInstrucciones);
        panel.add(new JLabel("Tipo:"));
        panel.add(cmbTipo);
        panel.add(new JLabel("Prioridad:"));
        panel.add(spnPrioridad);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Crear Proceso",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText();
            int instrucciones = (Integer) spnInstrucciones.getValue();
            TipoProceso tipo = (TipoProceso) cmbTipo.getSelectedItem();
            int prioridad = (Integer) spnPrioridad.getValue();
            
            controlador.crearProceso(nombre, instrucciones, tipo, prioridad);
        }
    }

    /**
     * Actualiza la información mostrada en el panel
     * 
     * @param sistema Sistema operativo simulado
     */
    public void actualizar(SistemaOperativoSimulado sistema) {
        lblCicloActual.setText("Ciclo: " + sistema.getCicloActual());
        lblAlgoritmo.setText("Algoritmo: " + sistema.getPlanificadorActual().getNombre());
        
        Proceso procesoActual = sistema.getProcesoEnEjecucion();
        if (procesoActual != null) {
            lblProcesoEjecutando.setText("CPU: " + procesoActual.getNombre());
            lblProgramCounter.setText("PC: " + procesoActual.getPC());
        } else {
            lblProcesoEjecutando.setText("CPU: IDLE");
            lblProgramCounter.setText("PC: -");
        }
        
        // Actualizar estado y botones
        if (controlador.isSimulacionActiva()) {
            if (controlador.isSimulacionPausada()) {
                lblEstadoCPU.setText("Estado: Pausado");
                btnIniciar.setEnabled(true);
                btnPausar.setEnabled(false);
            } else {
                lblEstadoCPU.setText("Estado: Ejecutando");
                btnIniciar.setEnabled(false);
                btnPausar.setEnabled(true);
            }
            btnDetener.setEnabled(true);
        } else {
            lblEstadoCPU.setText("Estado: Detenido");
            btnIniciar.setEnabled(true);
            btnPausar.setEnabled(false);
            btnDetener.setEnabled(false);
        }
    }
}

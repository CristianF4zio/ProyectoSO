package main.interfaz;

import main.core.SistemaOperativoSimulado;
import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de la interfaz gráfica del simulador
 * Contiene todos los paneles de visualización y control
 */
public class VentanaPrincipal extends JFrame {

    private ControladorInterfaz controlador;
    
    // Paneles
    private PanelSimulacion panelSimulacion;
    private PanelColas panelColas;
    private PanelConfiguracion panelConfiguracion;
    private PanelLog panelLog;
    private PanelMetricas panelMetricas;

    /**
     * Constructor de la ventana principal
     * 
     * @param controlador Controlador de la interfaz
     */
    public VentanaPrincipal(ControladorInterfaz controlador) {
        this.controlador = controlador;
        inicializarPaneles();
        configurarVentana();
    }

    /**
     * Inicializa todos los paneles
     */
    private void inicializarPaneles() {
        panelSimulacion = new PanelSimulacion(controlador);
        panelColas = new PanelColas();
        panelConfiguracion = new PanelConfiguracion(controlador);
        panelLog = new PanelLog();
        panelMetricas = new PanelMetricas();
    }

    /**
     * Configura la ventana principal
     */
    private void configurarVentana() {
        setTitle("Simulador de Sistema Operativo - Planificación de Procesos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        // Layout principal
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior: Simulación y Configuración
        JPanel panelSuperior = new JPanel(new GridLayout(1, 2, 10, 10));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        panelSuperior.add(panelSimulacion);
        panelSuperior.add(panelConfiguracion);
        
        // Panel central: Colas y Métricas
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelCentral.add(panelColas);
        panelCentral.add(panelMetricas);
        
        // Panel inferior: Log
        panelLog.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        // Agregar paneles a la ventana
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelLog, BorderLayout.SOUTH);
        
        // Menú
        crearMenuBar();
    }
    
    /**
     * Crea la barra de menú
     */
    private void crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemGuardarConfig = new JMenuItem("Guardar Configuración");
        itemGuardarConfig.addActionListener(e -> controlador.guardarConfiguracion());
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> {
            controlador.detenerSimulacion();
            System.exit(0);
        });
        menuArchivo.add(itemGuardarConfig);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcercaDe = new JMenuItem("Acerca de");
        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());
        menuAyuda.add(itemAcercaDe);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuAyuda);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Muestra el diálogo "Acerca de"
     */
    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
            "Simulador de Sistema Operativo\n" +
            "Versión 1.0\n\n" +
            "Simula la planificación de procesos en un sistema operativo\n" +
            "con múltiples algoritmos: FCFS, SJF, Prioridad, Round Robin,\n" +
            "Multinivel y Multinivel con Feedback.\n\n" +
            "Desarrollado para el curso de Sistemas Operativos",
            "Acerca de",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Actualiza todos los paneles con información del sistema
     * 
     * @param sistema Sistema operativo simulado
     */
    public void actualizar(SistemaOperativoSimulado sistema) {
        panelSimulacion.actualizar(sistema);
        panelColas.actualizar(sistema);
        panelMetricas.actualizar(sistema);
        panelLog.actualizar(sistema);
    }
    
    // Getters para los paneles
    
    public PanelSimulacion getPanelSimulacion() {
        return panelSimulacion;
    }
    
    public PanelColas getPanelColas() {
        return panelColas;
    }
    
    public PanelConfiguracion getPanelConfiguracion() {
        return panelConfiguracion;
    }
    
    public PanelLog getPanelLog() {
        return panelLog;
    }
    
    public PanelMetricas getPanelMetricas() {
        return panelMetricas;
    }
}

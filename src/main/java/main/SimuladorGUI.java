package main;

import main.gestor.GestorProcesos;
import main.modelo.*;
import main.planificacion.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Simulador con Interfaz Gráfica
 * Versión simplificada sin dependencias externas
 */
public class SimuladorGUI extends JFrame {
    
    private GestorProcesos gestorProcesos;
    private AlgoritmoPlanificacion algoritmoActual;
    private int cicloActual = 0;
    private Proceso procesoEnEjecucion;
    
    // Componentes GUI
    private JTextArea areaConsola;
    private JList<String> listaListos;
    private JList<String> listaTerminados;
    private DefaultListModel<String> modeloListos;
    private DefaultListModel<String> modeloTerminados;
    private JLabel lblCiclo;
    private JLabel lblCPU;
    private JLabel lblAlgoritmo;
    private JComboBox<String> comboAlgoritmos;
    private JButton btnIniciar;
    private JButton btnPausa;
    private JButton btnCrearProceso;
    
    private Timer timer;
    private boolean ejecutando = false;
    
    public SimuladorGUI() {
        super("Simulador de Sistema Operativo - GUI");
        
        // Inicializar componentes
        gestorProcesos = new GestorProcesos(50);
        algoritmoActual = new FCFS();
        
        // Configurar ventana
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Crear GUI
        inicializarComponentes();
        configurarLayout();
        
        // Crear procesos iniciales
        crearProcesosIniciales();
        
        // Timer para simulación
        timer = new Timer(1000, e -> ejecutarCiclo());
        
        setVisible(true);
    }
    
    private void inicializarComponentes() {
        // Área de consola
        areaConsola = new JTextArea(15, 50);
        areaConsola.setEditable(false);
        areaConsola.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Listas
        modeloListos = new DefaultListModel<>();
        modeloTerminados = new DefaultListModel<>();
        listaListos = new JList<>(modeloListos);
        listaTerminados = new JList<>(modeloTerminados);
        
        // Labels de estado
        lblCiclo = new JLabel("Ciclo: 0");
        lblCiclo.setFont(new Font("Arial", Font.BOLD, 16));
        lblCPU = new JLabel("CPU: IDLE");
        lblCPU.setFont(new Font("Arial", Font.PLAIN, 14));
        lblAlgoritmo = new JLabel("Algoritmo: FCFS");
        lblAlgoritmo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Combo de algoritmos
        String[] algoritmos = {"FCFS", "SJF", "SRTF", "Prioridad", "Round Robin"};
        comboAlgoritmos = new JComboBox<>(algoritmos);
        comboAlgoritmos.addActionListener(e -> cambiarAlgoritmo());
        
        // Botones
        btnIniciar = new JButton("Iniciar");
        btnIniciar.addActionListener(e -> iniciarSimulacion());
        
        btnPausa = new JButton("Pausar");
        btnPausa.setEnabled(false);
        btnPausa.addActionListener(e -> pausarSimulacion());
        
        btnCrearProceso = new JButton("Crear Proceso");
        btnCrearProceso.addActionListener(e -> mostrarDialogoCrearProceso());
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior - Estado
        JPanel panelEstado = new JPanel(new GridLayout(3, 1, 5, 5));
        panelEstado.setBorder(BorderFactory.createTitledBorder("Estado del Sistema"));
        panelEstado.add(lblCiclo);
        panelEstado.add(lblAlgoritmo);
        panelEstado.add(lblCPU);
        
        // Panel de control
        JPanel panelControl = new JPanel(new FlowLayout());
        panelControl.setBorder(BorderFactory.createTitledBorder("Controles"));
        panelControl.add(new JLabel("Algoritmo:"));
        panelControl.add(comboAlgoritmos);
        panelControl.add(btnIniciar);
        panelControl.add(btnPausa);
        panelControl.add(btnCrearProceso);
        
        // Panel izquierdo - Colas
        JPanel panelColas = new JPanel(new GridLayout(2, 1, 5, 5));
        panelColas.setBorder(BorderFactory.createTitledBorder("Colas de Procesos"));
        
        JPanel panelListos = new JPanel(new BorderLayout());
        panelListos.setBorder(BorderFactory.createTitledBorder("Listos"));
        panelListos.add(new JScrollPane(listaListos), BorderLayout.CENTER);
        
        JPanel panelTerminados = new JPanel(new BorderLayout());
        panelTerminados.setBorder(BorderFactory.createTitledBorder("Terminados"));
        panelTerminados.add(new JScrollPane(listaTerminados), BorderLayout.CENTER);
        
        panelColas.add(panelListos);
        panelColas.add(panelTerminados);
        
        // Panel derecho - Consola
        JPanel panelConsola = new JPanel(new BorderLayout());
        panelConsola.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        panelConsola.add(new JScrollPane(areaConsola), BorderLayout.CENTER);
        
        // Panel superior combinado
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(panelEstado, BorderLayout.WEST);
        panelTop.add(panelControl, BorderLayout.CENTER);
        
        // Split panel para colas y consola
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelColas, panelConsola);
        splitPane.setDividerLocation(350);
        
        // Agregar a la ventana
        add(panelTop, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void crearProcesosIniciales() {
        log("=== Creando procesos iniciales ===");
        gestorProcesos.crearProceso("Proceso1", 15, TipoProceso.CPU_BOUND, 1);
        gestorProcesos.crearProceso("Proceso2", 8, TipoProceso.IO_BOUND, 2);
        gestorProcesos.crearProceso("Proceso3", 12, TipoProceso.CPU_BOUND, 3);
        gestorProcesos.crearProceso("Proceso4", 6, TipoProceso.IO_BOUND, 1);
        gestorProcesos.crearProceso("Proceso5", 20, TipoProceso.CPU_BOUND, 2);
        
        // Cambiar a LISTO
        for (Proceso p : gestorProcesos.getProcesosActivos()) {
            p.setEstado(EstadoProceso.LISTO);
        }
        
        actualizarVista();
        log("5 procesos creados y listos para ejecutar");
    }
    
    private void iniciarSimulacion() {
        ejecutando = true;
        timer.start();
        btnIniciar.setEnabled(false);
        btnPausa.setEnabled(true);
        log("=== Simulación iniciada ===");
    }
    
    private void pausarSimulacion() {
        ejecutando = false;
        timer.stop();
        btnIniciar.setEnabled(true);
        btnPausa.setEnabled(false);
        log("=== Simulación pausada ===");
    }
    
    private void ejecutarCiclo() {
        cicloActual++;
        lblCiclo.setText("Ciclo: " + cicloActual);
        
        // Si no hay proceso en ejecución, seleccionar siguiente
        if (procesoEnEjecucion == null) {
            List<Proceso> listos = gestorProcesos.getProcesosPorEstado(EstadoProceso.LISTO);
            if (!listos.isEmpty()) {
                algoritmoActual.reordenarCola(listos);
                procesoEnEjecucion = algoritmoActual.seleccionarSiguiente(listos);
                
                if (procesoEnEjecucion != null) {
                    procesoEnEjecucion.setEstado(EstadoProceso.EJECUCION);
                    procesoEnEjecucion.iniciarEjecucion();
                    log("→ Proceso en CPU: " + procesoEnEjecucion.getNombre());
                }
            }
        }
        
        // Ejecutar proceso actual
        if (procesoEnEjecucion != null) {
            lblCPU.setText("CPU: " + procesoEnEjecucion.getNombre() + 
                          " [PC: " + procesoEnEjecucion.getProgramCounter() + "]");
            
            // Incrementar PC
            procesoEnEjecucion.setProgramCounter(procesoEnEjecucion.getProgramCounter() + 1);
            procesoEnEjecucion.setInstruccionesEjecutadas(
                procesoEnEjecucion.getInstruccionesEjecutadas() + 1
            );
            
            // Verificar si terminó
            if (procesoEnEjecucion.getProgramCounter() >= procesoEnEjecucion.getNumInstrucciones()) {
                procesoEnEjecucion.setEstado(EstadoProceso.TERMINADO);
                procesoEnEjecucion.finalizarEjecucion();
                log("✓ Proceso terminado: " + procesoEnEjecucion.getNombre());
                procesoEnEjecucion = null;
                lblCPU.setText("CPU: IDLE");
            }
        } else {
            lblCPU.setText("CPU: IDLE");
        }
        
        actualizarVista();
        
        // Verificar si todos terminaron
        if (gestorProcesos.getProcesosPorEstado(EstadoProceso.LISTO).isEmpty() &&
            procesoEnEjecucion == null) {
            pausarSimulacion();
            log("=== Todos los procesos han terminado ===");
            mostrarEstadisticas();
        }
    }
    
    private void cambiarAlgoritmo() {
        String seleccion = (String) comboAlgoritmos.getSelectedItem();
        
        switch (seleccion) {
            case "FCFS":
                algoritmoActual = new FCFS();
                break;
            case "SJF":
                algoritmoActual = new SJF();
                break;
            case "SRTF":
                algoritmoActual = new SRTF();
                break;
            case "Prioridad":
                algoritmoActual = new Prioridad();
                break;
            case "Round Robin":
                algoritmoActual = new RoundRobin(3);
                break;
        }
        
        lblAlgoritmo.setText("Algoritmo: " + algoritmoActual.getNombre());
        log("Algoritmo cambiado a: " + algoritmoActual.getNombre());
    }
    
    private void mostrarDialogoCrearProceso() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        JTextField txtNombre = new JTextField("Proceso_" + (gestorProcesos.getSiguienteId()));
        JSpinner spnInstrucciones = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
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
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Crear Nuevo Proceso",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            Proceso p = gestorProcesos.crearProceso(
                txtNombre.getText(),
                (Integer) spnInstrucciones.getValue(),
                (TipoProceso) cmbTipo.getSelectedItem(),
                (Integer) spnPrioridad.getValue()
            );
            
            if (p != null) {
                p.setEstado(EstadoProceso.LISTO);
                log("+ Proceso creado: " + p.getNombre());
                actualizarVista();
            }
        }
    }
    
    private void actualizarVista() {
        // Actualizar lista de listos
        modeloListos.clear();
        for (Proceso p : gestorProcesos.getProcesosPorEstado(EstadoProceso.LISTO)) {
            modeloListos.addElement(String.format("%-12s [%2d inst, Pri:%d, %s]",
                p.getNombre(), p.getNumInstrucciones(), p.getPrioridad(), p.getTipo()));
        }
        
        if (procesoEnEjecucion != null) {
            modeloListos.addElement("→ " + procesoEnEjecucion.getNombre() + " [EN EJECUCIÓN]");
        }
        
        // Actualizar lista de terminados
        modeloTerminados.clear();
        for (Proceso p : gestorProcesos.getProcesosPorEstado(EstadoProceso.TERMINADO)) {
            modeloTerminados.addElement(String.format("✓ %-12s [%2d inst ejecutadas]",
                p.getNombre(), p.getInstruccionesEjecutadas()));
        }
    }
    
    private void mostrarEstadisticas() {
        int[] stats = gestorProcesos.getEstadisticasProcesos();
        log("\n=== ESTADÍSTICAS FINALES ===");
        log("Total de ciclos: " + cicloActual);
        log("Procesos completados: " + stats[5]);
        log("CPU_BOUND: " + gestorProcesos.getProcesosPorTipo(TipoProceso.CPU_BOUND).size());
        log("IO_BOUND: " + gestorProcesos.getProcesosPorTipo(TipoProceso.IO_BOUND).size());
    }
    
    private void log(String mensaje) {
        areaConsola.append(mensaje + "\n");
        areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usar Look and Feel por defecto
        }
        
        SwingUtilities.invokeLater(() -> {
            new SimuladorGUI();
        });
    }
}


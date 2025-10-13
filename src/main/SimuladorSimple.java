package main;

import simuladorPlanificacion.io.IOCompletionHandler;
import simuladorPlanificacion.io.IOThread;
import simuladorPlanificacion.metricas.MetricasRendimiento;
import simuladorPlanificacion.grafica.GraficadorMetricas;
import simuladorPlanificacion.logging.Logger;
import main.gestor.Reloj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simulador simplificado que demuestra el funcionamiento de los componentes
 */
public class SimuladorSimple extends JFrame {
    
    // Componentes del simulador
    private MetricasRendimiento metricas;
    private Logger logger;
    private Reloj reloj;
    
    // Estado de la simulación
    private AtomicBoolean simulacionActiva;
    private AtomicLong contadorProcesos;
    private Timer timerSimulacion;
    
    // Componentes de la interfaz
    private JTextArea areaLog;
    private JLabel labelEstado;
    private JLabel labelCiclo;
    private JLabel labelProcesos;
    private JLabel labelUtilizacion;
    private JLabel labelThroughput;
    
    // Configuración
    private static final int DURACION_CICLO_MS = 100;
    private static final int INTERVALO_ACTUALIZACION_MS = 1000;
    
    public SimuladorSimple() {
        super("Simulador de Planificación - Versión Simplificada");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Inicializar componentes
        inicializarComponentes();
        
        // Crear interfaz
        crearInterfaz();
        
        // Configurar timer de simulación
        configurarTimer();
        
        logger.info("Simulador simplificado inicializado correctamente");
    }
    
    /**
     * Inicializa todos los componentes del simulador
     */
    private void inicializarComponentes() {
        // Inicializar métricas
        metricas = new MetricasRendimiento();
        metricas.setCiclosPorSegundoSimulado(1000);
        
        // Inicializar logger
        logger = Logger.get("SimuladorSimple");
        logger.setNivel(Logger.INFO);
        logger.setArchivo("logs/simulador_simple.log");
        logger.habilitarRotacion(1048576, 3);
        
        // Inicializar reloj
        reloj = new Reloj(DURACION_CICLO_MS);
        
        // Inicializar estado
        simulacionActiva = new AtomicBoolean(false);
        contadorProcesos = new AtomicLong(0);
        
        logger.info("Componentes del simulador inicializados");
    }
    
    /**
     * Crea la interfaz gráfica del simulador
     */
    private void crearInterfaz() {
        // Panel superior con controles
        JPanel panelControles = crearPanelControles();
        
        // Panel central con información
        JPanel panelInfo = crearPanelInfo();
        
        // Panel inferior con logs
        JPanel panelLogs = crearPanelLogs();
        
        // Agregar componentes al frame
        add(panelControles, BorderLayout.NORTH);
        add(panelInfo, BorderLayout.CENTER);
        add(panelLogs, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    /**
     * Crea el panel de controles
     */
    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Controles de Simulación"));
        
        JButton btnIniciar = new JButton("Iniciar Simulación");
        JButton btnDetener = new JButton("Detener Simulación");
        JButton btnExportar = new JButton("Exportar Métricas");
        JButton btnLimpiar = new JButton("Limpiar Logs");
        
        btnIniciar.addActionListener(e -> iniciarSimulacion());
        btnDetener.addActionListener(e -> detenerSimulacion());
        btnExportar.addActionListener(e -> exportarMetricas());
        btnLimpiar.addActionListener(e -> limpiarLogs());
        
        panel.add(btnIniciar);
        panel.add(btnDetener);
        panel.add(btnExportar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    /**
     * Crea el panel de información
     */
    private JPanel crearPanelInfo() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Información del Sistema"));
        
        // Estado
        JPanel panelEstado = new JPanel(new FlowLayout());
        panelEstado.add(new JLabel("Estado:"));
        labelEstado = new JLabel("Detenido");
        panelEstado.add(labelEstado);
        
        // Ciclo
        JPanel panelCiclo = new JPanel(new FlowLayout());
        panelCiclo.add(new JLabel("Ciclo:"));
        labelCiclo = new JLabel("0");
        panelCiclo.add(labelCiclo);
        
        // Procesos
        JPanel panelProcesos = new JPanel(new FlowLayout());
        panelProcesos.add(new JLabel("Procesos:"));
        labelProcesos = new JLabel("0");
        panelProcesos.add(labelProcesos);
        
        // Utilización CPU
        JPanel panelUtilizacion = new JPanel(new FlowLayout());
        panelUtilizacion.add(new JLabel("Utilización CPU:"));
        labelUtilizacion = new JLabel("0%");
        panelUtilizacion.add(labelUtilizacion);
        
        // Throughput
        JPanel panelThroughput = new JPanel(new FlowLayout());
        panelThroughput.add(new JLabel("Throughput:"));
        labelThroughput = new JLabel("0 procesos/seg");
        panelThroughput.add(labelThroughput);
        
        panel.add(panelEstado);
        panel.add(panelCiclo);
        panel.add(panelProcesos);
        panel.add(panelUtilizacion);
        panel.add(panelThroughput);
        
        return panel;
    }
    
    /**
     * Crea el panel de logs
     */
    private JPanel crearPanelLogs() {
        JPanel panel = new JPanel(new BorderLayout());
        
        areaLog = new JTextArea(10, 60);
        areaLog.setEditable(false);
        areaLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Logs del Sistema"));
        
        panel.add(scrollLog, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Configura el timer de simulación
     */
    private void configurarTimer() {
        timerSimulacion = new Timer(INTERVALO_ACTUALIZACION_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (simulacionActiva.get()) {
                    ejecutarCicloSimulacion();
                    actualizarInterfaz();
                }
            }
        });
    }
    
    /**
     * Inicia la simulación
     */
    private void iniciarSimulacion() {
        if (!simulacionActiva.get()) {
            simulacionActiva.set(true);
            timerSimulacion.start();
            logger.info("Simulación iniciada");
            actualizarEstado("Simulación activa");
        }
    }
    
    /**
     * Detiene la simulación
     */
    private void detenerSimulacion() {
        simulacionActiva.set(false);
        timerSimulacion.stop();
        logger.info("Simulación detenida");
        actualizarEstado("Simulación detenida");
    }
    
    /**
     * Exporta las métricas a archivos
     */
    private void exportarMetricas() {
        logger.info("Iniciando exportación de métricas");
        
        boolean csvExitoso = metricas.exportarCSV("metricas_simulacion.csv");
        boolean jsonExitoso = metricas.exportarJSON("metricas_simulacion.json");
        
        if (csvExitoso && jsonExitoso) {
            logger.info("Métricas exportadas correctamente");
            mostrarMensaje("Métricas exportadas correctamente a:\n- metricas_simulacion.csv\n- metricas_simulacion.json");
        } else {
            logger.warn("Error al exportar algunas métricas");
            mostrarMensaje("Error al exportar métricas", "Error");
        }
    }
    
    /**
     * Limpia los logs de la interfaz
     */
    private void limpiarLogs() {
        areaLog.setText("");
        logger.info("Logs de la interfaz limpiados");
    }
    
    /**
     * Ejecuta un ciclo de simulación
     */
    private void ejecutarCicloSimulacion() {
        // Avanzar reloj
        reloj.avanzarCiclo();
        
        // Simular trabajo de CPU (70% utilización promedio)
        if (Math.random() < 0.7) {
            metricas.registrarUsoCPU(1);
        } else {
            metricas.registrarIdle(1);
        }
        
        // Simular cambios de contexto ocasionalmente
        if (Math.random() < 0.1) {
            metricas.registrarCambioContexto();
        }
        
        // Simular llegada de procesos ocasionalmente
        if (Math.random() < 0.3) {
            simularLlegadaProceso();
        }
        
        // Simular terminación de procesos ocasionalmente
        if (Math.random() < 0.2) {
            simularTerminacionProceso();
        }
        
        // Simular I/O ocasionalmente
        if (Math.random() < 0.15) {
            simularOperacionIO();
        }
        
        // Tomar muestra de métricas
        metricas.muestrear(reloj.getCicloActual());
    }
    
    /**
     * Simula la llegada de un nuevo proceso
     */
    private void simularLlegadaProceso() {
        long id = contadorProcesos.incrementAndGet();
        logger.info("Nuevo proceso creado: Proceso" + id + " (ID: " + id + ")");
    }
    
    /**
     * Simula la terminación de un proceso
     */
    private void simularTerminacionProceso() {
        if (contadorProcesos.get() > 0) {
            long id = (long)(Math.random() * contadorProcesos.get()) + 1;
            logger.info("Proceso terminado: Proceso" + id + " (ID: " + id + ")");
        }
    }
    
    /**
     * Simula una operación de I/O
     */
    private void simularOperacionIO() {
        if (contadorProcesos.get() > 0) {
            long id = (long)(Math.random() * contadorProcesos.get()) + 1;
            
            logger.info("Iniciando operación I/O para proceso: Proceso" + id);
            
            // Crear handler para I/O
            IOCompletionHandler handler = new IOCompletionHandler() {
                @Override
                public void onIOComplete(PCB pcb) {
                    logger.info("Operación I/O completada para proceso: " + pcb.getNombre());
                }
            };
            
            // Crear PCB simple para la demostración
            PCB proceso = new PCB();
            proceso.setNombre("Proceso" + id);
            
            // Crear y ejecutar hilo de I/O
            IOThread ioThread = new IOThread(proceso, 5, reloj, handler);
            Thread thread = new Thread(ioThread);
            thread.start();
        }
    }
    
    /**
     * Actualiza la interfaz gráfica
     */
    private void actualizarInterfaz() {
        // Actualizar labels de estado
        actualizarEstado();
        
        // Actualizar área de logs
        actualizarLogs();
    }
    
    /**
     * Actualiza los labels de estado
     */
    private void actualizarEstado() {
        labelCiclo.setText(String.valueOf(reloj.getCicloActual()));
        labelProcesos.setText(String.valueOf(contadorProcesos.get()));
        
        MetricasRendimiento.Snapshot snapshot = metricas.snapshot();
        labelUtilizacion.setText(String.format("%.1f%%", snapshot.utilizacionCPU * 100));
        labelThroughput.setText(String.format("%.2f procesos/seg", snapshot.throughput));
    }
    
    /**
     * Actualiza el estado específico
     */
    private void actualizarEstado(String estado) {
        labelEstado.setText(estado);
    }
    
    /**
     * Actualiza el área de logs con información del sistema
     */
    private void actualizarLogs() {
        MetricasRendimiento.Snapshot snapshot = metricas.snapshot();
        
        StringBuilder logInfo = new StringBuilder();
        logInfo.append("=== MÉTRICAS ACTUALES ===\n");
        logInfo.append(String.format("Ciclos Totales: %d\n", snapshot.ciclosTotales));
        logInfo.append(String.format("Ciclos CPU: %d\n", snapshot.ciclosCPU));
        logInfo.append(String.format("Utilización CPU: %.2f%%\n", snapshot.utilizacionCPU * 100));
        logInfo.append(String.format("Throughput: %.2f procesos/seg\n", snapshot.throughput));
        logInfo.append(String.format("Procesos Terminados: %d\n", snapshot.procesosTerminados));
        logInfo.append(String.format("Cambios de Contexto: %d\n", snapshot.cambiosContexto));
        logInfo.append("========================\n");
        
        areaLog.setText(logInfo.toString());
    }
    
    /**
     * Muestra un mensaje al usuario
     */
    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra un mensaje de error al usuario
     */
    private void mostrarMensaje(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Método principal
     */
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Ejecutar en EDT
        SwingUtilities.invokeLater(() -> {
            try {
                SimuladorSimple simulador = new SimuladorSimple();
                simulador.setVisible(true);
                
                // Mostrar mensaje de bienvenida
                JOptionPane.showMessageDialog(simulador, 
                    "¡Bienvenido al Simulador de Planificación!\n\n" +
                    "Este simulador demuestra el funcionamiento de:\n" +
                    "• IOThread - Hilos de I/O con callbacks\n" +
                    "• MetricasRendimiento - Sistema de métricas\n" +
                    "• Logger - Sistema de logging thread-safe\n\n" +
                    "Haz clic en 'Iniciar Simulación' para comenzar.",
                    "Simulador de Planificación",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error al inicializar el simulador: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

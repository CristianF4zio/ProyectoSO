package main;

import simuladorPlanificacion.io.IOCompletionHandler;
import simuladorPlanificacion.io.IOThread;
import simuladorPlanificacion.metricas.MetricasRendimiento;
import simuladorPlanificacion.grafica.GraficadorMetricas;
import simuladorPlanificacion.logging.Logger;
import main.modelo.PCB;
import main.modelo.EstadoProceso;
import main.modelo.TipoProceso;
import main.gestor.Reloj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Clase principal que demuestra el funcionamiento de todos los componentes
 * implementados del simulador de planificación
 */
public class SimuladorMain extends JFrame {
    
    // Componentes del simulador
    private MetricasRendimiento metricas;
    private Logger logger;
    private Reloj reloj;
    
    // Estado de la simulación
    private AtomicBoolean simulacionActiva;
    private AtomicLong contadorProcesos;
    private Timer timerSimulacion;
    
    // Componentes de la interfaz
    private JPanel panelUtilizacion;
    private JPanel panelThroughput;
    private JTextArea areaLog;
    private JLabel labelEstado;
    private JLabel labelCiclo;
    private JLabel labelProcesos;
    
    // Configuración
    private static final int DURACION_CICLO_MS = 100; // 100ms por ciclo
    private static final int INTERVALO_ACTUALIZACION_MS = 500; // Actualizar cada 500ms
    
    public SimuladorMain() {
        super("Simulador de Planificación - Demostración de Componentes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Inicializar componentes
        inicializarComponentes();
        
        // Crear interfaz
        crearInterfaz();
        
        // Configurar timer de simulación
        configurarTimer();
        
        logger.info("Simulador inicializado correctamente");
    }
    
    /**
     * Inicializa todos los componentes del simulador
     */
    private void inicializarComponentes() {
        // Inicializar métricas
        metricas = new MetricasRendimiento();
        metricas.setCiclosPorSegundoSimulado(1000); // 1000 ciclos por segundo simulado
        
        // Inicializar logger
        logger = Logger.get("SimuladorMain");
        logger.setNivel(Logger.INFO);
        logger.setArchivo("logs/simulador_demo.log");
        logger.habilitarRotacion(1048576, 3); // 1MB, 3 archivos
        
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
        
        // Panel central con gráficas
        JPanel panelGraficas = crearPanelGraficas();
        
        // Panel inferior con logs y estado
        JPanel panelInferior = crearPanelInferior();
        
        // Agregar componentes al frame
        add(panelControles, BorderLayout.NORTH);
        add(panelGraficas, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
        
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
        JButton btnPausar = new JButton("Pausar/Reanudar");
        JButton btnExportar = new JButton("Exportar Métricas");
        JButton btnLimpiar = new JButton("Limpiar Logs");
        
        btnIniciar.addActionListener(e -> iniciarSimulacion());
        btnDetener.addActionListener(e -> detenerSimulacion());
        btnPausar.addActionListener(e -> pausarSimulacion());
        btnExportar.addActionListener(e -> exportarMetricas());
        btnLimpiar.addActionListener(e -> limpiarLogs());
        
        panel.add(btnIniciar);
        panel.add(btnDetener);
        panel.add(btnPausar);
        panel.add(btnExportar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    /**
     * Crea el panel de gráficas
     */
    private JPanel crearPanelGraficas() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        
        // Gráfica de utilización
        panelUtilizacion = (JPanel) GraficadorMetricas.crearGraficoUtilizacion(metricas.snapshot());
        panelUtilizacion.setBorder(BorderFactory.createTitledBorder("Utilización de CPU (%)"));
        panelUtilizacion.setPreferredSize(new Dimension(400, 300));
        
        // Gráfica de throughput
        panelThroughput = (JPanel) GraficadorMetricas.crearGraficoThroughput(metricas.snapshot());
        panelThroughput.setBorder(BorderFactory.createTitledBorder("Throughput (procesos/seg)"));
        panelThroughput.setPreferredSize(new Dimension(400, 300));
        
        panel.add(panelUtilizacion);
        panel.add(panelThroughput);
        
        return panel;
    }
    
    /**
     * Crea el panel inferior con logs y estado
     */
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de estado
        JPanel panelEstado = new JPanel(new FlowLayout());
        labelEstado = new JLabel("Estado: Detenido");
        labelCiclo = new JLabel("Ciclo: 0");
        labelProcesos = new JLabel("Procesos: 0");
        
        panelEstado.add(labelEstado);
        panelEstado.add(labelCiclo);
        panelEstado.add(labelProcesos);
        
        // Área de logs
        areaLog = new JTextArea(8, 60);
        areaLog.setEditable(false);
        areaLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Logs del Sistema"));
        
        panel.add(panelEstado, BorderLayout.NORTH);
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
     * Pausa o reanuda la simulación
     */
    private void pausarSimulacion() {
        if (simulacionActiva.get()) {
            simulacionActiva.set(false);
            timerSimulacion.stop();
            logger.info("Simulación pausada");
            actualizarEstado("Simulación pausada");
        } else {
            simulacionActiva.set(true);
            timerSimulacion.start();
            logger.info("Simulación reanudada");
            actualizarEstado("Simulación activa");
        }
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
        PCB proceso = new PCB((int)id, "Proceso" + id, 
                             (int)(Math.random() * 100) + 50, // 50-150 instrucciones
                             TipoProceso.CPU_BOUND, 
                             10, // K_excepcion
                             5,  // S_servicio
                             (int)(Math.random() * 5) + 1); // Prioridad 1-5
        
        logger.info("Nuevo proceso creado: " + proceso.getNombre() + " (ID: " + id + ")");
    }
    
    /**
     * Simula la terminación de un proceso
     */
    private void simularTerminacionProceso() {
        if (contadorProcesos.get() > 0) {
            long id = (long)(Math.random() * contadorProcesos.get()) + 1;
            PCB proceso = new PCB((int)id, "Proceso" + id, 0, null, 0, 0, 0);
            metricas.registrarTerminado(proceso);
            logger.info("Proceso terminado: " + proceso.getNombre() + " (ID: " + id + ")");
        }
    }
    
    /**
     * Simula una operación de I/O
     */
    private void simularOperacionIO() {
        if (contadorProcesos.get() > 0) {
            long id = (long)(Math.random() * contadorProcesos.get()) + 1;
            PCB proceso = new PCB((int)id, "Proceso" + id, 0, TipoProceso.IO_BOUND, 10, 5, 1);
            
            logger.info("Iniciando operación I/O para proceso: " + proceso.getNombre());
            
            // Crear handler para I/O
            IOCompletionHandler handler = new IOCompletionHandler() {
                @Override
                public void onIOComplete(PCB pcb) {
                    logger.info("Operación I/O completada para proceso: " + pcb.getNombre());
                }
            };
            
            // Crear y ejecutar hilo de I/O
            IOThread ioThread = new IOThread(proceso, proceso.getS_servicio(), reloj, handler);
            Thread thread = new Thread(ioThread);
            thread.start();
        }
    }
    
    /**
     * Actualiza la interfaz gráfica
     */
    private void actualizarInterfaz() {
        // Actualizar gráficas
        actualizarGraficas();
        
        // Actualizar labels de estado
        actualizarEstado();
        
        // Actualizar área de logs
        actualizarLogs();
    }
    
    /**
     * Actualiza las gráficas con los datos más recientes
     */
    private void actualizarGraficas() {
        MetricasRendimiento.Snapshot snapshot = metricas.snapshot();
        
        // Actualizar gráfica de utilización
        panelUtilizacion.removeAll();
        panelUtilizacion.add((JPanel) GraficadorMetricas.crearGraficoUtilizacion(snapshot));
        panelUtilizacion.revalidate();
        panelUtilizacion.repaint();
        
        // Actualizar gráfica de throughput
        panelThroughput.removeAll();
        panelThroughput.add((JPanel) GraficadorMetricas.crearGraficoThroughput(snapshot));
        panelThroughput.revalidate();
        panelThroughput.repaint();
    }
    
    /**
     * Actualiza los labels de estado
     */
    private void actualizarEstado() {
        labelCiclo.setText("Ciclo: " + reloj.getCicloActual());
        labelProcesos.setText("Procesos: " + contadorProcesos.get());
    }
    
    /**
     * Actualiza el estado específico
     */
    private void actualizarEstado(String estado) {
        labelEstado.setText("Estado: " + estado);
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
                SimuladorMain simulador = new SimuladorMain();
                simulador.setVisible(true);
                
                // Mostrar mensaje de bienvenida
                JOptionPane.showMessageDialog(simulador, 
                    "¡Bienvenido al Simulador de Planificación!\n\n" +
                    "Este simulador demuestra el funcionamiento de:\n" +
                    "• IOThread - Hilos de I/O con callbacks\n" +
                    "• MetricasRendimiento - Sistema de métricas\n" +
                    "• GraficadorMetricas - Visualización de gráficas\n" +
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

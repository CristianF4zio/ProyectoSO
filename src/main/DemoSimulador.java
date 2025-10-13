package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Demostración del simulador de planificación
 * Versión simplificada que funciona independientemente
 */
public class DemoSimulador extends JFrame {

    // Estado de la simulación
    private AtomicBoolean simulacionActiva;
    private AtomicLong contadorProcesos;
    private AtomicLong ciclosTotales;
    private AtomicLong ciclosCPU;
    private AtomicLong procesosTerminados;
    private AtomicLong cambiosContexto;

    // Componentes de la interfaz
    private JTextArea areaLog;
    private JLabel labelEstado;
    private JLabel labelCiclo;
    private JLabel labelProcesos;
    private JLabel labelUtilizacion;
    private JLabel labelThroughput;
    private Timer timerSimulacion;

    // Configuración
    private static final int INTERVALO_ACTUALIZACION_MS = 1000;
    private static final int CICLOS_POR_SEGUNDO = 1000;

    public DemoSimulador() {
        super("Simulador de Planificación - Demostración");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inicializar estado
        inicializarEstado();

        // Crear interfaz
        crearInterfaz();

        // Configurar timer de simulación
        configurarTimer();

        log("Simulador de demostración inicializado correctamente");
    }

    /**
     * Inicializa el estado del simulador
     */
    private void inicializarEstado() {
        simulacionActiva = new AtomicBoolean(false);
        contadorProcesos = new AtomicLong(0);
        ciclosTotales = new AtomicLong(0);
        ciclosCPU = new AtomicLong(0);
        procesosTerminados = new AtomicLong(0);
        cambiosContexto = new AtomicLong(0);
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
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
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

        // Cambios de contexto
        JPanel panelCambios = new JPanel(new FlowLayout());
        panelCambios.add(new JLabel("Cambios Contexto:"));
        JLabel labelCambios = new JLabel("0");
        panelCambios.add(labelCambios);

        panel.add(panelEstado);
        panel.add(panelCiclo);
        panel.add(panelProcesos);
        panel.add(panelUtilizacion);
        panel.add(panelThroughput);
        panel.add(panelCambios);

        return panel;
    }

    /**
     * Crea el panel de logs
     */
    private JPanel crearPanelLogs() {
        JPanel panel = new JPanel(new BorderLayout());

        areaLog = new JTextArea(8, 60);
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
            log("Simulación iniciada");
            actualizarEstado("Simulación activa");
        }
    }

    /**
     * Detiene la simulación
     */
    private void detenerSimulacion() {
        simulacionActiva.set(false);
        timerSimulacion.stop();
        log("Simulación detenida");
        actualizarEstado("Simulación detenida");
    }

    /**
     * Exporta las métricas a archivos
     */
    private void exportarMetricas() {
        log("Iniciando exportación de métricas");

        try {
            // Exportar CSV
            exportarCSV("metricas_demo.csv");

            // Exportar JSON
            exportarJSON("metricas_demo.json");

            log("Métricas exportadas correctamente");
            mostrarMensaje("Métricas exportadas correctamente a:\n- metricas_demo.csv\n- metricas_demo.json");

        } catch (IOException e) {
            log("Error al exportar métricas: " + e.getMessage());
            mostrarMensaje("Error al exportar métricas: " + e.getMessage(), "Error");
        }
    }

    /**
     * Exporta métricas a CSV
     */
    private void exportarCSV(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(
                    "Timestamp,CiclosTotales,CiclosCPU,UtilizacionCPU,Throughput,ProcesosTerminados,CambiosContexto\n");

            double utilizacion = calcularUtilizacionCPU();
            double throughput = calcularThroughput();

            writer.write(String.format("%s,%d,%d,%.4f,%.4f,%d,%d\n",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    ciclosTotales.get(),
                    ciclosCPU.get(),
                    utilizacion,
                    throughput,
                    procesosTerminados.get(),
                    cambiosContexto.get()));
        }
    }

    /**
     * Exporta métricas a JSON
     */
    private void exportarJSON(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            double utilizacion = calcularUtilizacionCPU();
            double throughput = calcularThroughput();

            writer.write("{\n");
            writer.write("  \"timestamp\": \"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    + "\",\n");
            writer.write("  \"metricas\": {\n");
            writer.write("    \"ciclosTotales\": " + ciclosTotales.get() + ",\n");
            writer.write("    \"ciclosCPU\": " + ciclosCPU.get() + ",\n");
            writer.write("    \"utilizacionCPU\": " + utilizacion + ",\n");
            writer.write("    \"throughput\": " + throughput + ",\n");
            writer.write("    \"procesosTerminados\": " + procesosTerminados.get() + ",\n");
            writer.write("    \"cambiosContexto\": " + cambiosContexto.get() + "\n");
            writer.write("  }\n");
            writer.write("}\n");
        }
    }

    /**
     * Limpia los logs de la interfaz
     */
    private void limpiarLogs() {
        areaLog.setText("");
        log("Logs de la interfaz limpiados");
    }

    /**
     * Ejecuta un ciclo de simulación
     */
    private void ejecutarCicloSimulacion() {
        // Avanzar ciclos
        ciclosTotales.addAndGet(10); // Simular 10 ciclos por actualización

        // Simular trabajo de CPU (70% utilización promedio)
        if (Math.random() < 0.7) {
            ciclosCPU.addAndGet(7); // 7 ciclos de CPU
        } else {
            // 3 ciclos idle
        }

        // Simular cambios de contexto ocasionalmente
        if (Math.random() < 0.1) {
            cambiosContexto.incrementAndGet();
        }

        // Simular llegada de procesos ocasionalmente
        if (Math.random() < 0.3) {
            simularLlegadaProceso();
        }

        // Simular terminación de procesos ocasionalmente
        if (Math.random() < 0.2) {
            simularTerminacionProceso();
        }

        // Simular operación de I/O ocasionalmente
        if (Math.random() < 0.15) {
            simularOperacionIO();
        }
    }

    /**
     * Simula la llegada de un nuevo proceso
     */
    private void simularLlegadaProceso() {
        long id = contadorProcesos.incrementAndGet();
        log("Nuevo proceso creado: Proceso" + id + " (ID: " + id + ")");
    }

    /**
     * Simula la terminación de un proceso
     */
    private void simularTerminacionProceso() {
        if (contadorProcesos.get() > 0) {
            long id = (long) (Math.random() * contadorProcesos.get()) + 1;
            procesosTerminados.incrementAndGet();
            log("Proceso terminado: Proceso" + id + " (ID: " + id + ")");
        }
    }

    /**
     * Simula una operación de I/O
     */
    private void simularOperacionIO() {
        if (contadorProcesos.get() > 0) {
            long id = (long) (Math.random() * contadorProcesos.get()) + 1;
            log("Iniciando operación I/O para proceso: Proceso" + id);

            // Simular I/O en un hilo separado
            Thread ioThread = new Thread(() -> {
                try {
                    Thread.sleep(100); // Simular tiempo de I/O
                    log("Operación I/O completada para proceso: Proceso" + id);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            ioThread.start();
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
        labelCiclo.setText(String.valueOf(ciclosTotales.get()));
        labelProcesos.setText(String.valueOf(contadorProcesos.get()));

        double utilizacion = calcularUtilizacionCPU();
        double throughput = calcularThroughput();

        labelUtilizacion.setText(String.format("%.1f%%", utilizacion * 100));
        labelThroughput.setText(String.format("%.2f procesos/seg", throughput));
    }

    /**
     * Actualiza el estado específico
     */
    private void actualizarEstado(String estado) {
        labelEstado.setText(estado);
    }

    /**
     * Calcula la utilización del CPU
     */
    private double calcularUtilizacionCPU() {
        if (ciclosTotales.get() == 0)
            return 0.0;
        return (double) ciclosCPU.get() / ciclosTotales.get();
    }

    /**
     * Calcula el throughput
     */
    private double calcularThroughput() {
        if (ciclosTotales.get() == 0)
            return 0.0;
        double segundosSimulados = (double) ciclosTotales.get() / CICLOS_POR_SEGUNDO;
        return (double) procesosTerminados.get() / Math.max(1, segundosSimulados);
    }

    /**
     * Actualiza el área de logs con información del sistema
     */
    private void actualizarLogs() {
        StringBuilder logInfo = new StringBuilder();
        logInfo.append("=== MÉTRICAS ACTUALES ===\n");
        logInfo.append(String.format("Ciclos Totales: %d\n", ciclosTotales.get()));
        logInfo.append(String.format("Ciclos CPU: %d\n", ciclosCPU.get()));
        logInfo.append(String.format("Utilización CPU: %.2f%%\n", calcularUtilizacionCPU() * 100));
        logInfo.append(String.format("Throughput: %.2f procesos/seg\n", calcularThroughput()));
        logInfo.append(String.format("Procesos Terminados: %d\n", procesosTerminados.get()));
        logInfo.append(String.format("Cambios de Contexto: %d\n", cambiosContexto.get()));
        logInfo.append("========================\n");

        areaLog.setText(logInfo.toString());
    }

    /**
     * Registra un mensaje en el log
     */
    private void log(String mensaje) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        // System.out.println("[" + timestamp + "] " + mensaje);
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
        System.out.println("Corriendo programa");

        // Ejecutar en EDT
        SwingUtilities.invokeLater(() -> {
            try {
                DemoSimulador simulador = new DemoSimulador();
                simulador.setVisible(true);

                // Interfaz gráfica lista

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

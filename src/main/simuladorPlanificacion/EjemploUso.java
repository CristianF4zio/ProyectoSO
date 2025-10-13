package simuladorPlanificacion;

import simuladorPlanificacion.io.IOCompletionHandler;
import simuladorPlanificacion.io.IOThread;
import simuladorPlanificacion.metricas.MetricasRendimiento;
import simuladorPlanificacion.grafica.GraficadorMetricas;
import simuladorPlanificacion.logging.Logger;
import main.modelo.PCB;
import main.gestor.Reloj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ejemplo de uso de los componentes implementados
 */
public class EjemploUso extends JFrame {
    
    private MetricasRendimiento metricas;
    private Logger logger;
    private Reloj reloj;
    private JPanel panelUtilizacion;
    private JPanel panelThroughput;
    
    public EjemploUso() {
        super("Simulador de Planificación - Ejemplo de Uso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Inicializar componentes
        inicializarComponentes();
        
        // Crear interfaz
        crearInterfaz();
        
        // Configurar timer para actualización
        configurarTimer();
    }
    
    private void inicializarComponentes() {
        // Inicializar métricas
        metricas = new MetricasRendimiento();
        metricas.setCiclosPorSegundoSimulado(1000);
        
        // Inicializar logger
        logger = Logger.get("EjemploUso");
        logger.setNivel(Logger.INFO);
        logger.setArchivo("logs/ejemplo.log");
        logger.habilitarRotacion(1048576, 3); // 1MB, 3 archivos
        
        // Inicializar reloj simulado
        reloj = new Reloj();
        
        logger.info("Componentes inicializados correctamente");
    }
    
    private void crearInterfaz() {
        // Panel superior con controles
        JPanel panelControles = new JPanel(new FlowLayout());
        
        JButton btnIniciar = new JButton("Iniciar Simulación");
        JButton btnDetener = new JButton("Detener Simulación");
        JButton btnExportar = new JButton("Exportar Métricas");
        
        btnIniciar.addActionListener(e -> iniciarSimulacion());
        btnDetener.addActionListener(e -> detenerSimulacion());
        btnExportar.addActionListener(e -> exportarMetricas());
        
        panelControles.add(btnIniciar);
        panelControles.add(btnDetener);
        panelControles.add(btnExportar);
        
        // Panel central con gráficas
        JPanel panelGraficas = new JPanel(new GridLayout(1, 2));
        
        // Gráfica de utilización
        panelUtilizacion = (JPanel) GraficadorMetricas.crearGraficoUtilizacion(metricas.snapshot());
        panelUtilizacion.setBorder(BorderFactory.createTitledBorder("Utilización de CPU"));
        
        // Gráfica de throughput
        panelThroughput = (JPanel) GraficadorMetricas.crearGraficoThroughput(metricas.snapshot());
        panelThroughput.setBorder(BorderFactory.createTitledBorder("Throughput"));
        
        panelGraficas.add(panelUtilizacion);
        panelGraficas.add(panelThroughput);
        
        // Panel inferior con logs
        JTextArea areaLog = new JTextArea(10, 50);
        areaLog.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Logs del Sistema"));
        
        // Agregar componentes al frame
        add(panelControles, BorderLayout.NORTH);
        add(panelGraficas, BorderLayout.CENTER);
        add(scrollLog, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void configurarTimer() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarMetricas();
                actualizarGraficas();
            }
        });
        timer.start();
    }
    
    private void iniciarSimulacion() {
        logger.info("Iniciando simulación de planificación");
        
        // Simular algunos procesos
        simularProcesos();
    }
    
    private void detenerSimulacion() {
        logger.info("Deteniendo simulación");
        // Lógica para detener la simulación
    }
    
    private void exportarMetricas() {
        logger.info("Exportando métricas");
        
        boolean csvExitoso = metricas.exportarCSV("metricas.csv");
        boolean jsonExitoso = metricas.exportarJSON("metricas.json");
        
        if (csvExitoso && jsonExitoso) {
            logger.info("Métricas exportadas correctamente");
            JOptionPane.showMessageDialog(this, "Métricas exportadas correctamente");
        } else {
            logger.warn("Error al exportar algunas métricas");
            JOptionPane.showMessageDialog(this, "Error al exportar métricas");
        }
    }
    
    private void simularProcesos() {
        // Crear algunos procesos de ejemplo
        for (int i = 0; i < 5; i++) {
            PCB proceso = new PCB(i, "Proceso" + i, 100, null, 10, 5, 1);
            
            // Simular I/O si es necesario
            if (i % 2 == 0) {
                simularIO(proceso);
            }
            
            // Registrar en métricas
            metricas.registrarTerminado(proceso);
        }
    }
    
    private void simularIO(PCB proceso) {
        logger.info("Iniciando I/O para proceso " + proceso.getId());
        
        // Crear handler para I/O
        IOCompletionHandler handler = new IOCompletionHandler() {
            @Override
            public void onIOComplete(PCB pcb) {
                logger.info("I/O completado para proceso " + pcb.getId());
                // Aquí se movería el proceso de vuelta a la cola de listos
            }
        };
        
        // Crear y ejecutar hilo de I/O
        IOThread ioThread = new IOThread(proceso, proceso.getS_servicio(), reloj, handler);
        Thread thread = new Thread(ioThread);
        thread.start();
    }
    
    private void actualizarMetricas() {
        // Simular ciclos de CPU
        metricas.registrarUsoCPU(50);
        metricas.registrarIdle(10);
        metricas.registrarCambioContexto();
        
        // Tomar muestra
        metricas.muestrear(reloj.getCicloActual());
        
        // Avanzar reloj
        reloj.avanzarCiclo();
    }
    
    private void actualizarGraficas() {
        // Obtener snapshot actual
        MetricasRendimiento.Snapshot snapshot = metricas.snapshot();
        
        // Actualizar gráficas
        panelUtilizacion.removeAll();
        panelUtilizacion.add((JPanel) GraficadorMetricas.crearGraficoUtilizacion(snapshot));
        panelUtilizacion.revalidate();
        panelUtilizacion.repaint();
        
        panelThroughput.removeAll();
        panelThroughput.add((JPanel) GraficadorMetricas.crearGraficoThroughput(snapshot));
        panelThroughput.revalidate();
        panelThroughput.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new EjemploUso().setVisible(true);
        });
    }
}

package main.interfaz;

import main.core.SistemaOperativoSimulado;
import main.metricas.MetricasRendimiento;
import main.utilidades.Utilidades;
import javax.swing.*;
import java.awt.*;

/**
 * Panel que muestra las métricas de rendimiento del sistema
 * Incluye throughput, utilización CPU, equidad, tiempos de espera y respuesta
 */
public class PanelMetricas extends JPanel {

    private JLabel lblThroughput;
    private JLabel lblUtilizacionCPU;
    private JLabel lblTiempoEspera;
    private JLabel lblTiempoRespuesta;
    private JLabel lblEquidad;
    private JLabel lblProcesosCompletados;
    private JProgressBar barUtilizacionCPU;
    private JProgressBar barEquidad;

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
        lblThroughput = new JLabel("0.0000");
        lblThroughput.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        lblUtilizacionCPU = new JLabel("0.00%");
        lblUtilizacionCPU.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        lblTiempoEspera = new JLabel("0.00");
        lblTiempoEspera.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        lblTiempoRespuesta = new JLabel("0.00");
        lblTiempoRespuesta.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        lblEquidad = new JLabel("100.00");
        lblEquidad.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        lblProcesosCompletados = new JLabel("0");
        lblProcesosCompletados.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        barUtilizacionCPU = new JProgressBar(0, 100);
        barUtilizacionCPU.setStringPainted(true);
        
        barEquidad = new JProgressBar(0, 100);
        barEquidad.setStringPainted(true);
    }

    /**
     * Configura el layout del panel
     */
    private void configurarLayout() {
        setBorder(BorderFactory.createTitledBorder("Métricas de Rendimiento"));
        setLayout(new GridLayout(8, 2, 10, 5));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Métricas de Rendimiento"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Throughput
        add(new JLabel("Throughput (proc/ciclo):"));
        add(lblThroughput);
        
        // Utilización CPU
        add(new JLabel("Utilización CPU:"));
        add(lblUtilizacionCPU);
        
        add(new JLabel(""));
        add(barUtilizacionCPU);
        
        // Tiempo de espera
        add(new JLabel("Tiempo Espera Promedio:"));
        add(lblTiempoEspera);
        
        // Tiempo de respuesta
        add(new JLabel("Tiempo Respuesta Promedio:"));
        add(lblTiempoRespuesta);
        
        // Equidad
        add(new JLabel("Equidad:"));
        add(lblEquidad);
        
        add(new JLabel(""));
        add(barEquidad);
        
        // Procesos completados
        add(new JLabel("Procesos Completados:"));
        add(lblProcesosCompletados);
    }

    /**
     * Actualiza las métricas mostradas
     * 
     * @param sistema Sistema operativo simulado
     */
    public void actualizar(SistemaOperativoSimulado sistema) {
        MetricasRendimiento metricas = sistema.getMetricas();
        
        // Actualizar labels
        lblThroughput.setText(String.format("%.4f", metricas.getThroughput()));
        lblUtilizacionCPU.setText(Utilidades.formatearPorcentaje(metricas.getUtilizacionCPU()));
        lblTiempoEspera.setText(String.format("%.2f ciclos", metricas.getTiempoEsperaPromedio()));
        lblTiempoRespuesta.setText(String.format("%.2f ciclos", metricas.getTiempoRespuestaPromedio()));
        lblEquidad.setText(String.format("%.2f", metricas.getEquidad()));
        lblProcesosCompletados.setText(String.valueOf(metricas.getProcesosCompletados()));
        
        // Actualizar barras de progreso
        int utilizacion = (int) Math.round(metricas.getUtilizacionCPU());
        barUtilizacionCPU.setValue(utilizacion);
        barUtilizacionCPU.setString(utilizacion + "%");
        
        // Color de la barra según utilización
        if (utilizacion < 50) {
            barUtilizacionCPU.setForeground(Color.RED);
        } else if (utilizacion < 80) {
            barUtilizacionCPU.setForeground(Color.ORANGE);
        } else {
            barUtilizacionCPU.setForeground(Color.GREEN);
        }
        
        int equidad = (int) Math.round(metricas.getEquidad());
        barEquidad.setValue(equidad);
        barEquidad.setString(equidad + "%");
        
        // Color de la barra según equidad
        if (equidad < 50) {
            barEquidad.setForeground(Color.RED);
        } else if (equidad < 80) {
            barEquidad.setForeground(Color.ORANGE);
        } else {
            barEquidad.setForeground(Color.GREEN);
        }
    }
}

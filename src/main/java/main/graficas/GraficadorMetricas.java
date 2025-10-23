package main.graficas;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;

import javax.swing.*;
import java.awt.*;
import main.estructuras.ListaSimple;
import main.estructuras.MapaSimple;

public class GraficadorMetricas {
    private DefaultCategoryDataset datasetThroughput;
    private DefaultCategoryDataset datasetCPUUtil;
    private DefaultCategoryDataset datasetTiempoEspera;
    private XYSeriesCollection datasetTiempoReal;
    private XYSeries serieThroughput;
    private XYSeries serieCPUUtil;
    private XYSeries serieTiempoEspera;

    private JFrame ventanaGraficas;
    private JTabbedPane tabbedPane;
    private ChartPanel panelThroughput;
    private ChartPanel panelCPUUtil;
    private ChartPanel panelTiempoEspera;
    private ChartPanel panelTiempoReal;

    private int cicloActual;
    private MapaSimple<String, Double> metricasPorAlgoritmo;

    public GraficadorMetricas() {
        this.cicloActual = 0;
        this.metricasPorAlgoritmo = new MapaSimple<>();
        inicializarDatasets();
        crearVentana();
    }

    private void inicializarDatasets() {
        // Datasets para gráficas de barras
        datasetThroughput = new DefaultCategoryDataset();
        datasetCPUUtil = new DefaultCategoryDataset();
        datasetTiempoEspera = new DefaultCategoryDataset();

        // Datasets para gráfica de tiempo real
        datasetTiempoReal = new XYSeriesCollection();
        serieThroughput = new XYSeries("Throughput");
        serieCPUUtil = new XYSeries("Utilización CPU");
        serieTiempoEspera = new XYSeries("Tiempo de Espera");

        datasetTiempoReal.addSeries(serieThroughput);
        datasetTiempoReal.addSeries(serieCPUUtil);
        datasetTiempoReal.addSeries(serieTiempoEspera);
    }

    private void crearVentana() {
        ventanaGraficas = new JFrame("Gráficas de Rendimiento del Sistema");
        ventanaGraficas.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        ventanaGraficas.setSize(1000, 700);
        ventanaGraficas.setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Crear gráficas
        crearGraficaThroughput();
        crearGraficaCPUUtil();
        crearGraficaTiempoEspera();
        crearGraficaTiempoReal();

        // Agregar pestañas
        tabbedPane.addTab("Throughput", panelThroughput);
        tabbedPane.addTab("Utilización CPU", panelCPUUtil);
        tabbedPane.addTab("Tiempo de Espera", panelTiempoEspera);
        tabbedPane.addTab("Tiempo Real", panelTiempoReal);

        ventanaGraficas.add(tabbedPane);
    }

    private void crearGraficaThroughput() {
        JFreeChart chart = ChartFactory.createBarChart(
                "Throughput por Algoritmo",
                "Algoritmo",
                "Procesos/Ciclo",
                datasetThroughput,
                PlotOrientation.VERTICAL,
                true, true, false);

        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 12));

        panelThroughput = new ChartPanel(chart);
        panelThroughput.setPreferredSize(new Dimension(800, 600));
    }

    private void crearGraficaCPUUtil() {
        JFreeChart chart = ChartFactory.createBarChart(
                "Utilización de CPU por Algoritmo",
                "Algoritmo",
                "Utilización (%)",
                datasetCPUUtil,
                PlotOrientation.VERTICAL,
                true, true, false);

        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 12));

        panelCPUUtil = new ChartPanel(chart);
        panelCPUUtil.setPreferredSize(new Dimension(800, 600));
    }

    private void crearGraficaTiempoEspera() {
        JFreeChart chart = ChartFactory.createBarChart(
                "Tiempo de Espera Promedio por Algoritmo",
                "Algoritmo",
                "Tiempo de Espera (ciclos)",
                datasetTiempoEspera,
                PlotOrientation.VERTICAL,
                true, true, false);

        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 12));

        panelTiempoEspera = new ChartPanel(chart);
        panelTiempoEspera.setPreferredSize(new Dimension(800, 600));
    }

    private void crearGraficaTiempoReal() {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Métricas en Tiempo Real",
                "Ciclo",
                "Valor",
                datasetTiempoReal,
                PlotOrientation.VERTICAL,
                true, true, false);

        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 12));

        // Personalizar la gráfica
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShapesVisible(2, true);
        plot.setRenderer(renderer);

        panelTiempoReal = new ChartPanel(chart);
        panelTiempoReal.setPreferredSize(new Dimension(800, 600));
    }

    public void actualizarMetricas(String algoritmo, double throughput, double cpuUtil, double tiempoEspera) {
        if (algoritmo == null)
            algoritmo = "Desconocido";

        // Actualizar datasets de barras
        if (datasetThroughput != null)
            datasetThroughput.setValue(throughput, "Throughput", algoritmo);
        if (datasetCPUUtil != null)
            datasetCPUUtil.setValue(cpuUtil, "Utilización CPU", algoritmo);
        if (datasetTiempoEspera != null)
            datasetTiempoEspera.setValue(tiempoEspera, "Tiempo de Espera", algoritmo);

        // Actualizar gráfica de tiempo real
        if (serieThroughput != null)
            serieThroughput.add(cicloActual, throughput);
        if (serieCPUUtil != null)
            serieCPUUtil.add(cicloActual, cpuUtil);
        if (serieTiempoEspera != null)
            serieTiempoEspera.add(cicloActual, tiempoEspera);

        // Mantener solo los últimos 50 puntos para mejor visualización
        if (serieThroughput != null && serieThroughput.getItemCount() > 50) {
            serieThroughput.remove(0);
        }
        if (serieCPUUtil != null && serieCPUUtil.getItemCount() > 50) {
            serieCPUUtil.remove(0);
        }
        if (serieTiempoEspera != null && serieTiempoEspera.getItemCount() > 50) {
            serieTiempoEspera.remove(0);
        }

        cicloActual++;
    }

    public void mostrarGraficas() {
        if (ventanaGraficas != null) {
            ventanaGraficas.setVisible(true);
        }
    }

    public void ocultarGraficas() {
        if (ventanaGraficas != null) {
            ventanaGraficas.setVisible(false);
        }
    }

    public void limpiarDatos() {
        if (datasetThroughput != null)
            datasetThroughput.clear();
        if (datasetCPUUtil != null)
            datasetCPUUtil.clear();
        if (datasetTiempoEspera != null)
            datasetTiempoEspera.clear();

        if (serieThroughput != null)
            serieThroughput.clear();
        if (serieCPUUtil != null)
            serieCPUUtil.clear();
        if (serieTiempoEspera != null)
            serieTiempoEspera.clear();

        cicloActual = 0;
        if (metricasPorAlgoritmo != null)
            metricasPorAlgoritmo.limpiar();
    }

    public void actualizarMetricasPorAlgoritmo(String algoritmo, MapaSimple<String, Double> metricas) {
        if (metricas != null) {
            ListaSimple<String> claves = metricas.claves();
            if (claves != null) {
                for (int i = 0; i < claves.tamaño(); i++) {
                    String clave = claves.obtener(i);
                    if (clave != null) {
                        metricasPorAlgoritmo.poner(clave, metricas.obtener(clave));
                    }
                }
            }
        }

        double throughput = (metricas != null && metricas.obtener("throughput") != null)
                ? metricas.obtener("throughput")
                : 0.0;
        double cpuUtil = (metricas != null && metricas.obtener("cpuUtil") != null) ? metricas.obtener("cpuUtil") : 0.0;
        double tiempoEspera = (metricas != null && metricas.obtener("tiempoEspera") != null)
                ? metricas.obtener("tiempoEspera")
                : 0.0;

        actualizarMetricas(algoritmo, throughput, cpuUtil, tiempoEspera);
    }

    public void exportarGraficas() {
        // Función para exportar gráficas como imagen
        // Implementación futura si es necesaria
    }
}

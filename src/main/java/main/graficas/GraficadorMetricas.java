package main.graficas;

import main.estructuras.MapaSimple;
import main.estructuras.ListaSimple;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class GraficadorMetricas {

    private MapaSimple<String, ListaSimple<Double>> datosThroughput;
    private MapaSimple<String, ListaSimple<Double>> datosCpuUtil;
    private MapaSimple<String, ListaSimple<Double>> datosTiempoEspera;
    
    private JFrame ventanaGraficas;

    public GraficadorMetricas() {
        this.datosThroughput = new MapaSimple<>();
        this.datosCpuUtil = new MapaSimple<>();
        this.datosTiempoEspera = new MapaSimple<>();
    }

    public void actualizarMetricasPorAlgoritmo(String algoritmo, MapaSimple<String, Double> metricas) {
        if (!datosThroughput.containsKey(algoritmo)) {
            datosThroughput.put(algoritmo, new ListaSimple<>());
            datosCpuUtil.put(algoritmo, new ListaSimple<>());
            datosTiempoEspera.put(algoritmo, new ListaSimple<>());
        }
        Double throughput = metricas.get("throughput");
        Double cpuUtil = metricas.get("cpuUtil");
        Double tiempoEspera = metricas.get("tiempoEspera");

        if (throughput != null) {
            datosThroughput.get(algoritmo).agregar(throughput);
        }
        if (cpuUtil != null) {
            datosCpuUtil.get(algoritmo).agregar(cpuUtil);
        }
        if (tiempoEspera != null) {
            datosTiempoEspera.get(algoritmo).agregar(tiempoEspera);
        }
    }

    public void mostrarGraficas() {
        if (ventanaGraficas != null && ventanaGraficas.isVisible()) {
            ventanaGraficas.toFront();
            return;
        }

        ventanaGraficas = new JFrame("Métricas de Rendimiento");
        ventanaGraficas.setSize(1000, 700);
        ventanaGraficas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaGraficas.setLayout(new BorderLayout());

        // Panel con pestañas para diferentes métricas
        JTabbedPane pestanas = new JTabbedPane();

        PanelGrafica panelThroughput = new PanelGrafica("Throughput (Procesos/Ciclo)", datosThroughput, Color.BLUE);
        PanelGrafica panelCpuUtil = new PanelGrafica("Utilización de CPU (%)", datosCpuUtil, Color.GREEN);
        PanelGrafica panelTiempoEspera = new PanelGrafica("Tiempo de Espera Promedio", datosTiempoEspera, Color.ORANGE);

        pestanas.addTab("Throughput", panelThroughput);
        pestanas.addTab("Utilización CPU", panelCpuUtil);
        pestanas.addTab("Tiempo de Espera", panelTiempoEspera);

        JPanel panelInfo = new JPanel();
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información"));
        panelInfo.add(new JLabel("Gráficas de métricas de rendimiento por algoritmo"));

        ventanaGraficas.add(pestanas, BorderLayout.CENTER);
        ventanaGraficas.add(panelInfo, BorderLayout.SOUTH);
        ventanaGraficas.setLocationRelativeTo(null);
        ventanaGraficas.setVisible(true);
    }

    public void limpiarDatos() {
        datosThroughput = new MapaSimple<>();
        datosCpuUtil = new MapaSimple<>();
        datosTiempoEspera = new MapaSimple<>();
    }

    private class PanelGrafica extends JPanel {
        
        private String titulo;
        private MapaSimple<String, ListaSimple<Double>> datos;
        private Color colorPrincipal;

        public PanelGrafica(String titulo, MapaSimple<String, ListaSimple<Double>> datos, Color colorPrincipal) {
            this.titulo = titulo;
            this.datos = datos;
            this.colorPrincipal = colorPrincipal;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Márgenes
            int margenIzq = 60;
            int margenDer = 40;
            int margenSup = 60;
            int margenInf = 60;

            int anchoGrafica = width - margenIzq - margenDer;
            int altoGrafica = height - margenSup - margenInf;

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            int anchoTitulo = fm.stringWidth(titulo);
            g2.drawString(titulo, (width - anchoTitulo) / 2, 30);

            g2.setStroke(new BasicStroke(2));
            g2.drawLine(margenIzq, margenSup, margenIzq, margenSup + altoGrafica);
            g2.drawLine(margenIzq, margenSup + altoGrafica, margenIzq + anchoGrafica, margenSup + altoGrafica);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString("Ciclos", width - margenDer - 40, height - 20);
            
            Graphics2D g2d = (Graphics2D) g2.create();
            g2d.rotate(-Math.PI / 2);
            g2d.drawString("Valor", -height / 2 - 20, 20);
            g2d.dispose();
            if (datos.size() == 0) {
                g2.setFont(new Font("Arial", Font.ITALIC, 14));
                g2.setColor(Color.GRAY);
                String mensaje = "No hay datos para mostrar";
                int anchoMensaje = g2.getFontMetrics().stringWidth(mensaje);
                g2.drawString(mensaje, (width - anchoMensaje) / 2, height / 2);
                return;
            }

            ListaSimple<String> algoritmos = datos.obtenerClaves();
            if (algoritmos.tamaño() == 0) return;
            double maxValor = 0.001;
            int maxPuntos = 0;
            
            for (int j = 0; j < algoritmos.tamaño(); j++) {
                String algoritmo = algoritmos.obtener(j);
                ListaSimple<Double> valores = datos.get(algoritmo);
                if (valores != null) {
                    maxPuntos = Math.max(maxPuntos, valores.tamaño());
                    for (int i = 0; i < valores.tamaño(); i++) {
                        Double valor = valores.obtener(i);
                        if (valor != null) {
                            maxValor = Math.max(maxValor, valor);
                        }
                    }
                }
            }

            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 5; i++) {
                int y = margenSup + altoGrafica - (i * altoGrafica / 5);
                g2.drawLine(margenIzq - 5, y, margenIzq, y);
                String label = String.format("%.2f", (maxValor * i / 5.0));
                g2.drawString(label, margenIzq - 45, y + 5);
            }

            g2.setColor(new Color(230, 230, 230));
            g2.setStroke(new BasicStroke(1));
            for (int i = 0; i <= 5; i++) {
                int y = margenSup + altoGrafica - (i * altoGrafica / 5);
                g2.drawLine(margenIzq, y, margenIzq + anchoGrafica, y);
            }
            Color[] colores = {
                new Color(31, 119, 180),   // Azul
                new Color(255, 127, 14),   // Naranja
                new Color(44, 160, 44),    // Verde
                new Color(214, 39, 40),    // Rojo
                new Color(148, 103, 189),  // Púrpura
                new Color(140, 86, 75),    // Marrón
                new Color(227, 119, 194)
            };

            int indiceColor = 0;
            int offsetLeyenda = 0;
            
            for (int k = 0; k < algoritmos.tamaño(); k++) {
                String algoritmo = algoritmos.obtener(k);
                ListaSimple<Double> valores = datos.get(algoritmo);
                if (valores == null || valores.tamaño() == 0) continue;

                Color color = colores[indiceColor % colores.length];
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2));
                for (int i = 0; i < valores.tamaño() - 1; i++) {
                    Double valor1 = valores.obtener(i);
                    Double valor2 = valores.obtener(i + 1);
                    
                    if (valor1 != null && valor2 != null) {
                        int x1 = margenIzq + (i * anchoGrafica / Math.max(1, maxPuntos - 1));
                        int y1 = margenSup + altoGrafica - (int)((valor1 / maxValor) * altoGrafica);
                        int x2 = margenIzq + ((i + 1) * anchoGrafica / Math.max(1, maxPuntos - 1));
                        int y2 = margenSup + altoGrafica - (int)((valor2 / maxValor) * altoGrafica);
                        
                        g2.drawLine(x1, y1, x2, y2);
                    }
                }

                for (int i = 0; i < valores.tamaño(); i++) {
                    Double valor = valores.obtener(i);
                    if (valor != null) {
                        int x = margenIzq + (i * anchoGrafica / Math.max(1, maxPuntos - 1));
                        int y = margenSup + altoGrafica - (int)((valor / maxValor) * altoGrafica);
                        g2.fillOval(x - 4, y - 4, 8, 8);
                    }
                }

                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                int xLeyenda = width - margenDer - 150;
                int yLeyenda = margenSup + 20 + (offsetLeyenda * 20);
                
                g2.fillRect(xLeyenda, yLeyenda - 8, 15, 10);
                g2.setColor(Color.BLACK);
                g2.drawString(algoritmo, xLeyenda + 20, yLeyenda);
                
                indiceColor++;
                offsetLeyenda++;
            }
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            int intervalo = Math.max(1, maxPuntos / 10);
            for (int i = 0; i <= maxPuntos; i += intervalo) {
                int x = margenIzq + (i * anchoGrafica / Math.max(1, maxPuntos));
                g2.drawLine(x, margenSup + altoGrafica, x, margenSup + altoGrafica + 5);
                g2.drawString(String.valueOf(i), x - 5, margenSup + altoGrafica + 20);
            }
        }
    }
}


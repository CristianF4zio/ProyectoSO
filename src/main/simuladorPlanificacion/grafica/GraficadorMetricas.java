package simuladorPlanificacion.grafica;

import simuladorPlanificacion.metricas.MetricasRendimiento;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Renderiza gráficas de Utilización CPU vs tiempo y Throughput vs tiempo
 */
public final class GraficadorMetricas {
    
    private static final int ANCHO_GRAFICO = 800;
    private static final int ALTO_GRAFICO = 400;
    private static final Color COLOR_UTILIZACION = Color.BLUE;
    private static final Color COLOR_THROUGHPUT = Color.RED;
    private static final Color COLOR_FONDO = Color.WHITE;
    private static final Color COLOR_GRID = Color.LIGHT_GRAY;
    
    /**
     * Crea un gráfico de utilización de CPU
     * 
     * @param snapshot Snapshot de métricas
     * @return JPanel con el gráfico de utilización
     */
    public static Object crearGraficoUtilizacion(MetricasRendimiento.Snapshot snapshot) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGraficoUtilizacion(g, snapshot);
            }
        };
        
        panel.setPreferredSize(new Dimension(ANCHO_GRAFICO, ALTO_GRAFICO));
        panel.setBackground(COLOR_FONDO);
        
        return panel;
    }
    
    /**
     * Crea un gráfico de throughput
     * 
     * @param snapshot Snapshot de métricas
     * @return JPanel con el gráfico de throughput
     */
    public static Object crearGraficoThroughput(MetricasRendimiento.Snapshot snapshot) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGraficoThroughput(g, snapshot);
            }
        };
        
        panel.setPreferredSize(new Dimension(ANCHO_GRAFICO, ALTO_GRAFICO));
        panel.setBackground(COLOR_FONDO);
        
        return panel;
    }
    
    /**
     * Dibuja el gráfico de utilización de CPU
     */
    private static void dibujarGraficoUtilizacion(Graphics g, MetricasRendimiento.Snapshot snapshot) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int padding = 50;
        int ancho = getWidth() - 2 * padding;
        int alto = getHeight() - 2 * padding;
        
        // Dibujar fondo y grid
        dibujarGrid(g2d, padding, ancho, alto);
        
        // Dibujar ejes
        dibujarEjes(g2d, padding, ancho, alto, "Ciclo", "Utilización CPU", true);
        
        // Dibujar datos
        if (snapshot.muestras.length > 0) {
            dibujarLineaUtilizacion(g2d, snapshot, padding, ancho, alto);
        }
        
        // Dibujar valor actual
        g2d.setColor(COLOR_UTILIZACION);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(String.format("Utilización Actual: %.2f%%", snapshot.utilizacionCPU * 100), 
                      10, 20);
    }
    
    /**
     * Dibuja el gráfico de throughput
     */
    private static void dibujarGraficoThroughput(Graphics g, MetricasRendimiento.Snapshot snapshot) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int padding = 50;
        int ancho = getWidth() - 2 * padding;
        int alto = getHeight() - 2 * padding;
        
        // Dibujar fondo y grid
        dibujarGrid(g2d, padding, ancho, alto);
        
        // Dibujar ejes
        dibujarEjes(g2d, padding, ancho, alto, "Ciclo", "Throughput (procesos/seg)", false);
        
        // Dibujar datos
        if (snapshot.muestras.length > 0) {
            dibujarLineaThroughput(g2d, snapshot, padding, ancho, alto);
        }
        
        // Dibujar valor actual
        g2d.setColor(COLOR_THROUGHPUT);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(String.format("Throughput Actual: %.2f procesos/seg", snapshot.throughput), 
                      10, 20);
    }
    
    /**
     * Dibuja la grilla del gráfico
     */
    private static void dibujarGrid(Graphics2D g2d, int padding, int ancho, int alto) {
        g2d.setColor(COLOR_GRID);
        g2d.setStroke(new BasicStroke(1));
        
        // Líneas verticales
        for (int i = 0; i <= 10; i++) {
            int x = padding + (i * ancho / 10);
            g2d.drawLine(x, padding, x, padding + alto);
        }
        
        // Líneas horizontales
        for (int i = 0; i <= 10; i++) {
            int y = padding + (i * alto / 10);
            g2d.drawLine(padding, y, padding + ancho, y);
        }
    }
    
    /**
     * Dibuja los ejes del gráfico
     */
    private static void dibujarEjes(Graphics2D g2d, int padding, int ancho, int alto, 
                                  String etiquetaX, String etiquetaY, boolean porcentaje) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        // Eje X
        g2d.drawLine(padding, padding + alto, padding + ancho, padding + alto);
        
        // Eje Y
        g2d.drawLine(padding, padding, padding, padding + alto);
        
        // Etiquetas de ejes
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(etiquetaX, padding + ancho/2 - 30, padding + alto + 30);
        
        // Rotar texto para eje Y
        FontMetrics fm = g2d.getFontMetrics();
        int yLabelWidth = fm.stringWidth(etiquetaY);
        g2d.drawString(etiquetaY, padding - yLabelWidth - 10, padding + alto/2 + 5);
        
        // Etiquetas de valores
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        
        // Eje Y (0 a 1 para utilización, 0 a max para throughput)
        for (int i = 0; i <= 10; i++) {
            String valor;
            if (porcentaje) {
                valor = String.format("%.0f%%", (i * 10.0));
            } else {
                valor = String.format("%.1f", (i * 0.1));
            }
            int y = padding + alto - (i * alto / 10);
            g2d.drawString(valor, padding - 30, y + 5);
        }
    }
    
    /**
     * Dibuja la línea de utilización de CPU
     */
    private static void dibujarLineaUtilizacion(Graphics2D g2d, MetricasRendimiento.Snapshot snapshot, 
                                             int padding, int ancho, int alto) {
        if (snapshot.muestras.length < 2) return;
        
        g2d.setColor(COLOR_UTILIZACION);
        g2d.setStroke(new BasicStroke(2));
        
        // Encontrar rango de ciclos
        long minCiclo = snapshot.muestras[0].ciclo;
        long maxCiclo = snapshot.muestras[snapshot.muestras.length - 1].ciclo;
        long rangoCiclos = Math.max(1, maxCiclo - minCiclo);
        
        // Dibujar línea
        for (int i = 0; i < snapshot.muestras.length - 1; i++) {
            int x1 = padding + (int)((snapshot.muestras[i].ciclo - minCiclo) * ancho / rangoCiclos);
            int y1 = padding + alto - (int)(snapshot.muestras[i].utilizacionCPU * alto);
            
            int x2 = padding + (int)((snapshot.muestras[i + 1].ciclo - minCiclo) * ancho / rangoCiclos);
            int y2 = padding + alto - (int)(snapshot.muestras[i + 1].utilizacionCPU * alto);
            
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
    
    /**
     * Dibuja la línea de throughput
     */
    private static void dibujarLineaThroughput(Graphics2D g2d, MetricasRendimiento.Snapshot snapshot, 
                                            int padding, int ancho, int alto) {
        if (snapshot.muestras.length < 2) return;
        
        g2d.setColor(COLOR_THROUGHPUT);
        g2d.setStroke(new BasicStroke(2));
        
        // Encontrar rango de ciclos y throughput
        long minCiclo = snapshot.muestras[0].ciclo;
        long maxCiclo = snapshot.muestras[snapshot.muestras.length - 1].ciclo;
        long rangoCiclos = Math.max(1, maxCiclo - minCiclo);
        
        double maxThroughput = 0;
        for (var muestra : snapshot.muestras) {
            maxThroughput = Math.max(maxThroughput, muestra.throughput);
        }
        maxThroughput = Math.max(1, maxThroughput);
        
        // Dibujar línea
        for (int i = 0; i < snapshot.muestras.length - 1; i++) {
            int x1 = padding + (int)((snapshot.muestras[i].ciclo - minCiclo) * ancho / rangoCiclos);
            int y1 = padding + alto - (int)(snapshot.muestras[i].throughput * alto / maxThroughput);
            
            int x2 = padding + (int)((snapshot.muestras[i + 1].ciclo - minCiclo) * ancho / rangoCiclos);
            int y2 = padding + alto - (int)(snapshot.muestras[i + 1].throughput * alto / maxThroughput);
            
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
    
    /**
     * Exporta un gráfico como imagen PNG
     * 
     * @param grafico Componente gráfico a exportar
     * @param pathPng Ruta del archivo PNG
     * @return true si se exportó correctamente
     */
    public static boolean exportarImagen(Object grafico, String pathPng) {
        try {
            if (!(grafico instanceof JPanel)) {
                return false;
            }
            
            JPanel panel = (JPanel) grafico;
            BufferedImage imagen = new BufferedImage(
                panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g2d = imagen.createGraphics();
            panel.paint(g2d);
            g2d.dispose();
            
            File archivo = new File(pathPng);
            archivo.getParentFile().mkdirs();
            
            return ImageIO.write(imagen, "PNG", archivo);
            
        } catch (IOException e) {
            System.err.println("Error al exportar imagen: " + e.getMessage());
            return false;
        }
    }
}

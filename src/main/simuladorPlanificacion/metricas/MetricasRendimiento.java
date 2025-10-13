package simuladorPlanificacion.metricas;

import main.modelo.PCB;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Recolecta métricas de rendimiento en tiempo real y provee snapshots seguros
 */
public class MetricasRendimiento {
    
    // Acumuladores básicos
    private long ciclosTotales;
    private long ciclosCPU;
    private long procesosTerminados;
    private long cambiosContexto;
    
    // Series temporales usando estructuras propias
    private ListaEnlazada<MuestraTemporal> muestras;
    private final Object lock = new Object();
    
    // Parámetros de muestreo
    private long ultimoMuestreo;
    private int ciclosPorSegundoSimulado;
    
    /**
     * Constructor de métricas
     */
    public MetricasRendimiento() {
        this.ciclosTotales = 0;
        this.ciclosCPU = 0;
        this.procesosTerminados = 0;
        this.cambiosContexto = 0;
        this.muestras = new ListaEnlazada<>();
        this.ultimoMuestreo = 0;
        this.ciclosPorSegundoSimulado = 1000; // Valor por defecto
    }
    
    /**
     * Registra uso de CPU
     * 
     * @param ciclos Número de ciclos de CPU utilizados
     */
    public synchronized void registrarUsoCPU(long ciclos) {
        this.ciclosCPU += ciclos;
        this.ciclosTotales += ciclos;
    }
    
    /**
     * Registra tiempo idle del CPU
     * 
     * @param ciclos Número de ciclos idle
     */
    public synchronized void registrarIdle(long ciclos) {
        this.ciclosTotales += ciclos;
    }
    
    /**
     * Registra un cambio de contexto
     */
    public synchronized void registrarCambioContexto() {
        this.cambiosContexto++;
    }
    
    /**
     * Registra la terminación de un proceso
     * 
     * @param pcb PCB del proceso terminado
     */
    public synchronized void registrarTerminado(PCB pcb) {
        this.procesosTerminados++;
    }
    
    /**
     * Toma una muestra de métricas en el ciclo actual
     * 
     * @param cicloActual Ciclo actual del simulador
     */
    public synchronized void muestrear(long cicloActual) {
        if (cicloActual - ultimoMuestreo >= 10) { // Muestrear cada 10 ciclos
            double utilizacionCPU = calcularUtilizacionCPU();
            double throughput = calcularThroughput();
            double tiempoRespuestaProm = calcularTiempoRespuestaPromedio();
            
            MuestraTemporal muestra = new MuestraTemporal(
                cicloActual, utilizacionCPU, throughput, tiempoRespuestaProm
            );
            
            muestras.agregar(muestra);
            ultimoMuestreo = cicloActual;
        }
    }
    
    /**
     * Calcula la utilización del CPU
     * 
     * @return Utilización del CPU (0.0 a 1.0)
     */
    private double calcularUtilizacionCPU() {
        if (ciclosTotales == 0) return 0.0;
        return (double) ciclosCPU / Math.max(1, ciclosTotales);
    }
    
    /**
     * Calcula el throughput
     * 
     * @return Throughput (procesos por segundo simulado)
     */
    private double calcularThroughput() {
        if (ciclosTotales == 0) return 0.0;
        double segundosSimulados = (double) ciclosTotales / ciclosPorSegundoSimulado;
        return (double) procesosTerminados / Math.max(1, segundosSimulados);
    }
    
    /**
     * Calcula el tiempo de respuesta promedio
     * Nota: Esta implementación es simplificada ya que no tenemos acceso directo a los PCBs
     * 
     * @return Tiempo de respuesta promedio
     */
    private double calcularTiempoRespuestaPromedio() {
        // Implementación simplificada - en un sistema real se calcularía
        // basándose en los timestamps de los PCBs
        return 0.0;
    }
    
    /**
     * Obtiene un snapshot inmutable de las métricas actuales
     * 
     * @return Snapshot de métricas
     */
    public synchronized Snapshot snapshot() {
        return new Snapshot(
            ciclosTotales,
            ciclosCPU,
            procesosTerminados,
            cambiosContexto,
            calcularUtilizacionCPU(),
            calcularThroughput(),
            calcularTiempoRespuestaPromedio(),
            muestras.toArray()
        );
    }
    
    /**
     * Establece los ciclos por segundo simulado
     * 
     * @param ciclosPorSegundo Ciclos por segundo
     */
    public synchronized void setCiclosPorSegundoSimulado(int ciclosPorSegundo) {
        this.ciclosPorSegundoSimulado = ciclosPorSegundo;
    }
    
    /**
     * Exporta las métricas a un archivo CSV
     * 
     * @param path Ruta del archivo
     * @return true si se exportó correctamente
     */
    public synchronized boolean exportarCSV(String path) {
        try {
            Path filePath = Paths.get(path);
            StringBuilder csv = new StringBuilder();
            
            // Encabezados
            csv.append("Ciclo,UtilizacionCPU,Throughput,TiempoRespuestaProm\n");
            
            // Datos
            MuestraTemporal[] muestrasArray = muestras.toArray();
            for (MuestraTemporal muestra : muestrasArray) {
                csv.append(String.format("%d,%.4f,%.4f,%.4f\n",
                    muestra.ciclo, muestra.utilizacionCPU, 
                    muestra.throughput, muestra.tiempoRespuestaProm));
            }
            
            Files.write(filePath, csv.toString().getBytes());
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al exportar CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exporta las métricas a un archivo JSON
     * 
     * @param path Ruta del archivo
     * @return true si se exportó correctamente
     */
    public synchronized boolean exportarJSON(String path) {
        try {
            Path filePath = Paths.get(path);
            StringBuilder json = new StringBuilder();
            
            json.append("{\n");
            json.append("  \"timestamp\": \"").append(Instant.now().toString()).append("\",\n");
            json.append("  \"metricas\": {\n");
            json.append("    \"ciclosTotales\": ").append(ciclosTotales).append(",\n");
            json.append("    \"ciclosCPU\": ").append(ciclosCPU).append(",\n");
            json.append("    \"procesosTerminados\": ").append(procesosTerminados).append(",\n");
            json.append("    \"cambiosContexto\": ").append(cambiosContexto).append(",\n");
            json.append("    \"utilizacionCPU\": ").append(calcularUtilizacionCPU()).append(",\n");
            json.append("    \"throughput\": ").append(calcularThroughput()).append(",\n");
            json.append("    \"tiempoRespuestaProm\": ").append(calcularTiempoRespuestaPromedio()).append("\n");
            json.append("  },\n");
            json.append("  \"muestras\": [\n");
            
            MuestraTemporal[] muestrasArray = muestras.toArray();
            for (int i = 0; i < muestrasArray.length; i++) {
                MuestraTemporal muestra = muestrasArray[i];
                json.append("    {\n");
                json.append("      \"ciclo\": ").append(muestra.ciclo).append(",\n");
                json.append("      \"utilizacionCPU\": ").append(muestra.utilizacionCPU).append(",\n");
                json.append("      \"throughput\": ").append(muestra.throughput).append(",\n");
                json.append("      \"tiempoRespuestaProm\": ").append(muestra.tiempoRespuestaProm).append("\n");
                json.append("    }");
                if (i < muestrasArray.length - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            
            json.append("  ]\n");
            json.append("}\n");
            
            Files.write(filePath, json.toString().getBytes());
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al exportar JSON: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Limpia todas las métricas
     */
    public synchronized void limpiar() {
        this.ciclosTotales = 0;
        this.ciclosCPU = 0;
        this.procesosTerminados = 0;
        this.cambiosContexto = 0;
        this.muestras = new ListaEnlazada<>();
        this.ultimoMuestreo = 0;
    }
    
    /**
     * Clase para almacenar muestras temporales
     */
    private static class MuestraTemporal {
        final long ciclo;
        final double utilizacionCPU;
        final double throughput;
        final double tiempoRespuestaProm;
        
        MuestraTemporal(long ciclo, double utilizacionCPU, double throughput, double tiempoRespuestaProm) {
            this.ciclo = ciclo;
            this.utilizacionCPU = utilizacionCPU;
            this.throughput = throughput;
            this.tiempoRespuestaProm = tiempoRespuestaProm;
        }
    }
    
    /**
     * Snapshot inmutable de métricas
     */
    public static class Snapshot {
        public final long ciclosTotales;
        public final long ciclosCPU;
        public final long procesosTerminados;
        public final long cambiosContexto;
        public final double utilizacionCPU;
        public final double throughput;
        public final double tiempoRespuestaProm;
        public final MuestraTemporal[] muestras;
        
        Snapshot(long ciclosTotales, long ciclosCPU, long procesosTerminados, long cambiosContexto,
                double utilizacionCPU, double throughput, double tiempoRespuestaProm, 
                MuestraTemporal[] muestras) {
            this.ciclosTotales = ciclosTotales;
            this.ciclosCPU = ciclosCPU;
            this.procesosTerminados = procesosTerminados;
            this.cambiosContexto = cambiosContexto;
            this.utilizacionCPU = utilizacionCPU;
            this.throughput = throughput;
            this.tiempoRespuestaProm = tiempoRespuestaProm;
            this.muestras = muestras.clone(); // Copia defensiva
        }
    }
}

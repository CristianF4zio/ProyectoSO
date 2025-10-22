package main.metricas;

import main.modelo.Proceso;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase que calcula y almacena métricas de rendimiento del sistema
 * Incluye throughput, utilización CPU, equidad, tiempos de espera y respuesta
 */
public class MetricasRendimiento {
    
    // Métricas principales
    private double throughput;
    private double utilizacionCPU;
    private double equidad;
    private double tiempoEsperaPromedio;
    private double tiempoRespuestaPromedio;
    
    // Contadores y datos para cálculos
    private int totalCiclos;
    private int ciclosCPUOcupada;
    private int procesosCompletados;
    private Map<Integer, Integer> cicloIngresoCola;
    private Map<Integer, Integer> cicloDespacho;
    private Map<Integer, Integer> cicloFinalizacion;
    private Map<Integer, Integer> tiempoEsperaPorProceso;
    private Map<Integer, Integer> tiempoRespuestaPorProceso;
    
    // Historial de métricas
    private List<SnapshotMetricas> historial;
    
    /**
     * Constructor de métricas de rendimiento
     */
    public MetricasRendimiento() {
        this.throughput = 0.0;
        this.utilizacionCPU = 0.0;
        this.equidad = 0.0;
        this.tiempoEsperaPromedio = 0.0;
        this.tiempoRespuestaPromedio = 0.0;
        
        this.totalCiclos = 0;
        this.ciclosCPUOcupada = 0;
        this.procesosCompletados = 0;
        
        this.cicloIngresoCola = new ConcurrentHashMap<>();
        this.cicloDespacho = new ConcurrentHashMap<>();
        this.cicloFinalizacion = new ConcurrentHashMap<>();
        this.tiempoEsperaPorProceso = new ConcurrentHashMap<>();
        this.tiempoRespuestaPorProceso = new ConcurrentHashMap<>();
        
        this.historial = new ArrayList<>();
    }
    
    /**
     * Registra un tick del sistema
     * 
     * @param ciclo Número de ciclo actual
     * @param cpuOcupada Si la CPU está ocupada
     */
    public void registrarTick(int ciclo, boolean cpuOcupada) {
        totalCiclos = ciclo;
        if (cpuOcupada) {
            ciclosCPUOcupada++;
        }
        calcularMetricas();
    }
    
    /**
     * Registra el ingreso de un proceso a la cola de listos
     * 
     * @param p Proceso que ingresa
     * @param ciclo Ciclo actual
     */
    public void registrarIngresoCola(Proceso p, int ciclo) {
        if (!cicloIngresoCola.containsKey(p.getId())) {
            cicloIngresoCola.put(p.getId(), ciclo);
        }
    }
    
    /**
     * Registra la salida de un proceso de la cola de listos
     * 
     * @param p Proceso que sale
     * @param ciclo Ciclo actual
     */
    public void registrarSalidaCola(Proceso p, int ciclo) {
        // Este método puede usarse para registrar eventos adicionales
        // Por ahora no se necesita implementación específica
    }
    
    /**
     * Registra el despacho de un proceso a ejecución
     * 
     * @param p Proceso despachado
     * @param ciclo Ciclo actual
     */
    public void registrarDespacho(Proceso p, int ciclo) {
        if (!cicloDespacho.containsKey(p.getId())) {
            cicloDespacho.put(p.getId(), ciclo);
            
            // Calcular tiempo de respuesta (primer despacho - ingreso a cola)
            if (cicloIngresoCola.containsKey(p.getId())) {
                int tiempoRespuesta = ciclo - cicloIngresoCola.get(p.getId());
                tiempoRespuestaPorProceso.put(p.getId(), tiempoRespuesta);
            }
        }
    }
    
    /**
     * Registra la finalización de un proceso
     * 
     * @param p Proceso finalizado
     * @param ciclo Ciclo actual
     */
    public void registrarFinalizacion(Proceso p, int ciclo) {
        cicloFinalizacion.put(p.getId(), ciclo);
        procesosCompletados++;
        
        // Calcular tiempo de espera (finalización - ingreso a cola - tiempo de ejecución)
        if (cicloIngresoCola.containsKey(p.getId())) {
            int tiempoTotal = ciclo - cicloIngresoCola.get(p.getId());
            int tiempoEjecucion = p.getNumInstrucciones();
            int tiempoEspera = Math.max(0, tiempoTotal - tiempoEjecucion);
            tiempoEsperaPorProceso.put(p.getId(), tiempoEspera);
        }
        
        calcularMetricas();
    }
    
    /**
     * Calcula todas las métricas de rendimiento
     */
    private void calcularMetricas() {
        // Throughput: procesos completados por unidad de tiempo
        if (totalCiclos > 0) {
            throughput = (double) procesosCompletados / totalCiclos;
        }
        
        // Utilización CPU: porcentaje de ciclos con CPU ocupada
        if (totalCiclos > 0) {
            utilizacionCPU = (double) ciclosCPUOcupada / totalCiclos * 100.0;
        }
        
        // Tiempo de espera promedio
        if (!tiempoEsperaPorProceso.isEmpty()) {
            double sumaEspera = tiempoEsperaPorProceso.values().stream()
                    .mapToDouble(Integer::doubleValue)
                    .sum();
            tiempoEsperaPromedio = sumaEspera / tiempoEsperaPorProceso.size();
        }
        
        // Tiempo de respuesta promedio
        if (!tiempoRespuestaPorProceso.isEmpty()) {
            double sumaRespuesta = tiempoRespuestaPorProceso.values().stream()
                    .mapToDouble(Integer::doubleValue)
                    .sum();
            tiempoRespuestaPromedio = sumaRespuesta / tiempoRespuestaPorProceso.size();
        }
        
        // Equidad: desviación estándar de los tiempos de espera (invertida y normalizada)
        if (tiempoEsperaPorProceso.size() > 1) {
            double[] tiempos = tiempoEsperaPorProceso.values().stream()
                    .mapToDouble(Integer::doubleValue)
                    .toArray();
            double desviacion = calcularDesviacionEstandar(tiempos);
            // Equidad: 100 - desviación normalizada
            equidad = Math.max(0, 100.0 - desviacion);
        } else {
            equidad = 100.0; // Perfecta equidad si solo hay 1 proceso
        }
    }
    
    /**
     * Calcula la desviación estándar de un array de valores
     * 
     * @param valores Array de valores
     * @return Desviación estándar
     */
    private double calcularDesviacionEstandar(double[] valores) {
        if (valores.length == 0) return 0.0;
        
        double media = Arrays.stream(valores).average().orElse(0.0);
        double sumaCuadrados = Arrays.stream(valores)
                .map(v -> Math.pow(v - media, 2))
                .sum();
        return Math.sqrt(sumaCuadrados / valores.length);
    }
    
    /**
     * Crea un snapshot de las métricas actuales
     * 
     * @return Map con las métricas actuales
     */
    public Map<String, Number> snapshot() {
        Map<String, Number> snapshot = new HashMap<>();
        snapshot.put("throughput", throughput);
        snapshot.put("utilizacionCPU", utilizacionCPU);
        snapshot.put("equidad", equidad);
        snapshot.put("tiempoEsperaPromedio", tiempoEsperaPromedio);
        snapshot.put("tiempoRespuestaPromedio", tiempoRespuestaPromedio);
        snapshot.put("totalCiclos", totalCiclos);
        snapshot.put("procesosCompletados", procesosCompletados);
        
        // Guardar en historial
        historial.add(new SnapshotMetricas(totalCiclos, snapshot));
        
        return snapshot;
    }
    
    /**
     * Obtiene el historial de métricas
     * 
     * @return Lista de snapshots históricos
     */
    public List<SnapshotMetricas> getHistorial() {
        return new ArrayList<>(historial);
    }
    
    /**
     * Reinicia todas las métricas
     */
    public void reiniciar() {
        this.throughput = 0.0;
        this.utilizacionCPU = 0.0;
        this.equidad = 0.0;
        this.tiempoEsperaPromedio = 0.0;
        this.tiempoRespuestaPromedio = 0.0;
        
        this.totalCiclos = 0;
        this.ciclosCPUOcupada = 0;
        this.procesosCompletados = 0;
        
        this.cicloIngresoCola.clear();
        this.cicloDespacho.clear();
        this.cicloFinalizacion.clear();
        this.tiempoEsperaPorProceso.clear();
        this.tiempoRespuestaPorProceso.clear();
        
        this.historial.clear();
    }
    
    // Getters
    
    public double getThroughput() {
        return throughput;
    }
    
    public double getUtilizacionCPU() {
        return utilizacionCPU;
    }
    
    public double getEquidad() {
        return equidad;
    }
    
    public double getTiempoEsperaPromedio() {
        return tiempoEsperaPromedio;
    }
    
    public double getTiempoRespuestaPromedio() {
        return tiempoRespuestaPromedio;
    }
    
    public int getTotalCiclos() {
        return totalCiclos;
    }
    
    public int getProcesosCompletados() {
        return procesosCompletados;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Métricas[Throughput=%.4f, CPU=%.2f%%, Equidad=%.2f, TiempoEspera=%.2f, TiempoRespuesta=%.2f]",
            throughput, utilizacionCPU, equidad, tiempoEsperaPromedio, tiempoRespuestaPromedio
        );
    }
    
    /**
     * Clase interna para almacenar snapshots de métricas
     */
    public static class SnapshotMetricas {
        private int ciclo;
        private Map<String, Number> metricas;
        
        public SnapshotMetricas(int ciclo, Map<String, Number> metricas) {
            this.ciclo = ciclo;
            this.metricas = new HashMap<>(metricas);
        }
        
        public int getCiclo() {
            return ciclo;
        }
        
        public Map<String, Number> getMetricas() {
            return new HashMap<>(metricas);
        }
    }
}


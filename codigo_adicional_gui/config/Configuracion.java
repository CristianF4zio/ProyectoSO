package main.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Clase que representa la configuración del simulador
 * Contiene todos los parámetros configurables del sistema
 */
public class Configuracion {
    
    private int duracionCicloMs;
    private int quantumMs;
    private String politicaPlanificacion;
    private boolean loggingHabilitado;
    private int prioridadMaxima;
    private String formatoPersistencia;
    private int maxProcesos;
    private int ciclosExcepcionIO;
    
    /**
     * Constructor privado para forzar uso del método porDefecto()
     */
    private Configuracion() {
        // Constructor privado
    }
    
    /**
     * Crea una configuración con valores por defecto
     * 
     * @return Configuración por defecto
     */
    public static Configuracion porDefecto() {
        Configuracion config = new Configuracion();
        config.duracionCicloMs = 1000;  // 1 segundo por ciclo
        config.quantumMs = 3000;        // 3 segundos de quantum
        config.politicaPlanificacion = "FCFS";
        config.loggingHabilitado = true;
        config.prioridadMaxima = 10;
        config.formatoPersistencia = "json";
        config.maxProcesos = 100;
        config.ciclosExcepcionIO = 3;
        return config;
    }
    
    /**
     * Convierte la configuración a JSON
     * 
     * @return String JSON con la configuración
     */
    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
    
    /**
     * Crea una configuración desde un JSON
     * 
     * @param json String JSON con la configuración
     * @return Configuración parseada
     */
    public static Configuracion fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, Configuracion.class);
        } catch (Exception e) {
            System.err.println("Error al parsear configuración desde JSON: " + e.getMessage());
            return porDefecto();
        }
    }
    
    /**
     * Convierte la configuración a CSV
     * 
     * @return String CSV con la configuración
     */
    public String toCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("parametro,valor\n");
        csv.append("duracionCicloMs,").append(duracionCicloMs).append("\n");
        csv.append("quantumMs,").append(quantumMs).append("\n");
        csv.append("politicaPlanificacion,").append(politicaPlanificacion).append("\n");
        csv.append("loggingHabilitado,").append(loggingHabilitado).append("\n");
        csv.append("prioridadMaxima,").append(prioridadMaxima).append("\n");
        csv.append("formatoPersistencia,").append(formatoPersistencia).append("\n");
        csv.append("maxProcesos,").append(maxProcesos).append("\n");
        csv.append("ciclosExcepcionIO,").append(ciclosExcepcionIO).append("\n");
        return csv.toString();
    }
    
    /**
     * Crea una configuración desde un CSV
     * 
     * @param csv String CSV con la configuración
     * @return Configuración parseada
     */
    public static Configuracion fromCsv(String csv) {
        try {
            Configuracion config = new Configuracion();
            String[] lineas = csv.split("\n");
            
            for (int i = 1; i < lineas.length; i++) {
                String[] partes = lineas[i].split(",");
                if (partes.length < 2) continue;
                
                String parametro = partes[0].trim();
                String valor = partes[1].trim();
                
                switch (parametro) {
                    case "duracionCicloMs":
                        config.duracionCicloMs = Integer.parseInt(valor);
                        break;
                    case "quantumMs":
                        config.quantumMs = Integer.parseInt(valor);
                        break;
                    case "politicaPlanificacion":
                        config.politicaPlanificacion = valor;
                        break;
                    case "loggingHabilitado":
                        config.loggingHabilitado = Boolean.parseBoolean(valor);
                        break;
                    case "prioridadMaxima":
                        config.prioridadMaxima = Integer.parseInt(valor);
                        break;
                    case "formatoPersistencia":
                        config.formatoPersistencia = valor;
                        break;
                    case "maxProcesos":
                        config.maxProcesos = Integer.parseInt(valor);
                        break;
                    case "ciclosExcepcionIO":
                        config.ciclosExcepcionIO = Integer.parseInt(valor);
                        break;
                }
            }
            return config;
        } catch (Exception e) {
            System.err.println("Error al parsear configuración desde CSV: " + e.getMessage());
            return porDefecto();
        }
    }
    
    // Getters y Setters
    
    public int getDuracionCicloMs() {
        return duracionCicloMs;
    }
    
    public void setDuracionCicloMs(int duracionCicloMs) {
        if (duracionCicloMs > 0) {
            this.duracionCicloMs = duracionCicloMs;
        }
    }
    
    public int getQuantumMs() {
        return quantumMs;
    }
    
    public void setQuantumMs(int quantumMs) {
        if (quantumMs > 0) {
            this.quantumMs = quantumMs;
        }
    }
    
    public String getPoliticaPlanificacion() {
        return politicaPlanificacion;
    }
    
    public void setPoliticaPlanificacion(String politicaPlanificacion) {
        this.politicaPlanificacion = politicaPlanificacion;
    }
    
    public boolean isLoggingHabilitado() {
        return loggingHabilitado;
    }
    
    public void setLoggingHabilitado(boolean loggingHabilitado) {
        this.loggingHabilitado = loggingHabilitado;
    }
    
    public int getPrioridadMaxima() {
        return prioridadMaxima;
    }
    
    public void setPrioridadMaxima(int prioridadMaxima) {
        if (prioridadMaxima > 0) {
            this.prioridadMaxima = prioridadMaxima;
        }
    }
    
    public String getFormatoPersistencia() {
        return formatoPersistencia;
    }
    
    public void setFormatoPersistencia(String formatoPersistencia) {
        this.formatoPersistencia = formatoPersistencia;
    }
    
    public int getMaxProcesos() {
        return maxProcesos;
    }
    
    public void setMaxProcesos(int maxProcesos) {
        if (maxProcesos > 0) {
            this.maxProcesos = maxProcesos;
        }
    }
    
    public int getCiclosExcepcionIO() {
        return ciclosExcepcionIO;
    }
    
    public void setCiclosExcepcionIO(int ciclosExcepcionIO) {
        if (ciclosExcepcionIO > 0) {
            this.ciclosExcepcionIO = ciclosExcepcionIO;
        }
    }
    
    @Override
    public String toString() {
        return String.format("Configuracion[CicloMs=%d, QuantumMs=%d, Politica=%s, Logging=%b, MaxPrioridad=%d]",
                duracionCicloMs, quantumMs, politicaPlanificacion, loggingHabilitado, prioridadMaxima);
    }
}


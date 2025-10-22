package main.config;

/**
 * Clase simplificada de configuración sin dependencias externas
 */
public class ConfiguracionSimple {
    
    private int duracionCicloMs;
    private int quantumMs;
    private String politicaPlanificacion;
    private boolean loggingHabilitado;
    private int prioridadMaxima;
    private int maxProcesos;
    private int ciclosExcepcionIO;
    
    private ConfiguracionSimple() {}
    
    public static ConfiguracionSimple porDefecto() {
        ConfiguracionSimple config = new ConfiguracionSimple();
        config.duracionCicloMs = 1000;
        config.quantumMs = 3000;
        config.politicaPlanificacion = "FCFS";
        config.loggingHabilitado = true;
        config.prioridadMaxima = 10;
        config.maxProcesos = 100;
        config.ciclosExcepcionIO = 3;
        return config;
    }
    
    // Getters y Setters
    public int getDuracionCicloMs() { return duracionCicloMs; }
    public void setDuracionCicloMs(int duracionCicloMs) { this.duracionCicloMs = duracionCicloMs; }
    
    public int getQuantumMs() { return quantumMs; }
    public void setQuantumMs(int quantumMs) { this.quantumMs = quantumMs; }
    
    public String getPoliticaPlanificacion() { return politicaPlanificacion; }
    public void setPoliticaPlanificacion(String politicaPlanificacion) { this.politicaPlanificacion = politicaPlanificacion; }
    
    public boolean isLoggingHabilitado() { return loggingHabilitado; }
    public void setLoggingHabilitado(boolean loggingHabilitado) { this.loggingHabilitado = loggingHabilitado; }
    
    public int getPrioridadMaxima() { return prioridadMaxima; }
    public void setPrioridadMaxima(int prioridadMaxima) { this.prioridadMaxima = prioridadMaxima; }
    
    public int getMaxProcesos() { return maxProcesos; }
    public void setMaxProcesos(int maxProcesos) { this.maxProcesos = maxProcesos; }
    
    public int getCiclosExcepcionIO() { return ciclosExcepcionIO; }
    public void setCiclosExcepcionIO(int ciclosExcepcionIO) { this.ciclosExcepcionIO = ciclosExcepcionIO; }
}


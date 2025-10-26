package main.config;

public class ConfiguracionSistema {
    
    private int duracionCicloMs;
    private int maxProcesos;
    private String algoritmoInicial;
    private int quantumRR;
    private int numeroNivelesMultinivel;
    private boolean logActivo;
    
    public ConfiguracionSistema() {
        this.duracionCicloMs = 1000;
        this.maxProcesos = 50;
        this.algoritmoInicial = "FCFS";
        this.quantumRR = 4;
        this.numeroNivelesMultinivel = 3;
        this.logActivo = true;
    }
    
    public int getDuracionCicloMs() {
        return duracionCicloMs;
    }
    
    public void setDuracionCicloMs(int duracionCicloMs) {
        this.duracionCicloMs = duracionCicloMs;
    }
    
    public int getMaxProcesos() {
        return maxProcesos;
    }
    
    public void setMaxProcesos(int maxProcesos) {
        this.maxProcesos = maxProcesos;
    }
    
    public String getAlgoritmoInicial() {
        return algoritmoInicial;
    }
    
    public void setAlgoritmoInicial(String algoritmoInicial) {
        this.algoritmoInicial = algoritmoInicial;
    }
    
    public int getQuantumRR() {
        return quantumRR;
    }
    
    public void setQuantumRR(int quantumRR) {
        this.quantumRR = quantumRR;
    }
    
    public int getNumeroNivelesMultinivel() {
        return numeroNivelesMultinivel;
    }
    
    public void setNumeroNivelesMultinivel(int numeroNivelesMultinivel) {
        this.numeroNivelesMultinivel = numeroNivelesMultinivel;
    }
    
    public boolean isLogActivo() {
        return logActivo;
    }
    
    public void setLogActivo(boolean logActivo) {
        this.logActivo = logActivo;
    }
    
    public String toJSON() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"duracionCicloMs\": ").append(duracionCicloMs).append(",\n");
        json.append("  \"maxProcesos\": ").append(maxProcesos).append(",\n");
        json.append("  \"algoritmoInicial\": \"").append(algoritmoInicial).append("\",\n");
        json.append("  \"quantumRR\": ").append(quantumRR).append(",\n");
        json.append("  \"numeroNivelesMultinivel\": ").append(numeroNivelesMultinivel).append(",\n");
        json.append("  \"logActivo\": ").append(logActivo).append("\n");
        json.append("}");
        return json.toString();
    }
    
    public static ConfiguracionSistema fromJSON(String json) {
        ConfiguracionSistema config = new ConfiguracionSistema();
        
        try {
            String[] lines = json.split("\n");
            for (String line : lines) {
                line = line.trim();
                
                if (line.contains("\"duracionCicloMs\"")) {
                    config.duracionCicloMs = extraerEntero(line);
                } else if (line.contains("\"maxProcesos\"")) {
                    config.maxProcesos = extraerEntero(line);
                } else if (line.contains("\"algoritmoInicial\"")) {
                    config.algoritmoInicial = extraerCadena(line);
                } else if (line.contains("\"quantumRR\"")) {
                    config.quantumRR = extraerEntero(line);
                } else if (line.contains("\"numeroNivelesMultinivel\"")) {
                    config.numeroNivelesMultinivel = extraerEntero(line);
                } else if (line.contains("\"logActivo\"")) {
                    config.logActivo = extraerBooleano(line);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear JSON: " + e.getMessage());
        }
        
        return config;
    }
    
    private static int extraerEntero(String line) {
        String[] parts = line.split(":");
        if (parts.length > 1) {
            String valor = parts[1].trim().replace(",", "").replace("}", "");
            return Integer.parseInt(valor);
        }
        return 0;
    }
    
    private static String extraerCadena(String line) {
        int inicio = line.indexOf("\"", line.indexOf(":")) + 1;
        int fin = line.lastIndexOf("\"");
        if (inicio > 0 && fin > inicio) {
            return line.substring(inicio, fin);
        }
        return "";
    }
    
    private static boolean extraerBooleano(String line) {
        return line.toLowerCase().contains("true");
    }
}



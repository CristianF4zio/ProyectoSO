package main.gestor;

import main.modelo.Proceso;

/**
 * Clase que representa una excepción de E/S en el sistema
 * Contiene información sobre el proceso que solicitó la operación de E/S
 * y el tiempo de bloqueo necesario
 */
public class ExcepcionIO {
    
    private Proceso proceso;
    private int ciclosBloqueados;
    private int ciclosRestantes;
    private long tiempoInicio;
    private boolean completada;
    
    /**
     * Constructor de la excepción de E/S
     * 
     * @param proceso Proceso que solicita la operación de E/S
     * @param ciclosBloqueados Número de ciclos que durará el bloqueo
     */
    public ExcepcionIO(Proceso proceso, int ciclosBloqueados) {
        this.proceso = proceso;
        this.ciclosBloqueados = ciclosBloqueados;
        this.ciclosRestantes = ciclosBloqueados;
        this.tiempoInicio = System.currentTimeMillis();
        this.completada = false;
    }
    
    /**
     * Procesa un ciclo de E/S
     * 
     * @return true si la E/S ha completado
     */
    public boolean procesarCiclo() {
        if (ciclosRestantes > 0) {
            ciclosRestantes--;
        }
        if (ciclosRestantes == 0) {
            completada = true;
        }
        return completada;
    }
    
    /**
     * Obtiene el proceso asociado
     * 
     * @return Proceso que solicitó la E/S
     */
    public Proceso getProceso() {
        return proceso;
    }
    
    /**
     * Obtiene los ciclos bloqueados totales
     * 
     * @return Número de ciclos de bloqueo
     */
    public int getCiclosBloqueados() {
        return ciclosBloqueados;
    }
    
    /**
     * Obtiene los ciclos restantes
     * 
     * @return Ciclos que faltan para completar
     */
    public int getCiclosRestantes() {
        return ciclosRestantes;
    }
    
    /**
     * Obtiene el tiempo de inicio
     * 
     * @return Timestamp de inicio
     */
    public long getTiempoInicio() {
        return tiempoInicio;
    }
    
    /**
     * Verifica si la operación de E/S está completada
     * 
     * @return true si está completada
     */
    public boolean isCompletada() {
        return completada;
    }
    
    /**
     * Obtiene el progreso de la operación de E/S
     * 
     * @return Porcentaje de progreso (0-100)
     */
    public double getProgreso() {
        if (ciclosBloqueados == 0) return 100.0;
        return ((ciclosBloqueados - ciclosRestantes) * 100.0) / ciclosBloqueados;
    }
    
    @Override
    public String toString() {
        return String.format("ExcepcionIO[Proceso=%s, Ciclos=%d/%d, Completada=%b]",
                proceso.getNombre(), 
                ciclosBloqueados - ciclosRestantes, 
                ciclosBloqueados, 
                completada);
    }
}


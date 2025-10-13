package main.gestor;

/**
 * Clase que almacena la configuración del simulador
 */
public class Configuracion {

    private long duracionCiclo; // en milisegundos
    private int numInstruccionesPorProceso;
    private int ciclosParaExcepcionIO;
    private int ciclosParaCompletarIO;
    private int quantumRoundRobin;

    /**
     * Constructor con valores por defecto
     */
    public Configuracion() {
        // TODO: Establecer valores por defecto
    }

    // Getters y Setters
    public long getDuracionCiclo() {
        return duracionCiclo;
    }

    public void setDuracionCiclo(long duracionCiclo) {
        this.duracionCiclo = duracionCiclo;
    }

    public int getNumInstruccionesPorProceso() {
        return numInstruccionesPorProceso;
    }

    public void setNumInstruccionesPorProceso(int numInstruccionesPorProceso) {
        this.numInstruccionesPorProceso = numInstruccionesPorProceso;
    }

    public int getCiclosParaExcepcionIO() {
        return ciclosParaExcepcionIO;
    }

    public void setCiclosParaExcepcionIO(int ciclosParaExcepcionIO) {
        this.ciclosParaExcepcionIO = ciclosParaExcepcionIO;
    }

    public int getCiclosParaCompletarIO() {
        return ciclosParaCompletarIO;
    }

    public void setCiclosParaCompletarIO(int ciclosParaCompletarIO) {
        this.ciclosParaCompletarIO = ciclosParaCompletarIO;
    }

    public int getQuantumRoundRobin() {
        return quantumRoundRobin;
    }

    public void setQuantumRoundRobin(int quantumRoundRobin) {
        this.quantumRoundRobin = quantumRoundRobin;
    }
}
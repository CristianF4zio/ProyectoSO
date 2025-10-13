package main.modelo;

import java.time.LocalDateTime;

/**
 * Clase que representa un proceso en el sistema
 * Contiene toda la información del PCB (Process Control Block)
 */
public class Proceso {
    
    // Información básica del proceso
    private int id;
    private String nombre;
    private EstadoProceso estado;
    private TipoProceso tipo;
    
    // Información del PCB
    private int programCounter;
    private int memoryAddressRegister;
    private int numInstrucciones;
    private int instruccionesEjecutadas;
    private int prioridad;
    private int quantumRestante;
    
    // Tiempos de control
    private LocalDateTime tiempoCreacion;
    private LocalDateTime tiempoInicio;
    private LocalDateTime tiempoFinalizacion;
    private long tiempoEspera;
    private long tiempoRespuesta;
    private long tiempoEjecucion;
    
    // Información de I/O
    private int ciclosParaExcepcionIO;
    private int ciclosTranscurridosIO;
    private boolean enOperacionIO;
    
    // Constructor
    public Proceso(int id, String nombre, TipoProceso tipo, int numInstrucciones, int prioridad) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.numInstrucciones = numInstrucciones;
        this.prioridad = prioridad;
        this.estado = EstadoProceso.NUEVO;
        this.programCounter = 0;
        this.memoryAddressRegister = 0;
        this.instruccionesEjecutadas = 0;
        this.quantumRestante = 0;
        this.tiempoCreacion = LocalDateTime.now();
        this.tiempoEspera = 0;
        this.tiempoRespuesta = 0;
        this.tiempoEjecucion = 0;
        this.ciclosParaExcepcionIO = 0;
        this.ciclosTranscurridosIO = 0;
        this.enOperacionIO = false;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public EstadoProceso getEstado() { return estado; }
    public void setEstado(EstadoProceso estado) { this.estado = estado; }
    
    public TipoProceso getTipo() { return tipo; }
    public void setTipo(TipoProceso tipo) { this.tipo = tipo; }
    
    public int getProgramCounter() { return programCounter; }
    public void setProgramCounter(int programCounter) { this.programCounter = programCounter; }
    
    public int getMemoryAddressRegister() { return memoryAddressRegister; }
    public void setMemoryAddressRegister(int memoryAddressRegister) { this.memoryAddressRegister = memoryAddressRegister; }
    
    public int getNumInstrucciones() { return numInstrucciones; }
    public void setNumInstrucciones(int numInstrucciones) { this.numInstrucciones = numInstrucciones; }
    
    public int getInstruccionesEjecutadas() { return instruccionesEjecutadas; }
    public void setInstruccionesEjecutadas(int instruccionesEjecutadas) { this.instruccionesEjecutadas = instruccionesEjecutadas; }
    
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
    
    public int getQuantumRestante() { return quantumRestante; }
    public void setQuantumRestante(int quantumRestante) { this.quantumRestante = quantumRestante; }
    
    public LocalDateTime getTiempoCreacion() { return tiempoCreacion; }
    public void setTiempoCreacion(LocalDateTime tiempoCreacion) { this.tiempoCreacion = tiempoCreacion; }
    
    public LocalDateTime getTiempoInicio() { return tiempoInicio; }
    public void setTiempoInicio(LocalDateTime tiempoInicio) { this.tiempoInicio = tiempoInicio; }
    
    public LocalDateTime getTiempoFinalizacion() { return tiempoFinalizacion; }
    public void setTiempoFinalizacion(LocalDateTime tiempoFinalizacion) { this.tiempoFinalizacion = tiempoFinalizacion; }
    
    public long getTiempoEspera() { return tiempoEspera; }
    public void setTiempoEspera(long tiempoEspera) { this.tiempoEspera = tiempoEspera; }
    
    public long getTiempoRespuesta() { return tiempoRespuesta; }
    public void setTiempoRespuesta(long tiempoRespuesta) { this.tiempoRespuesta = tiempoRespuesta; }
    
    public long getTiempoEjecucion() { return tiempoEjecucion; }
    public void setTiempoEjecucion(long tiempoEjecucion) { this.tiempoEjecucion = tiempoEjecucion; }
    
    public int getCiclosParaExcepcionIO() { return ciclosParaExcepcionIO; }
    public void setCiclosParaExcepcionIO(int ciclosParaExcepcionIO) { this.ciclosParaExcepcionIO = ciclosParaExcepcionIO; }
    
    public int getCiclosTranscurridosIO() { return ciclosTranscurridosIO; }
    public void setCiclosTranscurridosIO(int ciclosTranscurridosIO) { this.ciclosTranscurridosIO = ciclosTranscurridosIO; }
    
    public boolean isEnOperacionIO() { return enOperacionIO; }
    public void setEnOperacionIO(boolean enOperacionIO) { this.enOperacionIO = enOperacionIO; }
    
    // Métodos de utilidad
    public boolean isCompletado() {
        return instruccionesEjecutadas >= numInstrucciones;
    }
    
    public int getInstruccionesRestantes() {
        return numInstrucciones - instruccionesEjecutadas;
    }
    
    public double getProgreso() {
        if (numInstrucciones == 0) return 0.0;
        return (double) instruccionesEjecutadas / numInstrucciones * 100.0;
    }
    
    public void ejecutarInstruccion() {
        if (!isCompletado()) {
            instruccionesEjecutadas++;
            programCounter++;
            ciclosTranscurridosIO++;
        }
    }
    
    public void iniciarEjecucion() {
        if (tiempoInicio == null) {
            tiempoInicio = LocalDateTime.now();
        }
        estado = EstadoProceso.EJECUCION;
    }
    
    public void finalizarEjecucion() {
        tiempoFinalizacion = LocalDateTime.now();
        estado = EstadoProceso.TERMINADO;
    }
    
    @Override
    public String toString() {
        return String.format("Proceso[ID=%d, Nombre=%s, Estado=%s, Tipo=%s, Instrucciones=%d/%d, Prioridad=%d]",
                id, nombre, estado, tipo, instruccionesEjecutadas, numInstrucciones, prioridad);
    }
}
package main.interrupciones;

import main.modelo.Proceso;

public class Interrupcion {
    
    private TipoInterrupcion tipo;
    private Proceso procesoOrigen;
    private int cicloOcurrencia;
    private String descripcion;
    
    public Interrupcion(TipoInterrupcion tipo, Proceso procesoOrigen, int cicloOcurrencia, String descripcion) {
        this.tipo = tipo;
        this.procesoOrigen = procesoOrigen;
        this.cicloOcurrencia = cicloOcurrencia;
        this.descripcion = descripcion;
    }
    
    public TipoInterrupcion getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoInterrupcion tipo) {
        this.tipo = tipo;
    }
    
    public Proceso getProcesoOrigen() {
        return procesoOrigen;
    }
    
    public void setProcesoOrigen(Proceso procesoOrigen) {
        this.procesoOrigen = procesoOrigen;
    }
    
    public int getCicloOcurrencia() {
        return cicloOcurrencia;
    }
    
    public void setCicloOcurrencia(int cicloOcurrencia) {
        this.cicloOcurrencia = cicloOcurrencia;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    @Override
    public String toString() {
        return String.format("[Ciclo %d] INT_%s: %s (Proceso: %s)", 
            cicloOcurrencia, 
            tipo, 
            descripcion, 
            procesoOrigen != null ? procesoOrigen.getNombre() : "Sistema");
    }
}



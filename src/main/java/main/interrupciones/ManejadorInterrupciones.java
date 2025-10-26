package main.interrupciones;

import main.estructuras.ColaSimple;
import main.modelo.Proceso;
import main.modelo.EstadoProceso;

public class ManejadorInterrupciones {
    
    private ColaSimple<Interrupcion> colaInterrupciones;
    private int contadorInterrupciones;
    
    public ManejadorInterrupciones() {
        this.colaInterrupciones = new ColaSimple<>();
        this.contadorInterrupciones = 0;
    }
    
    public void generarInterrupcion(TipoInterrupcion tipo, Proceso proceso, int ciclo, String descripcion) {
        Interrupcion interrupcion = new Interrupcion(tipo, proceso, ciclo, descripcion);
        colaInterrupciones.encolar(interrupcion);
        contadorInterrupciones++;
        System.out.println("→ INTERRUPCIÓN GENERADA: " + interrupcion);
    }
    
    public Interrupcion procesarSiguienteInterrupcion() {
        if (colaInterrupciones.estaVacia()) {
            return null;
        }
        
        Interrupcion interrupcion = colaInterrupciones.desencolar();
        System.out.println("→ PROCESANDO: " + interrupcion);
        return interrupcion;
    }
    
    public void generarInterrupcionIO(Proceso proceso, int ciclo) {
        generarInterrupcion(TipoInterrupcion.IO_SOLICITUD, proceso, ciclo, 
            "Solicitud de operación I/O");
    }
    
    public void generarInterrupcionIOCompletada(Proceso proceso, int ciclo) {
        generarInterrupcion(TipoInterrupcion.IO_COMPLETADA, proceso, ciclo, 
            "Operación I/O completada");
    }
    
    public void generarInterrupcionQuantum(Proceso proceso, int ciclo) {
        generarInterrupcion(TipoInterrupcion.TIMER_QUANTUM, proceso, ciclo, 
            "Quantum agotado");
    }
    
    public void generarExcepcionMemoria(Proceso proceso, int ciclo) {
        generarInterrupcion(TipoInterrupcion.ERROR_MEMORIA, proceso, ciclo, 
            "Error de acceso a memoria");
    }
    
    public void generarExcepcionDivisionCero(Proceso proceso, int ciclo) {
        generarInterrupcion(TipoInterrupcion.ERROR_DIVISION_CERO, proceso, ciclo, 
            "División por cero detectada");
    }
    
    public void generarExcepcionInstruccionInvalida(Proceso proceso, int ciclo) {
        generarInterrupcion(TipoInterrupcion.ERROR_INSTRUCCION_INVALIDA, proceso, ciclo, 
            "Instrucción inválida");
    }
    
    public void generarInterrupcionFinalizacion(Proceso proceso, int ciclo) {
        generarInterrupcion(TipoInterrupcion.FINALIZACION_PROCESO, proceso, ciclo, 
            "Proceso ha finalizado");
    }
    
    public void generarCambioContexto(Proceso proceso, int ciclo) {
        generarInterrupcion(TipoInterrupcion.CAMBIO_CONTEXTO, proceso, ciclo, 
            "Cambio de contexto");
    }
    
    public boolean hayInterrupciones() {
        return !colaInterrupciones.estaVacia();
    }
    
    public int getNumeroInterrupciones() {
        return colaInterrupciones.tamaño();
    }
    
    public int getContadorTotal() {
        return contadorInterrupciones;
    }
    
    public void limpiar() {
        while (!colaInterrupciones.estaVacia()) {
            colaInterrupciones.desencolar();
        }
        contadorInterrupciones = 0;
    }
}



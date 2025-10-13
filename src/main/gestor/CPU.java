package main.gestor;

import main.modelo.Proceso;

/**
 * Clase que simula la CPU del sistema
 */
public class CPU {

    private Proceso procesoActual;
    private int programCounter;
    private int memoryAddressRegister;
    private boolean ejecutandoSO;

    /**
     * Ejecuta una instrucción del proceso actual
     */
    public void ejecutarInstruccion() {
        // TODO: Implementar ejecución de instrucción
    }

    /**
     * Asigna un proceso a la CPU
     * 
     * @param proceso Proceso a ejecutar
     */
    public void asignarProceso(Proceso proceso) {
        // TODO: Implementar asignación de proceso
    }

    /**
     * Libera la CPU
     */
    public void liberarCPU() {
        // TODO: Implementar liberación de CPU
    }

    /**
     * Verifica si la CPU está ejecutando el SO
     * 
     * @return true si ejecuta SO, false si ejecuta proceso de usuario
     */
    public boolean isEjecutandoSO() {
        return ejecutandoSO;
    }
}
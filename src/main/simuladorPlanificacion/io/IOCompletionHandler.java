package simuladorPlanificacion.io;

import main.modelo.PCB;

/**
 * Interfaz para manejar la finalización de operaciones de I/O
 */
public interface IOCompletionHandler {
    
    /**
     * Se invoca cuando una operación de I/O ha sido completada
     * 
     * @param pcb PCB del proceso que completó la operación de I/O
     */
    void onIOComplete(PCB pcb);
}

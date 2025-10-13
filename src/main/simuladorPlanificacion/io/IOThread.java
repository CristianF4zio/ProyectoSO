package simuladorPlanificacion.io;

import main.modelo.PCB;
import main.gestor.Reloj;

/**
 * Hilo para simular servicio de E/S de un proceso I/O-bound
 * y notificar su finalización para reingreso a Listos
 */
public final class IOThread implements Runnable {
    
    private final PCB pcb;
    private final int ciclosServicio;
    private final Reloj reloj;
    private final IOCompletionHandler handler;
    private volatile boolean finished;
    private final Object lock = new Object();
    
    /**
     * Constructor del hilo de I/O
     * 
     * @param pcb PCB del proceso que requiere I/O
     * @param ciclosServicio Número de ciclos simulados para completar el servicio
     * @param reloj Reloj del simulador para sincronización
     * @param handler Callback para notificar finalización
     */
    public IOThread(PCB pcb, int ciclosServicio, Reloj reloj, IOCompletionHandler handler) {
        this.pcb = pcb;
        this.ciclosServicio = ciclosServicio;
        this.reloj = reloj;
        this.handler = handler;
        this.finished = false;
    }
    
    /**
     * Ejecuta el servicio de I/O simulando la espera de ciclos
     */
    @Override
    public void run() {
        try {
            // Esperar los ciclos de servicio simulados
            esperarCiclos(ciclosServicio);
            
            // Marcar como terminado
            synchronized (lock) {
                finished = true;
            }
            
            // Notificar finalización al handler
            if (handler != null) {
                handler.onIOComplete(pcb);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Marcar como terminado incluso si fue interrumpido
            synchronized (lock) {
                finished = true;
            }
        }
    }
    
    /**
     * Espera un número específico de ciclos simulados
     * 
     * @param ciclos Número de ciclos a esperar
     * @throws InterruptedException Si el hilo es interrumpido
     */
    private void esperarCiclos(int ciclos) throws InterruptedException {
        if (reloj == null) {
            // Si no hay reloj, usar sleep como fallback
            Thread.sleep(ciclos * 10); // 10ms por ciclo
            return;
        }
        
        long cicloInicial = reloj.getCicloActual();
        long ciclosEsperados = cicloInicial + ciclos;
        
        synchronized (reloj) {
            while (reloj.getCicloActual() < ciclosEsperados) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("IOThread interrumpido");
                }
                reloj.wait(); // Esperar a que el reloj avance
            }
        }
    }
    
    /**
     * Verifica si el hilo ha terminado
     * 
     * @return true si ha terminado
     */
    public boolean isFinished() {
        synchronized (lock) {
            return finished;
        }
    }
    
    /**
     * Obtiene el PCB asociado a este hilo de I/O
     * 
     * @return PCB del proceso
     */
    public PCB getPCB() {
        return pcb;
    }
    
    /**
     * Obtiene el número de ciclos de servicio
     * 
     * @return Ciclos de servicio
     */
    public int getCiclosServicio() {
        return ciclosServicio;
    }
    
    @Override
    public String toString() {
        return String.format("IOThread[PCB=%d, Ciclos=%d, Finished=%s]", 
                           pcb.getId(), ciclosServicio, finished);
    }
}

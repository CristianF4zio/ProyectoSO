package main.hilos;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import main.modelo.TipoProceso;

/**
 * Hilo que representa la ejecución de un proceso
 * Simula la ejecución de instrucciones y genera excepciones de E/S para procesos IO-bound
 */
public class ProcesoThread extends Thread {

    private Proceso proceso;
    private boolean ejecutando;
    private int instruccionesEjecutadas;
    private IOThread ioThread;
    private int duracionCicloMs;

    /**
     * Constructor del hilo de proceso
     * 
     * @param proceso Proceso a ejecutar
     * @param ioThread Hilo de I/O para solicitudes
     * @param duracionCicloMs Duración de cada ciclo
     */
    public ProcesoThread(Proceso proceso, IOThread ioThread, int duracionCicloMs) {
        this.proceso = proceso;
        this.ioThread = ioThread;
        this.duracionCicloMs = duracionCicloMs;
        this.ejecutando = false;
        this.instruccionesEjecutadas = 0;
        setName("ProcesoThread-" + proceso.getNombre());
    }

    @Override
    public void run() {
        System.out.println("Iniciando ejecución de proceso: " + proceso.getNombre());
        proceso.setEstado(EstadoProceso.EJECUCION);
        proceso.iniciarEjecucion();
        
        while (ejecutando && instruccionesEjecutadas < proceso.getNumInstrucciones()) {
            try {
                // Ejecutar una instrucción
                ejecutarInstruccion();
                instruccionesEjecutadas++;
                
                // Si es IO-bound, hay probabilidad de generar excepción de E/S
                if (proceso.getTipo() == TipoProceso.IO_BOUND && Math.random() < 0.3) {
                    // Solicitar E/S (3 ciclos de bloqueo)
                    if (ioThread != null) {
                        proceso.setEstado(EstadoProceso.BLOQUEADO);
                        ioThread.solicitarIO(proceso, 3);
                        
                        // Esperar a que se complete la E/S
                        while (proceso.getEstado() == EstadoProceso.BLOQUEADO && ejecutando) {
                            Thread.sleep(duracionCicloMs);
                        }
                        
                        if (ejecutando) {
                            proceso.setEstado(EstadoProceso.EJECUCION);
                        }
                    }
                }
                
                // Esperar el tiempo de un ciclo
                Thread.sleep(duracionCicloMs);
                
            } catch (InterruptedException e) {
                if (!ejecutando) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("Error en proceso " + proceso.getNombre() + ": " + e.getMessage());
                break;
            }
        }
        
        // Finalizar proceso
        if (instruccionesEjecutadas >= proceso.getNumInstrucciones()) {
            proceso.setEstado(EstadoProceso.TERMINADO);
            proceso.finalizarEjecucion();
            System.out.println("Proceso completado: " + proceso.getNombre());
        } else {
            System.out.println("Proceso interrumpido: " + proceso.getNombre());
        }
    }

    /**
     * Simula la ejecución de una instrucción
     */
    private void ejecutarInstruccion() {
        // Incrementar PC (Program Counter)
        proceso.setPC(proceso.getPC() + 1);
        
        // Simular operación CPU
        // En un simulador real aquí se ejecutaría la instrucción
    }

    /**
     * Inicia la ejecución del proceso
     */
    public void iniciarEjecucion() {
        this.ejecutando = true;
        this.start();
    }

    /**
     * Pausa la ejecución del proceso
     */
    public void pausarEjecucion() {
        this.ejecutando = false;
    }

    /**
     * Detiene la ejecución del proceso
     */
    public void detenerEjecucion() {
        this.ejecutando = false;
        this.interrupt();
    }
    
    /**
     * Obtiene el proceso asociado
     * 
     * @return Proceso
     */
    public Proceso getProceso() {
        return proceso;
    }
    
    /**
     * Obtiene el número de instrucciones ejecutadas
     * 
     * @return Instrucciones ejecutadas
     */
    public int getInstruccionesEjecutadas() {
        return instruccionesEjecutadas;
    }
    
    /**
     * Verifica si el proceso está ejecutando
     * 
     * @return true si está ejecutando
     */
    public boolean isEjecutando() {
        return ejecutando;
    }
}

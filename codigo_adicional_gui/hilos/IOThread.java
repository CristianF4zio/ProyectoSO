package main.hilos;

import main.gestor.ExcepcionIO;
import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import java.util.concurrent.*;

/**
 * Hilo que maneja las operaciones de I/O
 * Procesa excepciones de E/S y desbloquea procesos cuando completan
 */
public class IOThread extends Thread {

    private BlockingQueue<ExcepcionIO> colaIO;
    private CopyOnWriteArrayList<ExcepcionIO> procesandoIO;
    private boolean ejecutando;
    private int duracionCicloMs;
    private Semaphore semaforo;

    /**
     * Constructor del hilo de I/O
     * 
     * @param duracionCicloMs Duración de cada ciclo en milisegundos
     */
    public IOThread(int duracionCicloMs) {
        this.colaIO = new LinkedBlockingQueue<>();
        this.procesandoIO = new CopyOnWriteArrayList<>();
        this.ejecutando = false;
        this.duracionCicloMs = duracionCicloMs;
        this.semaforo = new Semaphore(1);
        setName("IOThread");
    }

    /**
     * Solicita una operación de I/O para un proceso
     * 
     * @param p Proceso que solicita I/O
     * @param ciclosBloqueo Número de ciclos de bloqueo
     */
    public void solicitarIO(Proceso p, int ciclosBloqueo) {
        try {
            ExcepcionIO excepcionIO = new ExcepcionIO(p, ciclosBloqueo);
            colaIO.offer(excepcionIO);
            System.out.println("Solicitud I/O agregada: " + p.getNombre() + " por " + ciclosBloqueo + " ciclos");
        } catch (Exception e) {
            System.err.println("Error al solicitar I/O: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("IOThread iniciado");
        
        while (ejecutando || !colaIO.isEmpty() || !procesandoIO.isEmpty()) {
            try {
                // Procesar nuevas solicitudes de I/O
                ExcepcionIO nueva = colaIO.poll(100, TimeUnit.MILLISECONDS);
                if (nueva != null) {
                    procesandoIO.add(nueva);
                    Proceso p = nueva.getProceso();
                    p.setEstado(EstadoProceso.BLOQUEADO);
                    System.out.println("Proceso bloqueado por I/O: " + p.getNombre());
                }
                
                // Procesar ciclos de I/O en curso
                for (ExcepcionIO excepcionIO : procesandoIO) {
                    boolean completada = excepcionIO.procesarCiclo();
                    
                    if (completada) {
                        Proceso p = excepcionIO.getProceso();
                        p.setEstado(EstadoProceso.LISTO);
                        procesandoIO.remove(excepcionIO);
                        System.out.println("Proceso desbloqueado: " + p.getNombre());
                    }
                }
                
                // Esperar el tiempo de un ciclo
                Thread.sleep(duracionCicloMs);
                
            } catch (InterruptedException e) {
                if (!ejecutando) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("Error en IOThread: " + e.getMessage());
            }
        }
        
        System.out.println("IOThread detenido");
    }

    /**
     * Inicia el procesamiento de I/O
     */
    public void iniciarProcesamiento() {
        this.ejecutando = true;
        this.start();
    }

    /**
     * Pausa el procesamiento de I/O
     */
    public void pausarProcesamiento() {
        this.ejecutando = false;
    }

    /**
     * Detiene el procesamiento de I/O
     */
    public void detenerProcesamiento() {
        this.ejecutando = false;
        this.interrupt();
    }
    
    /**
     * Obtiene el número de operaciones de I/O pendientes
     * 
     * @return Número de operaciones pendientes
     */
    public int getOperacionesPendientes() {
        return colaIO.size() + procesandoIO.size();
    }
    
    /**
     * Obtiene las excepciones de I/O en proceso
     * 
     * @return Lista de excepciones en proceso
     */
    public CopyOnWriteArrayList<ExcepcionIO> getProcesandoIO() {
        return procesandoIO;
    }
    
    /**
     * Establece la duración del ciclo
     * 
     * @param duracionCicloMs Duración en milisegundos
     */
    public void setDuracionCicloMs(int duracionCicloMs) {
        if (duracionCicloMs > 0) {
            this.duracionCicloMs = duracionCicloMs;
        }
    }
}

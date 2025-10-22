package main.hilos;

import main.core.SistemaOperativoSimulado;

/**
 * Hilo principal que ejecuta la simulación del sistema operativo
 * Ejecuta ciclos de reloj continuamente
 */
public class SimuladorThread extends Thread {

    private boolean ejecutando;
    private boolean pausado;
    private SistemaOperativoSimulado sistemaOperativo;
    private int duracionCicloMs;

    /**
     * Constructor del hilo simulador
     * 
     * @param sistemaOperativo Sistema operativo a simular
     */
    public SimuladorThread(SistemaOperativoSimulado sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
        this.ejecutando = false;
        this.pausado = false;
        this.duracionCicloMs = sistemaOperativo.getConfiguracion().getDuracionCicloMs();
        setName("SimuladorThread");
    }

    @Override
    public void run() {
        System.out.println("=== Simulador iniciado ===");
        sistemaOperativo.iniciar();
        
        while (ejecutando) {
            try {
                // Si está pausado, esperar
                while (pausado && ejecutando) {
                    Thread.sleep(100);
                }
                
                if (!ejecutando) break;
                
                // Ejecutar un ciclo del sistema operativo
                sistemaOperativo.ejecutarCiclo();
                
                // Esperar el tiempo del ciclo
                Thread.sleep(duracionCicloMs);
                
                // Mostrar estado cada 10 ciclos
                if (sistemaOperativo.getCicloActual() % 10 == 0) {
                    mostrarEstado();
                }
                
            } catch (InterruptedException e) {
                if (!ejecutando) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("Error en simulación: " + e.getMessage());
                sistemaOperativo.getLogger().error("Error en simulación", e);
            }
        }
        
        // Detener el sistema operativo
        sistemaOperativo.detener();
        System.out.println("=== Simulador detenido ===");
    }

    /**
     * Muestra el estado actual del sistema
     */
    private void mostrarEstado() {
        int ciclo = sistemaOperativo.getCicloActual();
        String algoritmo = sistemaOperativo.getPlanificadorActual().getNombre();
        int listos = sistemaOperativo.getColaListos().size();
        int bloqueados = sistemaOperativo.getColaBloqueados().size();
        int terminados = sistemaOperativo.getColaTerminados().size();
        
        String procesoActual = "IDLE";
        if (sistemaOperativo.getProcesoEnEjecucion() != null) {
            procesoActual = sistemaOperativo.getProcesoEnEjecucion().getNombre();
        }
        
        System.out.println(String.format(
            "[Ciclo %d] %s | CPU: %s | Listos: %d | Bloqueados: %d | Terminados: %d",
            ciclo, algoritmo, procesoActual, listos, bloqueados, terminados
        ));
    }

    /**
     * Inicia la simulación
     */
    public void iniciarSimulacion() {
        this.ejecutando = true;
        this.pausado = false;
        this.start();
    }

    /**
     * Pausa la simulación
     */
    public void pausarSimulacion() {
        this.pausado = true;
        System.out.println("Simulación pausada");
    }
    
    /**
     * Reanuda la simulación
     */
    public void reanudarSimulacion() {
        this.pausado = false;
        System.out.println("Simulación reanudada");
    }

    /**
     * Detiene la simulación
     */
    public void detenerSimulacion() {
        this.ejecutando = false;
        this.pausado = false;
        this.interrupt();
    }
    
    /**
     * Verifica si la simulación está ejecutando
     * 
     * @return true si está ejecutando
     */
    public boolean isEjecutando() {
        return ejecutando;
    }
    
    /**
     * Verifica si la simulación está pausada
     * 
     * @return true si está pausada
     */
    public boolean isPausado() {
        return pausado;
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

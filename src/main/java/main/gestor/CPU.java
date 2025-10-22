package main.gestor;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import main.modelo.TipoProceso;
import java.util.Random;

public class CPU {

    private GestorMemoria gestorMemoria;
    private Proceso procesoEnEjecucion;
    private int cicloActual;
    private boolean ocupada;

    // Estadísticas
    private int totalInstruccionesEjecutadas;
    private int totalCiclosOcupada;
    private int totalCiclosLibre;
    private int totalExcepcionesIO;

    // Generador de números aleatorios para excepciones
    private Random random;

    public CPU(GestorMemoria gestorMemoria) {
        this.gestorMemoria = gestorMemoria;
        this.procesoEnEjecucion = null;
        this.cicloActual = 0;
        this.ocupada = false;

        this.totalInstruccionesEjecutadas = 0;
        this.totalCiclosOcupada = 0;
        this.totalCiclosLibre = 0;
        this.totalExcepcionesIO = 0;

        this.random = new Random();
    }

    public boolean ejecutarCiclo() {
        cicloActual++;

        if (procesoEnEjecucion != null) {
            return ejecutarInstruccion();
        } else {
            totalCiclosLibre++;
            return false;
        }
    }

    private boolean ejecutarInstruccion() {
        if (procesoEnEjecucion == null) {
            return false;
        }

        // Verificar que el proceso esté en memoria principal
        if (!gestorMemoria.estaEnMemoriaPrincipal(procesoEnEjecucion)) {
            System.out.println("Error: Proceso no está en memoria principal: " + procesoEnEjecucion.getNombre());
            return false;
        }

        ocupada = true;
        totalCiclosOcupada++;

        // Ejecutar instrucción
        procesoEnEjecucion.ejecutarInstruccion();
        totalInstruccionesEjecutadas++;

        // Verificar si el proceso ha terminado
        if (procesoEnEjecucion.isCompletado()) {
            System.out.println("Proceso completado: " + procesoEnEjecucion.getNombre());
            procesoEnEjecucion.setEstado(EstadoProceso.TERMINADO);
            procesoEnEjecucion = null;
            ocupada = false;
            return true;
        }

        // Verificar excepción de I/O para procesos I/O bound
        if (procesoEnEjecucion.getTipo() == TipoProceso.IO_BOUND) {
            if (debeGenerarExcepcionIO()) {
                generarExcepcionIO();
                return true;
            }
        }

        return true;
    }

    private boolean debeGenerarExcepcionIO() {
        if (procesoEnEjecucion.getTipo() != TipoProceso.IO_BOUND) {
            return false;
        }

        // Generar excepción basada en ciclos para excepción
        int ciclosParaExcepcion = procesoEnEjecucion.getCiclosParaExcepcionIO();
        if (ciclosParaExcepcion > 0) {
            procesoEnEjecucion.setCiclosTranscurridosIO(procesoEnEjecucion.getCiclosTranscurridosIO() + 1);
            return procesoEnEjecucion.getCiclosTranscurridosIO() >= ciclosParaExcepcion;
        }

        // Generar excepción aleatoria para procesos I/O bound
        return random.nextDouble() < 0.1; // 10% de probabilidad por ciclo
    }

    private void generarExcepcionIO() {
        if (procesoEnEjecucion != null) {
            procesoEnEjecucion.setEnOperacionIO(true);
            procesoEnEjecucion.setEstado(EstadoProceso.BLOQUEADO);
            totalExcepcionesIO++;

            System.out.println("Excepción de I/O generada para: " + procesoEnEjecucion.getNombre());
            procesoEnEjecucion = null;
            ocupada = false;
        }
    }

    public boolean asignarProceso(Proceso proceso) {
        if (proceso == null) {
            return false;
        }

        // Verificar que el proceso esté en memoria principal
        if (!gestorMemoria.estaEnMemoriaPrincipal(proceso)) {
            System.out.println(
                    "Error: No se puede asignar proceso no residente en memoria principal: " + proceso.getNombre());
            return false;
        }

        procesoEnEjecucion = proceso;
        proceso.setEstado(EstadoProceso.EJECUCION);
        ocupada = true;

        System.out.println("Proceso asignado a CPU: " + proceso.getNombre() +
                " (Dirección: " + gestorMemoria.obtenerDireccionMemoria(proceso) + ")");
        return true;
    }

    public Proceso liberarCPU() {
        if (procesoEnEjecucion != null) {
            Proceso liberado = procesoEnEjecucion;
            procesoEnEjecucion = null;
            ocupada = false;

            System.out.println("CPU liberada de proceso: " + liberado.getNombre());
            return liberado;
        }
        return null;
    }

    public Proceso getProcesoEnEjecucion() {
        return procesoEnEjecucion;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public int getCicloActual() {
        return cicloActual;
    }

    public int getDireccionMemoriaProceso() {
        if (procesoEnEjecucion != null) {
            return gestorMemoria.obtenerDireccionMemoria(procesoEnEjecucion);
        }
        return -1;
    }

    public int getProgramCounter() {
        if (procesoEnEjecucion != null) {
            return procesoEnEjecucion.getProgramCounter();
        }
        return -1;
    }

    public int getMemoryAddressRegister() {
        if (procesoEnEjecucion != null) {
            return procesoEnEjecucion.getMemoryAddressRegister();
        }
        return -1;
    }

    public int[] obtenerEstadisticas() {
        return new int[] {
                totalInstruccionesEjecutadas,
                totalCiclosOcupada,
                totalCiclosLibre,
                totalExcepcionesIO,
                cicloActual
        };
    }

    public String obtenerInformacionDetallada() {
        StringBuilder info = new StringBuilder();
        info.append("=== ESTADO DE CPU ===\n");
        info.append("Ciclo actual: ").append(cicloActual).append("\n");
        info.append("Estado: ").append(ocupada ? "OCUPADA" : "LIBRE").append("\n");

        if (procesoEnEjecucion != null) {
            info.append("Proceso en ejecución: ").append(procesoEnEjecucion.getNombre()).append("\n");
            info.append("Program Counter: ").append(procesoEnEjecucion.getProgramCounter()).append("\n");
            info.append("Memory Address Register: ").append(procesoEnEjecucion.getMemoryAddressRegister()).append("\n");
            info.append("Dirección en memoria: ").append(gestorMemoria.obtenerDireccionMemoria(procesoEnEjecucion))
                    .append("\n");
        } else {
            info.append("Proceso en ejecución: Ninguno\n");
        }

        info.append("Total instrucciones ejecutadas: ").append(totalInstruccionesEjecutadas).append("\n");
        info.append("Total ciclos ocupada: ").append(totalCiclosOcupada).append("\n");
        info.append("Total ciclos libre: ").append(totalCiclosLibre).append("\n");
        info.append("Total excepciones I/O: ").append(totalExcepcionesIO).append("\n");

        return info.toString();
    }

    public void reiniciarEstadisticas() {
        totalInstruccionesEjecutadas = 0;
        totalCiclosOcupada = 0;
        totalCiclosLibre = 0;
        totalExcepcionesIO = 0;
        cicloActual = 0;

        System.out.println("Estadísticas de CPU reiniciadas");
    }
}

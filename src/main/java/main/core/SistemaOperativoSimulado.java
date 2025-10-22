package main.core;

import main.gestor.*;
import main.modelo.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class SistemaOperativoSimulado {

    private GestorProcesos gestorProcesos;
    private GestorMemoria gestorMemoria;
    private GestorColas gestorColas;
    private Planificador planificador;
    private CPU cpu;
    private Reloj reloj;

    private int cicloActual;
    private boolean ejecutando;
    private Semaphore semaforoEstado;

    // Configuración básica
    private int duracionCicloMs;
    private int maxProcesos;
    private String politicaPlanificacion;

    public SistemaOperativoSimulado(int duracionCicloMs, int maxProcesos, String politicaPlanificacion) {
        this.duracionCicloMs = duracionCicloMs;
        this.maxProcesos = maxProcesos;
        this.politicaPlanificacion = politicaPlanificacion;

        // Inicializar componentes del sistema
        this.gestorProcesos = new GestorProcesos(maxProcesos);
        this.gestorMemoria = new GestorMemoria(1024, 2048); // 1MB principal, 2MB secundaria
        this.gestorColas = new GestorColas(gestorMemoria);
        this.planificador = new Planificador(gestorMemoria, gestorColas);
        this.cpu = new CPU(gestorMemoria);
        this.reloj = new Reloj(duracionCicloMs);

        this.cicloActual = 0;
        this.ejecutando = false;
        this.semaforoEstado = new Semaphore(1);

        System.out.println("Sistema Operativo Simulado inicializado con gestión de memoria");
        System.out.println("Memoria Principal: " + gestorMemoria.getTamanioMemoriaPrincipal() + " KB");
        System.out.println("Memoria Secundaria: " + gestorMemoria.getTamanioMemoriaSecundaria() + " KB");
    }

    public void iniciar() {
        ejecutando = true;
        System.out.println("=== SISTEMA OPERATIVO INICIADO ===");
    }

    public void ejecutarCiclo() {
        try {
            semaforoEstado.acquire();

            if (!ejecutando) {
                return;
            }

            cicloActual++;
            reloj.avanzarCiclo();

            // Ejecutar ciclo de CPU
            cpu.ejecutarCiclo();

            // Gestionar memoria
            gestorColas.gestionarMemoria();

            // Si no hay proceso en ejecución, seleccionar uno
            if (!cpu.isOcupada()) {
                Proceso siguiente = planificador.seleccionarSiguiente();
                if (siguiente != null) {
                    cpu.asignarProceso(siguiente);
                    System.out.println("Proceso seleccionado: " + siguiente.getNombre());
                }
            }

            // Verificar si el proceso actual ha terminado
            if (cpu.getProcesoEnEjecucion() != null && cpu.getProcesoEnEjecucion().isCompletado()) {
                Proceso terminado = cpu.liberarCPU();
                if (terminado != null) {
                    gestorColas.agregarATerminados(terminado);
                    System.out.println("Proceso terminado: " + terminado.getNombre());
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaforoEstado.release();
        }
    }

    public Proceso crearProceso(String nombre, int numInstrucciones, TipoProceso tipo, int prioridad) {
        try {
            Proceso proceso = gestorProcesos.crearProceso(nombre, numInstrucciones, tipo, prioridad);
            if (proceso != null) {
                // Intentar agregar a cola de listos
                if (gestorColas.agregarAListos(proceso)) {
                    System.out.println("Proceso creado: " + proceso.getNombre());
                    return proceso;
                } else {
                    System.out.println("Proceso creado pero suspendido por falta de memoria: " + proceso.getNombre());
                    return proceso;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al crear proceso: " + e.getMessage());
        }
        return null;
    }

    public boolean configurarPlanificador(String nombreAlgoritmo) {
        boolean configurado = planificador.configurarAlgoritmo(nombreAlgoritmo);
        if (configurado) {
            System.out.println("Planificador cambiado a: " + nombreAlgoritmo);
        }
        return configurado;
    }

    public void detener() {
        ejecutando = false;
        cpu.liberarCPU();
        System.out.println("=== SISTEMA OPERATIVO DETENIDO ===");
    }

    public int getCicloActual() {
        return cicloActual;
    }

    public Proceso getProcesoEnEjecucion() {
        return cpu.getProcesoEnEjecucion();
    }

    public String getAlgoritmoActual() {
        return planificador.getNombreAlgoritmo();
    }

    public List<Proceso> getColaListos() {
        return gestorColas.getColaListos();
    }

    public List<Proceso> getColaBloqueados() {
        return gestorColas.getColaBloqueados();
    }

    public List<Proceso> getColaSuspendidos() {
        return gestorColas.getColaSuspendidos();
    }

    public List<Proceso> getColaTerminados() {
        return gestorColas.getColaTerminados();
    }

    public List<Proceso> getColaListosSuspendidos() {
        return gestorColas.getColaListosSuspendidos();
    }

    public List<Proceso> getColaBloqueadosSuspendidos() {
        return gestorColas.getColaBloqueadosSuspendidos();
    }

    public int[] obtenerEstadisticas() {
        int[] statsMemoria = gestorMemoria.obtenerEstadisticas();
        int[] statsColas = gestorColas.obtenerEstadisticas();
        int[] statsCPU = cpu.obtenerEstadisticas();

        return new int[] {
                statsColas[0], // Listos
                statsColas[1], // Bloqueados
                statsColas[2], // Suspendidos
                statsColas[3], // Terminados
                statsMemoria[2], // Memoria disponible
                statsMemoria[3], // Procesos en memoria principal
                statsMemoria[4], // Procesos suspendidos
                statsCPU[0], // Instrucciones ejecutadas
                statsCPU[1], // Ciclos ocupada
                statsCPU[2] // Ciclos libre
        };
    }

    public String obtenerInformacionDetallada() {
        StringBuilder info = new StringBuilder();
        info.append("=== SISTEMA OPERATIVO SIMULADO ===\n");
        info.append("Ciclo actual: ").append(cicloActual).append("\n");
        info.append("Algoritmo: ").append(getAlgoritmoActual()).append("\n");
        info.append("\n");
        info.append(gestorMemoria.obtenerInformacionDetallada());
        info.append("\n");
        info.append(gestorColas.obtenerInformacionDetallada());
        info.append("\n");
        info.append(cpu.obtenerInformacionDetallada());
        info.append("\n");
        info.append(reloj.obtenerInformacionReloj());
        return info.toString();
    }

    public GestorMemoria getGestorMemoria() {
        return gestorMemoria;
    }

    public GestorColas getGestorColas() {
        return gestorColas;
    }

    public Planificador getPlanificador() {
        return planificador;
    }

    public CPU getCPU() {
        return cpu;
    }

    public Reloj getReloj() {
        return reloj;
    }
}
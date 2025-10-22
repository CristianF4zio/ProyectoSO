package main;

import main.config.ConfiguracionSimple;
import main.gestor.GestorProcesos;
import main.modelo.*;
import main.planificacion.*;
import java.util.Scanner;

/**
 * Demo simple en consola del simulador
 */
public class DemoSimuladorSimple {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    SIMULADOR DE SISTEMA OPERATIVO     ");
        System.out.println("========================================");
        System.out.println();

        // Crear configuración
        ConfiguracionSimple config = ConfiguracionSimple.porDefecto();
        
        // Crear gestor de procesos
        GestorProcesos gestorProcesos = new GestorProcesos(50);
        
        // Crear algoritmos
        FCFS fcfs = new FCFS();
        SJF sjf = new SJF();
        RoundRobin roundRobin = new RoundRobin(4);
        Prioridad prioridad = new Prioridad();

        // Crear procesos de prueba
        System.out.println("Creando procesos de prueba...");
        Proceso p1 = gestorProcesos.crearProceso("CPU_1", 15, TipoProceso.CPU_BOUND, 1);
        Proceso p2 = gestorProcesos.crearProceso("CPU_2", 20, TipoProceso.CPU_BOUND, 3);
        Proceso p3 = gestorProcesos.crearProceso("IO_1", 10, TipoProceso.IO_BOUND, 2);
        Proceso p4 = gestorProcesos.crearProceso("IO_2", 8, TipoProceso.IO_BOUND, 1);
        Proceso p5 = gestorProcesos.crearProceso("CPU_3", 12, TipoProceso.CPU_BOUND, 2);

        System.out.println();
        System.out.println("Procesos creados: " + gestorProcesos.getNumeroProcesosActivos());
        System.out.println();

        // Simular con FCFS
        System.out.println("=== SIMULANDO CON FCFS ===");
        simular(gestorProcesos, fcfs, 50);

        System.out.println();
        System.out.println("========================================");
        System.out.println("    SIMULACIÓN COMPLETADA               ");
        System.out.println("========================================");
    }

    private static void simular(GestorProcesos gestor, AlgoritmoPlanificacion algoritmo, int ciclos) {
        var procesosListos = gestor.getProcesosPorEstado(EstadoProceso.NUEVO);
        
        // Cambiar a LISTO
        for (Proceso p : procesosListos) {
            p.setEstado(EstadoProceso.LISTO);
        }
        
        procesosListos = gestor.getProcesosPorEstado(EstadoProceso.LISTO);
        algoritmo.reordenarCola(procesosListos);
        
        System.out.println("Orden de ejecución:");
        for (int i = 0; i < procesosListos.size(); i++) {
            Proceso p = procesosListos.get(i);
            System.out.println("  " + (i+1) + ". " + p.getNombre() + 
                             " (Instrucciones: " + p.getNumInstrucciones() + 
                             ", Prioridad: " + p.getPrioridad() + ")");
        }
    }
}


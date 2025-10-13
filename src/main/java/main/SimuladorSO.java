package main;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import main.modelo.TipoProceso;
import main.planificacion.*;
import main.gestor.GestorProcesos;
import java.util.List;
import java.util.ArrayList;

/**
 * Clase principal del Simulador de Sistema Operativo
 * Punto de entrada del programa
 */
public class SimuladorSO {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    SIMULADOR DE SISTEMA OPERATIVO     ");
        System.out.println("========================================");
        System.out.println();

        // Crear gestor de procesos
        GestorProcesos gestorProcesos = new GestorProcesos(50);
        
        // Crear algoritmos de planificación
        FCFS fcfs = new FCFS();
        SJF sjf = new SJF();
        RoundRobin roundRobin = new RoundRobin(4);
        Prioridad prioridad = new Prioridad();
        Multinivel multinivel = new Multinivel(3);
        MultinivelFeedback multinivelFeedback = new MultinivelFeedback(3);

        // Crear procesos de prueba
        System.out.println("Creando procesos de prueba...");
        Proceso proceso1 = gestorProcesos.crearProceso("Proceso1", 15, TipoProceso.CPU_BOUND, 1);
        Proceso proceso2 = gestorProcesos.crearProceso("Proceso2", 8, TipoProceso.IO_BOUND, 2);
        Proceso proceso3 = gestorProcesos.crearProceso("Proceso3", 12, TipoProceso.CPU_BOUND, 3);
        Proceso proceso4 = gestorProcesos.crearProceso("Proceso4", 6, TipoProceso.IO_BOUND, 1);
        Proceso proceso5 = gestorProcesos.crearProceso("Proceso5", 20, TipoProceso.CPU_BOUND, 2);

        System.out.println();
        System.out.println("Procesos creados: " + gestorProcesos.getNumeroProcesosActivos());
        System.out.println();

        // Probar algoritmo FCFS
        System.out.println("=== PROBANDO ALGORITMO FCFS ===");
        probarAlgoritmo(fcfs, gestorProcesos.getProcesosActivos());
        System.out.println();

        // Probar algoritmo SJF
        System.out.println("=== PROBANDO ALGORITMO SJF ===");
        probarAlgoritmo(sjf, gestorProcesos.getProcesosActivos());
        System.out.println();

        // Probar algoritmo Round Robin
        System.out.println("=== PROBANDO ALGORITMO ROUND ROBIN ===");
        probarAlgoritmo(roundRobin, gestorProcesos.getProcesosActivos());
        System.out.println();

        // Probar algoritmo de Prioridades
        System.out.println("=== PROBANDO ALGORITMO DE PRIORIDADES ===");
        probarAlgoritmo(prioridad, gestorProcesos.getProcesosActivos());
        System.out.println();

        // Probar algoritmo Multinivel
        System.out.println("=== PROBANDO ALGORITMO MULTINIVEL ===");
        probarAlgoritmo(multinivel, gestorProcesos.getProcesosActivos());
        System.out.println();

        // Probar algoritmo Multinivel con Feedback
        System.out.println("=== PROBANDO ALGORITMO MULTINIVEL CON FEEDBACK ===");
        probarAlgoritmo(multinivelFeedback, gestorProcesos.getProcesosActivos());
        System.out.println();

        // Mostrar estadísticas finales
        System.out.println("=== ESTADÍSTICAS FINALES ===");
        mostrarEstadisticas(gestorProcesos);

        System.out.println();
        System.out.println("========================================");
        System.out.println("    SIMULACIÓN COMPLETADA EXITOSAMENTE  ");
        System.out.println("========================================");
    }

    /**
     * Prueba un algoritmo de planificación con una lista de procesos
     */
    private static void probarAlgoritmo(AlgoritmoPlanificacion algoritmo, List<Proceso> procesos) {
        System.out.println("Algoritmo: " + algoritmo.getNombre());
        
        // Crear copia de la lista para no modificar la original
        List<Proceso> procesosCopia = new ArrayList<>(procesos);
        
        // Reordenar cola según el algoritmo
        algoritmo.reordenarCola(procesosCopia);
        
        // Mostrar orden de ejecución
        System.out.println("Orden de ejecución:");
        for (int i = 0; i < procesosCopia.size(); i++) {
            Proceso p = procesosCopia.get(i);
            System.out.println("  " + (i + 1) + ". " + p.getNombre() + 
                             " (Instrucciones: " + p.getNumInstrucciones() + 
                             ", Prioridad: " + p.getPrioridad() + ")");
        }
        
        // Seleccionar siguiente proceso
        Proceso siguiente = algoritmo.seleccionarSiguiente(procesosCopia);
        if (siguiente != null) {
            System.out.println("Siguiente proceso a ejecutar: " + siguiente.getNombre());
        }
    }

    /**
     * Muestra estadísticas del gestor de procesos
     */
    private static void mostrarEstadisticas(GestorProcesos gestorProcesos) {
        int[] estadisticas = gestorProcesos.getEstadisticasProcesos();
        System.out.println("Total de procesos: " + estadisticas[0]);
        System.out.println("Procesos nuevos: " + estadisticas[1]);
        System.out.println("Procesos listos: " + estadisticas[2]);
        System.out.println("Procesos ejecutando: " + estadisticas[3]);
        System.out.println("Procesos bloqueados: " + estadisticas[4]);
        System.out.println("Procesos terminados: " + estadisticas[5]);
        
        System.out.println();
        System.out.println("Procesos por tipo:");
        System.out.println("CPU_BOUND: " + gestorProcesos.getProcesosPorTipo(TipoProceso.CPU_BOUND).size());
        System.out.println("IO_BOUND: " + gestorProcesos.getProcesosPorTipo(TipoProceso.IO_BOUND).size());
    }
}

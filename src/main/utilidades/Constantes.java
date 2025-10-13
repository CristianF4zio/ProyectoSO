package main.utilidades;

/**
 * Clase que contiene las constantes del sistema
 */
public class Constantes {

    // Estados de procesos
    public static final String ESTADO_NUEVO = "NUEVO";
    public static final String ESTADO_LISTO = "LISTO";
    public static final String ESTADO_EJECUCION = "EJECUCION";
    public static final String ESTADO_BLOQUEADO = "BLOQUEADO";
    public static final String ESTADO_TERMINADO = "TERMINADO";
    public static final String ESTADO_SUSPENDIDO = "SUSPENDIDO";

    // Tipos de procesos
    public static final String TIPO_CPU_BOUND = "CPU_BOUND";
    public static final String TIPO_IO_BOUND = "IO_BOUND";

    // Algoritmos de planificación
    public static final String ALGORITMO_FCFS = "FCFS";
    public static final String ALGORITMO_SJF = "SJF";
    public static final String ALGORITMO_ROUND_ROBIN = "ROUND_ROBIN";
    public static final String ALGORITMO_PRIORIDAD = "PRIORIDAD";
    public static final String ALGORITMO_MULTINIVEL = "MULTINIVEL";
    public static final String ALGORITMO_MULTINIVEL_FEEDBACK = "MULTINIVEL_FEEDBACK";

    // Configuración por defecto
    public static final long DURACION_CICLO_DEFAULT = 1000; // 1 segundo
    public static final int INSTRUCCIONES_DEFAULT = 10;
    public static final int CICLOS_EXCEPCION_DEFAULT = 3;
    public static final int CICLOS_COMPLETAR_DEFAULT = 5;
    public static final int QUANTUM_DEFAULT = 3;

    // Archivos
    public static final String ARCHIVO_CONFIG_JSON = "configuracion.json";
    public static final String ARCHIVO_CONFIG_CSV = "configuracion.csv";
    public static final String ARCHIVO_LOG = "simulacion.log";
}

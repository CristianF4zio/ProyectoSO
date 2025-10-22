package main.core;

import main.config.Configuracion;
import main.gestor.GestorProcesos;
import main.hilos.IOThread;
import main.logging.LoggerSistema;
import main.metricas.MetricasRendimiento;
import main.modelo.*;
import main.planificacion.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Clase principal que simula un sistema operativo
 * Coordina la planificación, ejecución y gestión de procesos
 */
public class SistemaOperativoSimulado {
    
    private Configuracion configuracion;
    private GestorProcesos gestorProcesos;
    private AlgoritmoPlanificacion planificadorActual;
    private IOThread ioThread;
    private LoggerSistema logger;
    private MetricasRendimiento metricas;
    
    private List<Proceso> colaListos;
    private Proceso procesoEnEjecucion;
    private List<Proceso> colaBloqueados;
    private List<Proceso> colaSuspendidos;
    private List<Proceso> colaTerminados;
    
    private int cicloActual;
    private int quantumRestante;
    private boolean ejecutando;
    private Semaphore semaforoEstado;
    
    // Algoritmos disponibles
    private Map<String, AlgoritmoPlanificacion> algoritmos;
    
    /**
     * Constructor del sistema operativo simulado
     * 
     * @param configuracion Configuración del sistema
     */
    public SistemaOperativoSimulado(Configuracion configuracion) {
        this.configuracion = configuracion;
        this.gestorProcesos = new GestorProcesos(configuracion.getMaxProcesos());
        this.logger = new LoggerSistema(configuracion.isLoggingHabilitado());
        this.metricas = new MetricasRendimiento();
        
        this.colaListos = new CopyOnWriteArrayList<>();
        this.colaBloqueados = new CopyOnWriteArrayList<>();
        this.colaSuspendidos = new CopyOnWriteArrayList<>();
        this.colaTerminados = new CopyOnWriteArrayList<>();
        
        this.cicloActual = 0;
        this.quantumRestante = 0;
        this.ejecutando = false;
        this.semaforoEstado = new Semaphore(1);
        
        inicializarAlgoritmos();
        configurarPlanificador(configuracion.getPoliticaPlanificacion());
        
        // Inicializar hilo de I/O
        this.ioThread = new IOThread(configuracion.getDuracionCicloMs());
        
        logger.info("Sistema Operativo Simulado inicializado");
    }
    
    /**
     * Inicializa los algoritmos de planificación disponibles
     */
    private void inicializarAlgoritmos() {
        algoritmos = new HashMap<>();
        algoritmos.put("FCFS", new FCFS());
        algoritmos.put("SJF", new SJF());
        algoritmos.put("SRTF", new SRTF());
        algoritmos.put("PRIORIDAD", new Prioridad());
        algoritmos.put("ROUND_ROBIN", new RoundRobin(configuracion.getQuantumMs() / configuracion.getDuracionCicloMs()));
        algoritmos.put("MULTINIVEL", new Multinivel(3));
        algoritmos.put("MULTINIVEL_FEEDBACK", new MultinivelFeedback(3));
    }
    
    /**
     * Configura el planificador activo
     * 
     * @param nombreAlgoritmo Nombre del algoritmo
     */
    public void configurarPlanificador(String nombreAlgoritmo) {
        AlgoritmoPlanificacion nuevo = algoritmos.get(nombreAlgoritmo.toUpperCase());
        if (nuevo != null) {
            planificadorActual = nuevo;
            logger.eventoPlanificador("Planificador cambiado a: " + nombreAlgoritmo);
            System.out.println("Planificador configurado: " + planificadorActual.getNombre());
        } else {
            logger.warn("Algoritmo no encontrado: " + nombreAlgoritmo);
        }
    }
    
    /**
     * Inicia el sistema operativo
     */
    public void iniciar() {
        ejecutando = true;
        ioThread.iniciarProcesamiento();
        logger.info("Sistema Operativo iniciado");
    }
    
    /**
     * Detiene el sistema operativo
     */
    public void detener() {
        ejecutando = false;
        ioThread.detenerProcesamiento();
        logger.info("Sistema Operativo detenido");
    }
    
    /**
     * Ejecuta un ciclo de reloj del sistema
     */
    public void ejecutarCiclo() {
        try {
            semaforoEstado.acquire();
            cicloActual++;
            
            // Actualizar colas de bloqueados
            actualizarColaBloqueados();
            
            // Si no hay proceso en ejecución o se acabó el quantum, planificar
            if (procesoEnEjecucion == null || 
                (planificadorActual.esPreemptivo() && quantumRestante <= 0)) {
                planificar();
            }
            
            // Ejecutar proceso actual
            if (procesoEnEjecucion != null) {
                ejecutarProceso();
                metricas.registrarTick(cicloActual, true);
            } else {
                metricas.registrarTick(cicloActual, false);
            }
            
            semaforoEstado.release();
            
        } catch (InterruptedException e) {
            logger.error("Error en ciclo de ejecución", e);
        }
    }
    
    /**
     * Actualiza la cola de procesos bloqueados
     */
    private void actualizarColaBloqueados() {
        List<Proceso> desbloqueados = new ArrayList<>();
        
        for (Proceso p : colaBloqueados) {
            if (p.getEstado() == EstadoProceso.LISTO) {
                desbloqueados.add(p);
            }
        }
        
        for (Proceso p : desbloqueados) {
            colaBloqueados.remove(p);
            agregarAColaListos(p);
            logger.info("Proceso desbloqueado: " + p.getNombre());
        }
    }
    
    /**
     * Planifica el siguiente proceso a ejecutar
     */
    private void planificar() {
        // Si hay proceso en ejecución, devolverlo a la cola
        if (procesoEnEjecucion != null && procesoEnEjecucion.getEstado() != EstadoProceso.TERMINADO) {
            procesoEnEjecucion.setEstado(EstadoProceso.LISTO);
            agregarAColaListos(procesoEnEjecucion);
            procesoEnEjecucion = null;
        }
        
        // Reordenar cola según el algoritmo
        planificadorActual.reordenarCola(colaListos);
        
        // Seleccionar siguiente proceso
        Proceso siguiente = planificadorActual.seleccionarSiguiente(colaListos);
        
        if (siguiente != null) {
            colaListos.remove(siguiente);
            procesoEnEjecucion = siguiente;
            procesoEnEjecucion.setEstado(EstadoProceso.EJECUCION);
            
            // Reiniciar quantum si es Round Robin
            if (planificadorActual instanceof RoundRobin) {
                quantumRestante = ((RoundRobin) planificadorActual).getQuantum();
            }
            
            metricas.registrarDespacho(procesoEnEjecucion, cicloActual);
            logger.eventoPlanificador("Proceso despachado: " + procesoEnEjecucion.getNombre());
        }
    }
    
    /**
     * Ejecuta el proceso actual por un ciclo
     */
    private void ejecutarProceso() {
        if (procesoEnEjecucion == null) return;
        
        // Incrementar PC
        procesoEnEjecucion.setPC(procesoEnEjecucion.getPC() + 1);
        
        // Decrementar quantum
        if (planificadorActual.esPreemptivo()) {
            quantumRestante--;
        }
        
        // Verificar si se genera excepción de I/O (para IO-bound)
        if (procesoEnEjecucion.getTipo() == TipoProceso.IO_BOUND && Math.random() < 0.2) {
            generarExcepcionIO();
        }
        
        // Verificar si el proceso completó todas sus instrucciones
        if (procesoEnEjecucion.getPC() >= procesoEnEjecucion.getNumInstrucciones()) {
            finalizarProceso();
        }
    }
    
    /**
     * Genera una excepción de I/O para el proceso actual
     */
    private void generarExcepcionIO() {
        if (procesoEnEjecucion == null) return;
        
        int ciclosBloqueo = configuracion.getCiclosExcepcionIO();
        ioThread.solicitarIO(procesoEnEjecucion, ciclosBloqueo);
        
        procesoEnEjecucion.setEstado(EstadoProceso.BLOQUEADO);
        colaBloqueados.add(procesoEnEjecucion);
        logger.info("Proceso bloqueado por I/O: " + procesoEnEjecucion.getNombre());
        
        procesoEnEjecucion = null;
    }
    
    /**
     * Finaliza el proceso actual
     */
    private void finalizarProceso() {
        if (procesoEnEjecucion == null) return;
        
        procesoEnEjecucion.setEstado(EstadoProceso.TERMINADO);
        procesoEnEjecucion.finalizarEjecucion();
        colaTerminados.add(procesoEnEjecucion);
        
        metricas.registrarFinalizacion(procesoEnEjecucion, cicloActual);
        logger.info("Proceso finalizado: " + procesoEnEjecucion.getNombre());
        
        procesoEnEjecucion = null;
    }
    
    /**
     * Crea un nuevo proceso y lo agrega al sistema
     * 
     * @param nombre Nombre del proceso
     * @param numInstrucciones Número de instrucciones
     * @param tipo Tipo de proceso
     * @param prioridad Prioridad
     * @return Proceso creado
     */
    public Proceso crearProceso(String nombre, int numInstrucciones, TipoProceso tipo, int prioridad) {
        Proceso p = gestorProcesos.crearProceso(nombre, numInstrucciones, tipo, prioridad);
        if (p != null) {
            agregarAColaListos(p);
            logger.info("Proceso creado: " + nombre);
        }
        return p;
    }
    
    /**
     * Agrega un proceso a la cola de listos
     * 
     * @param p Proceso a agregar
     */
    private void agregarAColaListos(Proceso p) {
        p.setEstado(EstadoProceso.LISTO);
        colaListos.add(p);
        metricas.registrarIngresoCola(p, cicloActual);
    }
    
    // Getters
    
    public Configuracion getConfiguracion() {
        return configuracion;
    }
    
    public GestorProcesos getGestorProcesos() {
        return gestorProcesos;
    }
    
    public AlgoritmoPlanificacion getPlanificadorActual() {
        return planificadorActual;
    }
    
    public LoggerSistema getLogger() {
        return logger;
    }
    
    public MetricasRendimiento getMetricas() {
        return metricas;
    }
    
    public List<Proceso> getColaListos() {
        return new ArrayList<>(colaListos);
    }
    
    public Proceso getProcesoEnEjecucion() {
        return procesoEnEjecucion;
    }
    
    public List<Proceso> getColaBloqueados() {
        return new ArrayList<>(colaBloqueados);
    }
    
    public List<Proceso> getColaSuspendidos() {
        return new ArrayList<>(colaSuspendidos);
    }
    
    public List<Proceso> getColaTerminados() {
        return new ArrayList<>(colaTerminados);
    }
    
    public int getCicloActual() {
        return cicloActual;
    }
    
    public boolean isEjecutando() {
        return ejecutando;
    }
    
    public IOThread getIoThread() {
        return ioThread;
    }
}


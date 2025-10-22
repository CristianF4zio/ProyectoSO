package main.interfaz;

import main.core.SistemaOperativoSimulado;
import main.config.Configuracion;
import main.config.GestorConfiguracion;
import main.hilos.SimuladorThread;
import main.modelo.TipoProceso;
import main.modelo.Proceso;
import javax.swing.Timer;

/**
 * Controlador principal de la interfaz gráfica
 * Conecta la interfaz con el simulador y coordina las actualizaciones
 */
public class ControladorInterfaz {

    private SistemaOperativoSimulado sistemaOperativo;
    private SimuladorThread simuladorThread;
    private VentanaPrincipal ventana;
    private Timer timerActualizacion;
    private Configuracion configuracion;

    /**
     * Constructor del controlador
     */
    public ControladorInterfaz() {
        // Cargar o crear configuración
        configuracion = GestorConfiguracion.cargarConfiguracionJson();
        if (configuracion == null) {
            configuracion = Configuracion.porDefecto();
        }
        
        // Crear sistema operativo
        sistemaOperativo = new SistemaOperativoSimulado(configuracion);
        
        // Crear ventana principal
        ventana = new VentanaPrincipal(this);
        
        // Crear timer para actualizar interfaz (cada 100ms)
        timerActualizacion = new Timer(100, e -> actualizarInterfaz());
    }

    /**
     * Inicia la simulación
     */
    public void iniciarSimulacion() {
        if (simuladorThread == null || !simuladorThread.isAlive()) {
            simuladorThread = new SimuladorThread(sistemaOperativo);
            simuladorThread.iniciarSimulacion();
            timerActualizacion.start();
            sistemaOperativo.getLogger().info("Simulación iniciada desde interfaz");
        } else if (simuladorThread.isPausado()) {
            simuladorThread.reanudarSimulacion();
            timerActualizacion.start();
            sistemaOperativo.getLogger().info("Simulación reanudada desde interfaz");
        }
    }

    /**
     * Pausa la simulación
     */
    public void pausarSimulacion() {
        if (simuladorThread != null && simuladorThread.isAlive()) {
            simuladorThread.pausarSimulacion();
            timerActualizacion.stop();
            sistemaOperativo.getLogger().info("Simulación pausada desde interfaz");
        }
    }

    /**
     * Detiene la simulación
     */
    public void detenerSimulacion() {
        if (simuladorThread != null) {
            simuladorThread.detenerSimulacion();
            timerActualizacion.stop();
            sistemaOperativo.getLogger().info("Simulación detenida desde interfaz");
            
            try {
                simuladorThread.join(2000); // Esperar hasta 2 segundos
            } catch (InterruptedException e) {
                System.err.println("Error al detener simulador: " + e.getMessage());
            }
            
            simuladorThread = null;
        }
    }

    /**
     * Cambia el algoritmo de planificación
     * 
     * @param algoritmo Nombre del algoritmo
     */
    public void cambiarAlgoritmo(String algoritmo) {
        sistemaOperativo.configurarPlanificador(algoritmo);
        configuracion.setPoliticaPlanificacion(algoritmo);
        sistemaOperativo.getLogger().eventoPlanificador("Algoritmo cambiado a: " + algoritmo);
    }
    
    /**
     * Cambia la duración del ciclo
     * 
     * @param duracionMs Duración en milisegundos
     */
    public void cambiarDuracionCiclo(int duracionMs) {
        configuracion.setDuracionCicloMs(duracionMs);
        if (simuladorThread != null) {
            simuladorThread.setDuracionCicloMs(duracionMs);
        }
        sistemaOperativo.getLogger().info("Duración de ciclo cambiada a: " + duracionMs + "ms");
    }
    
    /**
     * Crea un nuevo proceso
     * 
     * @param nombre Nombre del proceso
     * @param instrucciones Número de instrucciones
     * @param tipo Tipo de proceso
     * @param prioridad Prioridad
     */
    public void crearProceso(String nombre, int instrucciones, TipoProceso tipo, int prioridad) {
        Proceso p = sistemaOperativo.crearProceso(nombre, instrucciones, tipo, prioridad);
        if (p != null) {
            sistemaOperativo.getLogger().info("Proceso creado desde interfaz: " + nombre);
        }
    }
    
    /**
     * Guarda la configuración actual
     */
    public void guardarConfiguracion() {
        GestorConfiguracion.guardarConfiguracionJson(configuracion);
        sistemaOperativo.getLogger().info("Configuración guardada");
    }
    
    /**
     * Actualiza todos los paneles de la interfaz
     */
    private void actualizarInterfaz() {
        if (ventana != null) {
            ventana.actualizar(sistemaOperativo);
        }
    }
    
    /**
     * Muestra la ventana principal
     */
    public void mostrarVentana() {
        ventana.setVisible(true);
    }
    
    // Getters
    
    public SistemaOperativoSimulado getSistemaOperativo() {
        return sistemaOperativo;
    }
    
    public Configuracion getConfiguracion() {
        return configuracion;
    }
    
    public VentanaPrincipal getVentana() {
        return ventana;
    }
    
    public boolean isSimulacionActiva() {
        return simuladorThread != null && simuladorThread.isAlive();
    }
    
    public boolean isSimulacionPausada() {
        return simuladorThread != null && simuladorThread.isPausado();
    }
}

package main;

import main.interfaz.ControladorInterfaz;
import main.config.Configuracion;
import main.config.GestorConfiguracion;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal para ejecutar el simulador con interfaz gráfica
 * Punto de entrada de la aplicación GUI
 */
public class DemoSimuladorGUI {

    /**
     * Método principal
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Configurar Look and Feel
        configurarLookAndFeel();
        
        // Ejecutar en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("========================================");
                System.out.println("  SIMULADOR DE SISTEMA OPERATIVO - GUI ");
                System.out.println("========================================");
                System.out.println();
                
                // Crear y mostrar la interfaz
                ControladorInterfaz controlador = new ControladorInterfaz();
                controlador.mostrarVentana();
                
                System.out.println("Interfaz gráfica iniciada correctamente");
                System.out.println("Usa los controles de la ventana para gestionar la simulación");
                System.out.println();
                
                // Crear algunos procesos de ejemplo
                crearProcesosEjemplo(controlador);
                
            } catch (Exception e) {
                System.err.println("Error al iniciar la interfaz gráfica:");
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Configura el Look and Feel de la aplicación
     */
    private static void configurarLookAndFeel() {
        try {
            // Intentar usar el Look and Feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo configurar el Look and Feel: " + e.getMessage());
            // Continuar con el Look and Feel por defecto
        }
    }
    
    /**
     * Crea algunos procesos de ejemplo para demostración
     * 
     * @param controlador Controlador de la interfaz
     */
    private static void crearProcesosEjemplo(ControladorInterfaz controlador) {
        System.out.println("Creando procesos de ejemplo...");
        
        // Procesos CPU-bound
        controlador.crearProceso("CPU_1", 15, main.modelo.TipoProceso.CPU_BOUND, 1);
        controlador.crearProceso("CPU_2", 20, main.modelo.TipoProceso.CPU_BOUND, 3);
        controlador.crearProceso("CPU_3", 12, main.modelo.TipoProceso.CPU_BOUND, 2);
        
        // Procesos IO-bound
        controlador.crearProceso("IO_1", 10, main.modelo.TipoProceso.IO_BOUND, 2);
        controlador.crearProceso("IO_2", 8, main.modelo.TipoProceso.IO_BOUND, 1);
        
        System.out.println("5 procesos de ejemplo creados");
        System.out.println("Presiona 'Iniciar' en la interfaz para comenzar la simulación");
    }
}


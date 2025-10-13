package main.gestor;

import main.modelo.Proceso;
import java.util.List;

/**
 * Clase que gestiona la creación, eliminación y control de procesos
 */
public class GestorProcesos {

    /**
     * Crea un nuevo proceso
     * 
     * @param nombre           Nombre del proceso
     * @param numInstrucciones Número de instrucciones
     * @param tipoProceso      Tipo de proceso (CPU_BOUND o IO_BOUND)
     * @return Proceso creado
     */
    public Proceso crearProceso(String nombre, int numInstrucciones, String tipoProceso) {
        // TODO: Implementar creación de procesos
        return null;
    }

    /**
     * Elimina un proceso del sistema
     * 
     * @param proceso Proceso a eliminar
     */
    public void eliminarProceso(Proceso proceso) {
        // TODO: Implementar eliminación de procesos
    }

    /**
     * Obtiene todos los procesos en un estado específico
     * 
     * @param estado Estado de los procesos a buscar
     * @return Lista de procesos en el estado especificado
     */
    public List<Proceso> getProcesosPorEstado(String estado) {
        // TODO: Implementar búsqueda por estado
        return null;
    }
}
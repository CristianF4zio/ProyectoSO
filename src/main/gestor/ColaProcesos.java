package main.gestor;

import main.modelo.Proceso;
import java.util.List;

/**
 * Clase que representa una cola de procesos
 */
public class ColaProcesos {

    private List<Proceso> procesos;
    private String tipoCola; // "listos", "bloqueados", "suspendidos"

    /**
     * Constructor de la cola
     * 
     * @param tipoCola Tipo de cola
     */
    public ColaProcesos(String tipoCola) {
        this.tipoCola = tipoCola;
        // TODO: Inicializar lista de procesos
    }

    /**
     * Agrega un proceso a la cola
     * 
     * @param proceso Proceso a agregar
     */
    public void agregarProceso(Proceso proceso) {
        // TODO: Implementar agregado de proceso
    }

    /**
     * Remueve un proceso de la cola
     * 
     * @param proceso Proceso a remover
     */
    public void removerProceso(Proceso proceso) {
        // TODO: Implementar remoción de proceso
    }

    /**
     * Obtiene el primer proceso de la cola
     * 
     * @return Primer proceso o null si está vacía
     */
    public Proceso obtenerPrimero() {
        // TODO: Implementar obtención del primer proceso
        return null;
    }

    /**
     * Verifica si la cola está vacía
     * 
     * @return true si está vacía
     */
    public boolean estaVacia() {
        // TODO: Implementar verificación de cola vacía
        return true;
    }

    /**
     * Obtiene todos los procesos de la cola
     * 
     * @return Lista de procesos
     */
    public List<Proceso> getProcesos() {
        return procesos;
    }
}
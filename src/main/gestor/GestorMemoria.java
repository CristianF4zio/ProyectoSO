package main.gestor;

import main.modelo.Proceso;

/**
 * Clase que gestiona la memoria y los estados suspendidos
 */
public class GestorMemoria {

    /**
     * Verifica si un proceso necesita ser suspendido por falta de memoria
     * 
     * @param proceso Proceso a verificar
     * @return true si debe ser suspendido
     */
    public boolean debeSuspenderse(Proceso proceso) {
        // TODO: Implementar lógica de suspensión por memoria
        return false;
    }

    /**
     * Suspende un proceso por falta de memoria
     * 
     * @param proceso Proceso a suspender
     */
    public void suspenderProceso(Proceso proceso) {
        // TODO: Implementar suspensión de proceso
    }

    /**
     * Reactiva un proceso suspendido
     * 
     * @param proceso Proceso a reactivar
     */
    public void reactivarProceso(Proceso proceso) {
        // TODO: Implementar reactivación de proceso
    }
}
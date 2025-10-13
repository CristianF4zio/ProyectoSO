package main.gestor;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import main.modelo.TipoProceso;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;

/**
 * Clase que gestiona la creación, eliminación y control de procesos
 * Maneja el ciclo de vida completo de los procesos en el sistema
 */
public class GestorProcesos {

    private List<Proceso> procesosActivos;
    private AtomicInteger contadorId;
    private int maxProcesos;

    public GestorProcesos() {
        this.procesosActivos = new ArrayList<>();
        this.contadorId = new AtomicInteger(1);
        this.maxProcesos = 100; // Límite por defecto
    }

    public GestorProcesos(int maxProcesos) {
        this();
        this.maxProcesos = maxProcesos;
    }

    /**
     * Crea un nuevo proceso
     * 
     * @param nombre           Nombre del proceso
     * @param numInstrucciones Número de instrucciones
     * @param tipoProceso      Tipo de proceso (CPU_BOUND o IO_BOUND)
     * @param prioridad        Prioridad del proceso
     * @return Proceso creado o null si no se pudo crear
     */
    public Proceso crearProceso(String nombre, int numInstrucciones, TipoProceso tipoProceso, int prioridad) {
        // Verificar límite de procesos
        if (procesosActivos.size() >= maxProcesos) {
            System.err.println("No se puede crear más procesos. Límite alcanzado: " + maxProcesos);
            return null;
        }

        // Validar parámetros
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre del proceso no puede estar vacío");
            return null;
        }

        if (numInstrucciones <= 0) {
            System.err.println("El número de instrucciones debe ser mayor a 0");
            return null;
        }

        if (tipoProceso == null) {
            System.err.println("El tipo de proceso no puede ser null");
            return null;
        }

        // Generar ID único
        int id = contadorId.getAndIncrement();

        // Crear el proceso
        Proceso nuevoProceso = new Proceso(id, nombre, tipoProceso, numInstrucciones, prioridad);

        // Agregar a la lista de procesos activos
        procesosActivos.add(nuevoProceso);

        System.out.println("Proceso creado: " + nuevoProceso);
        return nuevoProceso;
    }

    /**
     * Crea un nuevo proceso con valores por defecto
     * 
     * @param nombre Nombre del proceso
     * @return Proceso creado
     */
    public Proceso crearProceso(String nombre) {
        return crearProceso(nombre, 10, TipoProceso.CPU_BOUND, 5);
    }

    /**
     * Elimina un proceso del sistema
     * 
     * @param proceso Proceso a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarProceso(Proceso proceso) {
        if (proceso == null) {
            return false;
        }

        // Cambiar estado a terminado
        proceso.setEstado(EstadoProceso.TERMINADO);
        proceso.finalizarEjecucion();

        // Remover de la lista de procesos activos
        boolean eliminado = procesosActivos.remove(proceso);

        if (eliminado) {
            System.out.println("Proceso eliminado: " + proceso);
        }

        return eliminado;
    }

    /**
     * Elimina un proceso por ID
     * 
     * @param id ID del proceso a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarProceso(int id) {
        Proceso proceso = buscarProcesoPorId(id);
        return eliminarProceso(proceso);
    }

    /**
     * Busca un proceso por ID
     * 
     * @param id ID del proceso
     * @return Proceso encontrado o null
     */
    public Proceso buscarProcesoPorId(int id) {
        return procesosActivos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Busca procesos por nombre
     * 
     * @param nombre Nombre del proceso
     * @return Lista de procesos con ese nombre
     */
    public List<Proceso> buscarProcesosPorNombre(String nombre) {
        if (nombre == null) {
            return new ArrayList<>();
        }

        return procesosActivos.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Obtiene todos los procesos en un estado específico
     * 
     * @param estado Estado de los procesos a buscar
     * @return Lista de procesos en el estado especificado
     */
    public List<Proceso> getProcesosPorEstado(EstadoProceso estado) {
        if (estado == null) {
            return new ArrayList<>();
        }

        return procesosActivos.stream()
                .filter(p -> p.getEstado() == estado)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Obtiene todos los procesos de un tipo específico
     * 
     * @param tipo Tipo de proceso
     * @return Lista de procesos del tipo especificado
     */
    public List<Proceso> getProcesosPorTipo(TipoProceso tipo) {
        if (tipo == null) {
            return new ArrayList<>();
        }

        return procesosActivos.stream()
                .filter(p -> p.getTipo() == tipo)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Obtiene todos los procesos activos
     * 
     * @return Lista de todos los procesos activos
     */
    public List<Proceso> getProcesosActivos() {
        return new ArrayList<>(procesosActivos);
    }

    /**
     * Obtiene el número total de procesos activos
     * 
     * @return Número de procesos activos
     */
    public int getNumeroProcesosActivos() {
        return procesosActivos.size();
    }

    /**
     * Obtiene el número de procesos por estado
     * 
     * @param estado Estado a contar
     * @return Número de procesos en ese estado
     */
    public int contarProcesosPorEstado(EstadoProceso estado) {
        return (int) procesosActivos.stream()
                .filter(p -> p.getEstado() == estado)
                .count();
    }

    /**
     * Obtiene estadísticas de procesos
     * 
     * @return Array con estadísticas [total, nuevos, listos, ejecutando,
     *         bloqueados, terminados]
     */
    public int[] getEstadisticasProcesos() {
        int[] estadisticas = new int[6];
        estadisticas[0] = procesosActivos.size(); // Total
        estadisticas[1] = contarProcesosPorEstado(EstadoProceso.NUEVO);
        estadisticas[2] = contarProcesosPorEstado(EstadoProceso.LISTO);
        estadisticas[3] = contarProcesosPorEstado(EstadoProceso.EJECUCION);
        estadisticas[4] = contarProcesosPorEstado(EstadoProceso.BLOQUEADO);
        estadisticas[5] = contarProcesosPorEstado(EstadoProceso.TERMINADO);
        return estadisticas;
    }

    /**
     * Cambia el estado de un proceso
     * 
     * @param proceso     Proceso a modificar
     * @param nuevoEstado Nuevo estado
     * @return true si se cambió correctamente
     */
    public boolean cambiarEstadoProceso(Proceso proceso, EstadoProceso nuevoEstado) {
        if (proceso == null || nuevoEstado == null) {
            return false;
        }

        EstadoProceso estadoAnterior = proceso.getEstado();
        proceso.setEstado(nuevoEstado);

        // Actualizar tiempos según el estado
        switch (nuevoEstado) {
            case EJECUCION:
                proceso.iniciarEjecucion();
                break;
            case TERMINADO:
                proceso.finalizarEjecucion();
                break;
        }

        System.out.println("Estado cambiado: " + proceso.getNombre() +
                " [" + estadoAnterior + " -> " + nuevoEstado + "]");
        return true;
    }

    /**
     * Elimina todos los procesos terminados
     * 
     * @return Número de procesos eliminados
     */
    public int limpiarProcesosTerminados() {
        List<Proceso> procesosTerminados = getProcesosPorEstado(EstadoProceso.TERMINADO);
        int eliminados = 0;

        for (Proceso proceso : procesosTerminados) {
            if (eliminarProceso(proceso)) {
                eliminados++;
            }
        }

        System.out.println("Procesos terminados eliminados: " + eliminados);
        return eliminados;
    }

    /**
     * Obtiene el límite máximo de procesos
     * 
     * @return Límite máximo de procesos
     */
    public int getMaxProcesos() {
        return maxProcesos;
    }

    /**
     * Establece el límite máximo de procesos
     * 
     * @param maxProcesos Nuevo límite
     */
    public void setMaxProcesos(int maxProcesos) {
        if (maxProcesos > 0) {
            this.maxProcesos = maxProcesos;
        }
    }

    /**
     * Verifica si se puede crear un nuevo proceso
     * 
     * @return true si se puede crear un nuevo proceso
     */
    public boolean puedeCrearProceso() {
        return procesosActivos.size() < maxProcesos;
    }

    /**
     * Obtiene el siguiente ID disponible
     * 
     * @return Siguiente ID disponible
     */
    public int getSiguienteId() {
        return contadorId.get();
    }

    /**
     * Reinicia el contador de IDs
     */
    public void reiniciarContadorId() {
        contadorId.set(1);
    }

    /**
     * Limpia todos los procesos activos
     */
    public void limpiarTodosLosProcesos() {
        procesosActivos.clear();
        reiniciarContadorId();
        System.out.println("Todos los procesos han sido eliminados");
    }
}

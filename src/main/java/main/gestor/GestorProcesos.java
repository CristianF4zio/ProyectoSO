package main.gestor;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import main.modelo.TipoProceso;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;

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

    public Proceso crearProceso(String nombre) {
        return crearProceso(nombre, 10, TipoProceso.CPU_BOUND, 5);
    }

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

    public boolean eliminarProceso(int id) {
        Proceso proceso = buscarProcesoPorId(id);
        return eliminarProceso(proceso);
    }

    public Proceso buscarProcesoPorId(int id) {
        return procesosActivos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Proceso> buscarProcesosPorNombre(String nombre) {
        if (nombre == null) {
            return new ArrayList<>();
        }

        return procesosActivos.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Proceso> getProcesosPorEstado(EstadoProceso estado) {
        if (estado == null) {
            return new ArrayList<>();
        }

        return procesosActivos.stream()
                .filter(p -> p.getEstado() == estado)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Proceso> getProcesosPorTipo(TipoProceso tipo) {
        if (tipo == null) {
            return new ArrayList<>();
        }

        return procesosActivos.stream()
                .filter(p -> p.getTipo() == tipo)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Proceso> getProcesosActivos() {
        return new ArrayList<>(procesosActivos);
    }

    public int getNumeroProcesosActivos() {
        return procesosActivos.size();
    }

    public int contarProcesosPorEstado(EstadoProceso estado) {
        return (int) procesosActivos.stream()
                .filter(p -> p.getEstado() == estado)
                .count();
    }

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

    public int getMaxProcesos() {
        return maxProcesos;
    }

    public void setMaxProcesos(int maxProcesos) {
        if (maxProcesos > 0) {
            this.maxProcesos = maxProcesos;
        }
    }

    public boolean puedeCrearProceso() {
        return procesosActivos.size() < maxProcesos;
    }

    public int getSiguienteId() {
        return contadorId.get();
    }

    public void reiniciarContadorId() {
        contadorId.set(1);
    }

    public void limpiarTodosLosProcesos() {
        procesosActivos.clear();
        reiniciarContadorId();
        System.out.println("Todos los procesos han sido eliminados");
    }
}

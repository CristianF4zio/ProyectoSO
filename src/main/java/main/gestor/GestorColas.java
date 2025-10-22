package main.gestor;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GestorColas {

    private GestorMemoria gestorMemoria;

    // Colas de procesos
    private List<Proceso> colaListos;
    private List<Proceso> colaBloqueados;
    private List<Proceso> colaSuspendidos;
    private List<Proceso> colaTerminados;

    // Colas suspendidas específicas
    private List<Proceso> colaListosSuspendidos;
    private List<Proceso> colaBloqueadosSuspendidos;

    public GestorColas(GestorMemoria gestorMemoria) {
        this.gestorMemoria = gestorMemoria;

        this.colaListos = new CopyOnWriteArrayList<>();
        this.colaBloqueados = new CopyOnWriteArrayList<>();
        this.colaSuspendidos = new CopyOnWriteArrayList<>();
        this.colaTerminados = new CopyOnWriteArrayList<>();

        this.colaListosSuspendidos = new CopyOnWriteArrayList<>();
        this.colaBloqueadosSuspendidos = new CopyOnWriteArrayList<>();
    }

    public boolean agregarAListos(Proceso proceso) {
        if (proceso.getEstado() == EstadoProceso.NUEVO) {
            // Intentar asignar memoria
            if (gestorMemoria.asignarMemoria(proceso)) {
                if (gestorMemoria.estaEnMemoriaPrincipal(proceso)) {
                    proceso.setEstado(EstadoProceso.LISTO);
                    colaListos.add(proceso);
                    System.out.println("Proceso agregado a cola de listos: " + proceso.getNombre());
                    return true;
                } else {
                    // Proceso suspendido por falta de memoria
                    proceso.setEstado(EstadoProceso.SUSPENDIDO);
                    colaSuspendidos.add(proceso);
                    colaListosSuspendidos.add(proceso);
                    System.out.println("Proceso suspendido por falta de memoria: " + proceso.getNombre());
                    return false;
                }
            }
        } else if (proceso.getEstado() == EstadoProceso.SUSPENDIDO) {
            // Intentar reactivar proceso
            if (gestorMemoria.reactivarProceso(proceso)) {
                proceso.setEstado(EstadoProceso.LISTO);
                colaSuspendidos.remove(proceso);
                colaListosSuspendidos.remove(proceso);
                colaListos.add(proceso);
                System.out.println("Proceso reactivado y agregado a cola de listos: " + proceso.getNombre());
                return true;
            }
        }

        return false;
    }

    public boolean agregarABloqueados(Proceso proceso) {
        if (proceso.getEstado() == EstadoProceso.EJECUCION) {
            proceso.setEstado(EstadoProceso.BLOQUEADO);
            colaBloqueados.add(proceso);
            System.out.println("Proceso agregado a cola de bloqueados: " + proceso.getNombre());
            return true;
        }
        return false;
    }

    public boolean agregarATerminados(Proceso proceso) {
        if (proceso.getEstado() == EstadoProceso.EJECUCION) {
            proceso.setEstado(EstadoProceso.TERMINADO);
            colaTerminados.add(proceso);

            // Liberar memoria
            gestorMemoria.liberarMemoria(proceso);

            System.out.println("Proceso terminado y agregado a cola de terminados: " + proceso.getNombre());
            return true;
        }
        return false;
    }

    public boolean suspenderProceso(Proceso proceso) {
        if (proceso.getEstado() == EstadoProceso.LISTO) {
            if (gestorMemoria.suspenderProceso(proceso)) {
                colaListos.remove(proceso);
                colaSuspendidos.add(proceso);
                colaListosSuspendidos.add(proceso);
                System.out.println("Proceso suspendido por falta de memoria: " + proceso.getNombre());
                return true;
            }
        } else if (proceso.getEstado() == EstadoProceso.BLOQUEADO) {
            if (gestorMemoria.suspenderProceso(proceso)) {
                colaBloqueados.remove(proceso);
                colaSuspendidos.add(proceso);
                colaBloqueadosSuspendidos.add(proceso);
                System.out.println("Proceso bloqueado suspendido por falta de memoria: " + proceso.getNombre());
                return true;
            }
        }
        return false;
    }

    public boolean reactivarProceso(Proceso proceso) {
        if (proceso.getEstado() == EstadoProceso.SUSPENDIDO) {
            if (gestorMemoria.reactivarProceso(proceso)) {
                colaSuspendidos.remove(proceso);

                // Determinar a qué cola regresar
                if (colaListosSuspendidos.contains(proceso)) {
                    colaListosSuspendidos.remove(proceso);
                    colaListos.add(proceso);
                    proceso.setEstado(EstadoProceso.LISTO);
                } else if (colaBloqueadosSuspendidos.contains(proceso)) {
                    colaBloqueadosSuspendidos.remove(proceso);
                    colaBloqueados.add(proceso);
                    proceso.setEstado(EstadoProceso.BLOQUEADO);
                }

                System.out.println("Proceso reactivado: " + proceso.getNombre());
                return true;
            }
        }
        return false;
    }

    public boolean removerDeListos(Proceso proceso) {
        if (colaListos.remove(proceso)) {
            System.out.println("Proceso removido de cola de listos: " + proceso.getNombre());
            return true;
        }
        return false;
    }

    public boolean removerDeBloqueados(Proceso proceso) {
        if (colaBloqueados.remove(proceso)) {
            System.out.println("Proceso removido de cola de bloqueados: " + proceso.getNombre());
            return true;
        }
        return false;
    }

    public void gestionarMemoria() {
        // Verificar si hay procesos que necesitan ser suspendidos
        List<Proceso> candidatosSuspension = new ArrayList<>();

        // Priorizar procesos de menor prioridad para suspensión
        candidatosSuspension.addAll(colaListos);
        candidatosSuspension.addAll(colaBloqueados);

        // Ordenar por prioridad (mayor número = menor prioridad)
        candidatosSuspension.sort((p1, p2) -> p2.getPrioridad() - p1.getPrioridad());

        // Suspender procesos si es necesario
        for (Proceso proceso : candidatosSuspension) {
            if (gestorMemoria.getMemoriaDisponible() < 10) { // Umbral de 10 KB
                suspenderProceso(proceso);
            }
        }
    }

    public void intentarReactivarProcesos() {
        List<Proceso> candidatosReactivacion = new ArrayList<>(colaSuspendidos);

        // Ordenar por prioridad (menor número = mayor prioridad)
        candidatosReactivacion.sort((p1, p2) -> p1.getPrioridad() - p2.getPrioridad());

        for (Proceso proceso : candidatosReactivacion) {
            if (gestorMemoria.getMemoriaDisponible() > 20) { // Umbral de 20 KB
                reactivarProceso(proceso);
            }
        }
    }

    // Getters para las colas
    public List<Proceso> getColaListos() {
        return new ArrayList<>(colaListos);
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

    public List<Proceso> getColaListosSuspendidos() {
        return new ArrayList<>(colaListosSuspendidos);
    }

    public List<Proceso> getColaBloqueadosSuspendidos() {
        return new ArrayList<>(colaBloqueadosSuspendidos);
    }

    public int[] obtenerEstadisticas() {
        return new int[] {
                colaListos.size(),
                colaBloqueados.size(),
                colaSuspendidos.size(),
                colaTerminados.size(),
                colaListosSuspendidos.size(),
                colaBloqueadosSuspendidos.size()
        };
    }

    public String obtenerInformacionDetallada() {
        StringBuilder info = new StringBuilder();
        info.append("=== ESTADO DE COLAS ===\n");
        info.append("Cola Listos: ").append(colaListos.size()).append(" procesos\n");
        info.append("Cola Bloqueados: ").append(colaBloqueados.size()).append(" procesos\n");
        info.append("Cola Suspendidos: ").append(colaSuspendidos.size()).append(" procesos\n");
        info.append("Cola Terminados: ").append(colaTerminados.size()).append(" procesos\n");
        info.append("Listos Suspendidos: ").append(colaListosSuspendidos.size()).append(" procesos\n");
        info.append("Bloqueados Suspendidos: ").append(colaBloqueadosSuspendidos.size()).append(" procesos\n");
        return info.toString();
    }

    public void limpiarProcesosTerminados() {
        colaTerminados.clear();
        System.out.println("Cola de procesos terminados limpiada");
    }

    public boolean hayProcesosActivos() {
        return !colaListos.isEmpty() || !colaBloqueados.isEmpty() || !colaSuspendidos.isEmpty();
    }
}


package main.gestor;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import main.planificacion.AlgoritmoPlanificacion;
import main.planificacion.*;
import java.util.*;

public class Planificador {

    private GestorMemoria gestorMemoria;
    private GestorColas gestorColas;
    private AlgoritmoPlanificacion algoritmoActual;
    private Proceso procesoEnEjecucion;

    // Algoritmos disponibles
    private Map<String, AlgoritmoPlanificacion> algoritmos;

    public Planificador(GestorMemoria gestorMemoria, GestorColas gestorColas) {
        this.gestorMemoria = gestorMemoria;
        this.gestorColas = gestorColas;
        this.procesoEnEjecucion = null;

        inicializarAlgoritmos();
        this.algoritmoActual = algoritmos.get("FCFS");
    }

    private void inicializarAlgoritmos() {
        algoritmos = new HashMap<>();
        algoritmos.put("FCFS", new FCFS());
        algoritmos.put("SJF", new SJF());
        algoritmos.put("SRTF", new SRTF());
        algoritmos.put("PRIORIDAD", new Prioridad());
        algoritmos.put("ROUND_ROBIN", new RoundRobin(4));
        algoritmos.put("MULTINIVEL", new Multinivel(3));
        algoritmos.put("MULTINIVEL_FEEDBACK", new MultinivelFeedback(3));
    }

    public boolean configurarAlgoritmo(String nombreAlgoritmo) {
        AlgoritmoPlanificacion nuevo = algoritmos.get(nombreAlgoritmo.toUpperCase());
        if (nuevo != null) {
            algoritmoActual = nuevo;
            System.out.println("Planificador configurado: " + algoritmoActual.getNombre());
            return true;
        }
        return false;
    }

    public Proceso seleccionarSiguiente() {
        List<Proceso> colaListos = gestorColas.getColaListos();

        if (colaListos.isEmpty()) {
            // Intentar reactivar procesos suspendidos
            gestorColas.intentarReactivarProcesos();
            colaListos = gestorColas.getColaListos();
        }

        if (colaListos.isEmpty()) {
            return null;
        }

        // Reordenar cola según el algoritmo
        algoritmoActual.reordenarCola(colaListos);

        // Seleccionar siguiente proceso
        Proceso siguiente = algoritmoActual.seleccionarSiguiente(colaListos);

        // Verificar que el proceso esté en memoria principal
        if (siguiente != null && !gestorMemoria.estaEnMemoriaPrincipal(siguiente)) {
            // Intentar reactivar el proceso
            if (gestorColas.reactivarProceso(siguiente)) {
                System.out.println("Proceso reactivado para ejecución: " + siguiente.getNombre());
            } else {
                System.out.println("No se puede reactivar proceso: " + siguiente.getNombre());
                return null;
            }
        }

        return siguiente;
    }

    public boolean ejecutarProceso(Proceso proceso) {
        if (proceso != null && proceso.getEstado() == EstadoProceso.LISTO) {
            // Verificar que esté en memoria principal
            if (!gestorMemoria.estaEnMemoriaPrincipal(proceso)) {
                System.out.println("Proceso no está en memoria principal: " + proceso.getNombre());
                return false;
            }

            // Remover de cola de listos
            gestorColas.removerDeListos(proceso);

            // Cambiar estado a ejecución
            proceso.setEstado(EstadoProceso.EJECUCION);
            procesoEnEjecucion = proceso;

            System.out.println("Proceso en ejecución: " + proceso.getNombre() +
                    " (Dirección: " + gestorMemoria.obtenerDireccionMemoria(proceso) + ")");
            return true;
        }
        return false;
    }

    public Proceso finalizarEjecucion() {
        if (procesoEnEjecucion != null) {
            Proceso terminado = procesoEnEjecucion;

            // Agregar a cola de terminados
            gestorColas.agregarATerminados(terminado);

            // Limpiar proceso en ejecución
            procesoEnEjecucion = null;

            System.out.println("Proceso terminado: " + terminado.getNombre());
            return terminado;
        }
        return null;
    }

    public Proceso bloquearProceso() {
        if (procesoEnEjecucion != null) {
            Proceso bloqueado = procesoEnEjecucion;

            // Agregar a cola de bloqueados
            gestorColas.agregarABloqueados(bloqueado);

            // Limpiar proceso en ejecución
            procesoEnEjecucion = null;

            System.out.println("Proceso bloqueado por I/O: " + bloqueado.getNombre());
            return bloqueado;
        }
        return null;
    }

    public boolean desbloquearProceso(Proceso proceso) {
        if (proceso.getEstado() == EstadoProceso.BLOQUEADO) {
            // Remover de cola de bloqueados
            gestorColas.removerDeBloqueados(proceso);

            // Agregar a cola de listos
            gestorColas.agregarAListos(proceso);

            System.out.println("Proceso desbloqueado: " + proceso.getNombre());
            return true;
        }
        return false;
    }

    public void gestionarMemoria() {
        // Gestionar memoria automáticamente
        gestorColas.gestionarMemoria();

        // Verificar si el proceso en ejecución necesita ser suspendido
        if (procesoEnEjecucion != null && gestorMemoria.getMemoriaDisponible() < 5) {
            System.out.println("Memoria crítica, suspendiendo proceso en ejecución: " + procesoEnEjecucion.getNombre());
            gestorColas.suspenderProceso(procesoEnEjecucion);
            procesoEnEjecucion = null;
        }
    }

    public Proceso getProcesoEnEjecucion() {
        return procesoEnEjecucion;
    }

    public boolean hayProcesoEnEjecucion() {
        return procesoEnEjecucion != null;
    }

    public AlgoritmoPlanificacion getAlgoritmoActual() {
        return algoritmoActual;
    }

    public String getNombreAlgoritmo() {
        return algoritmoActual != null ? algoritmoActual.getNombre() : "Ninguno";
    }

    public String obtenerEstadoPlanificador() {
        StringBuilder estado = new StringBuilder();
        estado.append("=== ESTADO DEL PLANIFICADOR ===\n");
        estado.append("Algoritmo: ").append(getNombreAlgoritmo()).append("\n");
        estado.append("Proceso en ejecución: ");

        if (procesoEnEjecucion != null) {
            estado.append(procesoEnEjecucion.getNombre())
                    .append(" (ID: ").append(procesoEnEjecucion.getId())
                    .append(", Dirección: ").append(gestorMemoria.obtenerDireccionMemoria(procesoEnEjecucion))
                    .append(")");
        } else {
            estado.append("Ninguno");
        }

        estado.append("\n");
        return estado.toString();
    }

    public int[] obtenerEstadisticas() {
        return new int[] {
                gestorColas.getColaListos().size(),
                gestorColas.getColaBloqueados().size(),
                gestorColas.getColaSuspendidos().size(),
                gestorColas.getColaTerminados().size(),
                hayProcesoEnEjecucion() ? 1 : 0
        };
    }
}


package main.gestor;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import main.estructuras.ListaSimple;
import main.estructuras.ColaSimple;
import main.estructuras.Ordenador;

public class GestorColas {

    private GestorMemoria gestorMemoria;

    // Colas de procesos
    private ListaSimple<Proceso> colaListos;
    private ListaSimple<Proceso> colaBloqueados;
    private ListaSimple<Proceso> colaSuspendidos;
    private ListaSimple<Proceso> colaTerminados;

    // Colas suspendidas específicas
    private ListaSimple<Proceso> colaListosSuspendidos;
    private ListaSimple<Proceso> colaBloqueadosSuspendidos;

    public GestorColas(GestorMemoria gestorMemoria) {
        this.gestorMemoria = gestorMemoria;

        this.colaListos = new ListaSimple<>();
        this.colaBloqueados = new ListaSimple<>();
        this.colaSuspendidos = new ListaSimple<>();
        this.colaTerminados = new ListaSimple<>();

        this.colaListosSuspendidos = new ListaSimple<>();
        this.colaBloqueadosSuspendidos = new ListaSimple<>();
    }

    public boolean agregarAListos(Proceso proceso) {
        if (proceso.getEstado() == EstadoProceso.NUEVO) {
            // Intentar asignar memoria
            if (gestorMemoria.asignarMemoria(proceso)) {
                if (gestorMemoria.estaEnMemoriaPrincipal(proceso)) {
                    proceso.setEstado(EstadoProceso.LISTO);
                    colaListos.agregar(proceso);
                    System.out.println("Proceso agregado a cola de listos: " + proceso.getNombre());
                    return true;
                } else {
                    // Proceso suspendido por falta de memoria
                    proceso.setEstado(EstadoProceso.SUSPENDIDO);
                    colaSuspendidos.agregar(proceso);
                    colaListosSuspendidos.agregar(proceso);
                    System.out.println("Proceso suspendido por falta de memoria: " + proceso.getNombre());
                    return false;
                }
            }
        } else if (proceso.getEstado() == EstadoProceso.SUSPENDIDO) {
            // Intentar reactivar proceso
            if (gestorMemoria.reactivarProceso(proceso)) {
                proceso.setEstado(EstadoProceso.LISTO);
                colaSuspendidos.remover(proceso);
                colaListosSuspendidos.remover(proceso);
                colaListos.agregar(proceso);
                System.out.println("Proceso reactivado y agregado a cola de listos: " + proceso.getNombre());
                return true;
            }
        }

        return false;
    }

    public boolean agregarABloqueados(Proceso proceso) {
        if (proceso.getEstado() == EstadoProceso.EJECUCION) {
            proceso.setEstado(EstadoProceso.BLOQUEADO);
            colaBloqueados.agregar(proceso);
            System.out.println("Proceso agregado a cola de bloqueados: " + proceso.getNombre());
            return true;
        }
        return false;
    }

    public boolean agregarATerminados(Proceso proceso) {
        if (proceso.getEstado() == EstadoProceso.EJECUCION) {
            proceso.setEstado(EstadoProceso.TERMINADO);
            colaTerminados.agregar(proceso);

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
                colaListos.remover(proceso);
                colaSuspendidos.add(proceso);
                colaListosSuspendidos.add(proceso);
                System.out.println("Proceso suspendido por falta de memoria: " + proceso.getNombre());
                return true;
            }
        } else if (proceso.getEstado() == EstadoProceso.BLOQUEADO) {
            if (gestorMemoria.suspenderProceso(proceso)) {
                colaBloqueados.remover(proceso);
                colaSuspendidos.add(proceso);
                colaBloqueadosSuspendidos.agregar(proceso);
                System.out.println("Proceso bloqueado suspendido por falta de memoria: " + proceso.getNombre());
                return true;
            }
        }
        return false;
    }

    public boolean reactivarProceso(Proceso proceso) {
        if (proceso.getEstado() == EstadoProceso.SUSPENDIDO) {
            if (gestorMemoria.reactivarProceso(proceso)) {
                colaSuspendidos.remover(proceso);

                // Determinar a qué cola regresar
                if (colaListosSuspendidos.contains(proceso)) {
                    colaListosSuspendidos.remover(proceso);
                    colaListos.agregar(proceso);
                    proceso.setEstado(EstadoProceso.LISTO);
                } else if (colaBloqueadosSuspendidos.contains(proceso)) {
                    colaBloqueadosSuspendidos.remover(proceso);
                    colaBloqueados.agregar(proceso);
                    proceso.setEstado(EstadoProceso.BLOQUEADO);
                }

                System.out.println("Proceso reactivado: " + proceso.getNombre());
                return true;
            }
        }
        return false;
    }

    public boolean removerDeListos(Proceso proceso) {
        if (colaListos.remover(proceso)) {
            System.out.println("Proceso removido de cola de listos: " + proceso.getNombre());
            return true;
        }
        return false;
    }

    public boolean removerDeBloqueados(Proceso proceso) {
        if (colaBloqueados.remover(proceso)) {
            System.out.println("Proceso removido de cola de bloqueados: " + proceso.getNombre());
            return true;
        }
        return false;
    }

    public void gestionarMemoria() {
        // Verificar si hay procesos que necesitan ser suspendidos
        ListaSimple<Proceso> candidatosSuspension = new ListaSimple<>();

        // Priorizar procesos de menor prioridad para suspensión
        for (int i = 0; i < colaListos.tamaño(); i++) {
            candidatosSuspension.agregar(colaListos.obtener(i));
        }
        for (int i = 0; i < colaBloqueados.tamaño(); i++) {
            candidatosSuspension.agregar(colaBloqueados.obtener(i));
        }

        // Ordenar por prioridad (mayor número = menor prioridad)
        Ordenador.ordenarPorPrioridad(candidatosSuspension);

        // Suspender procesos si es necesario
        for (int i = 0; i < candidatosSuspension.tamaño(); i++) {
            Proceso proceso = candidatosSuspension.obtener(i);
            if (gestorMemoria.getMemoriaDisponible() < 10) { // Umbral de 10 KB
                suspenderProceso(proceso);
            }
        }
    }

    public void intentarReactivarProcesos() {
        ListaSimple<Proceso> candidatosReactivacion = new ListaSimple<>();
        for (int i = 0; i < colaSuspendidos.tamaño(); i++) {
            candidatosReactivacion.agregar(colaSuspendidos.obtener(i));
        }

        // Ordenar por prioridad (menor número = mayor prioridad)
        Ordenador.ordenarPorPrioridad(candidatosReactivacion);

        for (int i = 0; i < candidatosReactivacion.tamaño(); i++) {
            Proceso proceso = candidatosReactivacion.obtener(i);
            if (gestorMemoria.getMemoriaDisponible() > 20) { // Umbral de 20 KB
                reactivarProceso(proceso);
            }
        }
    }

    // Getters para las colas
    public ListaSimple<Proceso> getColaListos() {
        ListaSimple<Proceso> resultado = new ListaSimple<>();
        for (int i = 0; i < colaListos.tamaño(); i++) {
            resultado.agregar(colaListos.obtener(i));
        }
        return resultado;
    }

    public ListaSimple<Proceso> getColaBloqueados() {
        ListaSimple<Proceso> resultado = new ListaSimple<>();
        for (int i = 0; i < colaBloqueados.tamaño(); i++) {
            resultado.agregar(colaBloqueados.obtener(i));
        }
        return resultado;
    }

    public ListaSimple<Proceso> getColaSuspendidos() {
        ListaSimple<Proceso> resultado = new ListaSimple<>();
        for (int i = 0; i < colaSuspendidos.tamaño(); i++) {
            resultado.agregar(colaSuspendidos.obtener(i));
        }
        return resultado;
    }

    public ListaSimple<Proceso> getColaTerminados() {
        ListaSimple<Proceso> resultado = new ListaSimple<>();
        for (int i = 0; i < colaTerminados.tamaño(); i++) {
            resultado.agregar(colaTerminados.obtener(i));
        }
        return resultado;
    }

    public ListaSimple<Proceso> getColaListosSuspendidos() {
        ListaSimple<Proceso> resultado = new ListaSimple<>();
        for (int i = 0; i < colaListosSuspendidos.tamaño(); i++) {
            resultado.agregar(colaListosSuspendidos.obtener(i));
        }
        return resultado;
    }

    public ListaSimple<Proceso> getColaBloqueadosSuspendidos() {
        ListaSimple<Proceso> resultado = new ListaSimple<>();
        for (int i = 0; i < colaBloqueadosSuspendidos.tamaño(); i++) {
            resultado.agregar(colaBloqueadosSuspendidos.obtener(i));
        }
        return resultado;
    }

    public int[] obtenerEstadisticas() {
        return new int[] {
                colaListos.tamaño(),
                colaBloqueados.tamaño(),
                colaSuspendidos.tamaño(),
                colaTerminados.tamaño(),
                colaListosSuspendidos.tamaño(),
                colaBloqueadosSuspendidos.tamaño()
        };
    }

    public String obtenerInformacionDetallada() {
        StringBuilder info = new StringBuilder();
        info.append("=== ESTADO DE COLAS ===\n");
        info.append("Cola Listos: ").append(colaListos.tamaño()).append(" procesos\n");
        info.append("Cola Bloqueados: ").append(colaBloqueados.tamaño()).append(" procesos\n");
        info.append("Cola Suspendidos: ").append(colaSuspendidos.tamaño()).append(" procesos\n");
        info.append("Cola Terminados: ").append(colaTerminados.tamaño()).append(" procesos\n");
        info.append("Listos Suspendidos: ").append(colaListosSuspendidos.tamaño()).append(" procesos\n");
        info.append("Bloqueados Suspendidos: ").append(colaBloqueadosSuspendidos.tamaño()).append(" procesos\n");
        return info.toString();
    }

    public void limpiarProcesosTerminados() {
        colaTerminados.limpiar();
        System.out.println("Cola de procesos terminados limpiada");
    }

    public boolean hayProcesosActivos() {
        return !colaListos.estaVacia() || !colaBloqueados.estaVacia() || !colaSuspendidos.estaVacia();
    }
}

package main.gestor;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import main.estructuras.ListaSimple;
import main.estructuras.MapaSimple;
import main.estructuras.Ordenador;

public class GestorMemoria {

    // Configuración de memoria
    private int tamanioMemoriaPrincipal;
    private int tamanioMemoriaSecundaria;
    private int memoriaDisponible;

    // Mapas de memoria
    private MapaSimple<Integer, Proceso> memoriaPrincipal; // ID -> Proceso
    private MapaSimple<Integer, Proceso> memoriaSecundaria; // ID -> Proceso
    private MapaSimple<Integer, Integer> direccionesMemoria; // ID -> Dirección en memoria

    // Estadísticas
    private int procesosEnMemoriaPrincipal;
    private int procesosEnMemoriaSecundaria;
    private int totalAsignaciones;
    private int totalLiberaciones;
    private int totalSuspensiones;

    // Política de reemplazo
    private PoliticaReemplazo politicaReemplazo;

    public GestorMemoria(int tamanioMemoriaPrincipal, int tamanioMemoriaSecundaria) {
        this.tamanioMemoriaPrincipal = tamanioMemoriaPrincipal;
        this.tamanioMemoriaSecundaria = tamanioMemoriaSecundaria;
        this.memoriaDisponible = tamanioMemoriaPrincipal;

        this.memoriaPrincipal = new MapaSimple<>();
        this.memoriaSecundaria = new MapaSimple<>();
        this.direccionesMemoria = new MapaSimple<>();

        this.procesosEnMemoriaPrincipal = 0;
        this.procesosEnMemoriaSecundaria = 0;
        this.totalAsignaciones = 0;
        this.totalLiberaciones = 0;
        this.totalSuspensiones = 0;

        this.politicaReemplazo = PoliticaReemplazo.LRU; // Least Recently Used por defecto
    }

    public boolean asignarMemoria(Proceso proceso) {
        int tamanioProceso = calcularTamanioProceso(proceso);

        // Verificar si hay espacio en memoria principal
        if (memoriaDisponible >= tamanioProceso) {
            return asignarMemoriaPrincipal(proceso, tamanioProceso);
        } else {
            // Intentar hacer espacio liberando procesos
            if (liberarMemoria(tamanioProceso)) {
                return asignarMemoriaPrincipal(proceso, tamanioProceso);
            } else {
                // Asignar en memoria secundaria
                return asignarMemoriaSecundaria(proceso, tamanioProceso);
            }
        }
    }

    private boolean asignarMemoriaPrincipal(Proceso proceso, int tamanio) {
        memoriaPrincipal.poner(proceso.getId(), proceso);
        direccionesMemoria.poner(proceso.getId(), memoriaDisponible - tamanio);
        memoriaDisponible -= tamanio;
        procesosEnMemoriaPrincipal++;
        totalAsignaciones++;

        System.out.println("Memoria principal asignada a " + proceso.getNombre() +
                " (ID: " + proceso.getId() + ", Tamaño: " + tamanio + " KB)");
        return true;
    }

    private boolean asignarMemoriaSecundaria(Proceso proceso, int tamanio) {
        memoriaSecundaria.poner(proceso.getId(), proceso);
        direccionesMemoria.poner(proceso.getId(), -1); // -1 indica memoria secundaria
        procesosEnMemoriaSecundaria++;
        totalAsignaciones++;

        // Cambiar estado a suspendido
        proceso.setEstado(EstadoProceso.SUSPENDIDO);
        totalSuspensiones++;

        System.out.println("Memoria secundaria asignada a " + proceso.getNombre() +
                " (ID: " + proceso.getId() + ", Tamaño: " + tamanio + " KB) - SUSPENDIDO");
        return true;
    }

    public boolean liberarMemoria(Proceso proceso) {
        int id = proceso.getId();

        if (memoriaPrincipal.contieneClave(id)) {
            return liberarMemoriaPrincipal(proceso);
        } else if (memoriaSecundaria.contieneClave(id)) {
            return liberarMemoriaSecundaria(proceso);
        }

        return false;
    }

    private boolean liberarMemoriaPrincipal(Proceso proceso) {
        int id = proceso.getId();
        int tamanio = calcularTamanioProceso(proceso);

        memoriaPrincipal.remover(id);
        direccionesMemoria.remover(id);
        memoriaDisponible += tamanio;
        procesosEnMemoriaPrincipal--;
        totalLiberaciones++;

        System.out.println("Memoria principal liberada de " + proceso.getNombre() +
                " (ID: " + id + ", Tamaño: " + tamanio + " KB)");
        return true;
    }

    private boolean liberarMemoriaSecundaria(Proceso proceso) {
        int id = proceso.getId();
        int tamanio = calcularTamanioProceso(proceso);

        memoriaSecundaria.remover(id);
        direccionesMemoria.remover(id);
        procesosEnMemoriaSecundaria--;
        totalLiberaciones++;

        System.out.println("Memoria secundaria liberada de " + proceso.getNombre() +
                " (ID: " + id + ", Tamaño: " + tamanio + " KB)");
        return true;
    }

    private boolean liberarMemoria(int tamanioNecesario) {
        ListaSimple<Proceso> candidatos = obtenerCandidatosReemplazo();

        for (int i = 0; i < candidatos.tamaño(); i++) {
            Proceso proceso = candidatos.obtener(i);
            if (memoriaDisponible >= tamanioNecesario) {
                break;
            }

            // Suspender proceso
            suspenderProceso(proceso);
        }

        return memoriaDisponible >= tamanioNecesario;
    }

    public boolean suspenderProceso(Proceso proceso) {
        int id = proceso.getId();

        if (memoriaPrincipal.contieneClave(id)) {
            Proceso procesoSuspendido = memoriaPrincipal.remover(id);
            memoriaSecundaria.poner(id, procesoSuspendido);

            int tamanio = calcularTamanioProceso(proceso);
            memoriaDisponible += tamanio;
            direccionesMemoria.poner(id, -1);

            proceso.setEstado(EstadoProceso.SUSPENDIDO);
            procesosEnMemoriaPrincipal--;
            procesosEnMemoriaSecundaria++;
            totalSuspensiones++;

            System.out.println("Proceso suspendido: " + proceso.getNombre() +
                    " (ID: " + id + ") - Movido a memoria secundaria");
            return true;
        }

        return false;
    }

    public boolean reactivarProceso(Proceso proceso) {
        int id = proceso.getId();
        int tamanio = calcularTamanioProceso(proceso);

        if (memoriaSecundaria.contieneClave(id) && memoriaDisponible >= tamanio) {
            Proceso procesoReactivado = memoriaSecundaria.remover(id);
            memoriaPrincipal.poner(id, procesoReactivado);

            memoriaDisponible -= tamanio;
            direccionesMemoria.poner(id, memoriaDisponible + tamanio);

            proceso.setEstado(EstadoProceso.LISTO);
            procesosEnMemoriaSecundaria--;
            procesosEnMemoriaPrincipal++;

            System.out.println("Proceso reactivado: " + proceso.getNombre() +
                    " (ID: " + id + ") - Movido a memoria principal");
            return true;
        }

        return false;
    }

    private ListaSimple<Proceso> obtenerCandidatosReemplazo() {
        ListaSimple<Proceso> candidatos = new ListaSimple<>();
        for (int i = 0; i < memoriaPrincipal.tamaño(); i++) {
            candidatos.agregar(memoriaPrincipal.obtener(i));
        }

        switch (politicaReemplazo) {
            case LRU:
                // Least Recently Used - ordenar por tiempo de acceso
                Ordenador.ordenarPorTiempoLlegada(candidatos);
                break;
            case FIFO:
                // First In First Out - ordenar por tiempo de creación
                Ordenador.ordenarPorTiempoLlegada(candidatos);
                break;
            case PRIORIDAD:
                // Por prioridad - ordenar por prioridad (menor prioridad = mayor número)
                Ordenador.ordenarPorPrioridad(candidatos);
                break;
        }

        return candidatos;
    }

    private int calcularTamanioProceso(Proceso proceso) {
        // Tamaño base + tamaño por instrucción
        return 4 + (proceso.getNumInstrucciones() / 10); // 4 KB base + 1 KB por cada 10 instrucciones
    }

    public boolean estaEnMemoriaPrincipal(Proceso proceso) {
        return memoriaPrincipal.contieneClave(proceso.getId());
    }

    public boolean estaEnMemoriaSecundaria(Proceso proceso) {
        return memoriaSecundaria.contieneClave(proceso.getId());
    }

    public int obtenerDireccionMemoria(Proceso proceso) {
        Integer direccion = direccionesMemoria.obtener(proceso.getId());
        return direccion != null ? direccion : -1;
    }

    public int[] obtenerEstadisticas() {
        return new int[] {
                tamanioMemoriaPrincipal,
                tamanioMemoriaSecundaria,
                memoriaDisponible,
                procesosEnMemoriaPrincipal,
                procesosEnMemoriaSecundaria,
                totalAsignaciones,
                totalLiberaciones,
                totalSuspensiones
        };
    }

    public String obtenerInformacionDetallada() {
        StringBuilder info = new StringBuilder();
        info.append("=== ESTADO DE MEMORIA ===\n");
        info.append("Memoria Principal: ").append(tamanioMemoriaPrincipal).append(" KB\n");
        info.append("Memoria Secundaria: ").append(tamanioMemoriaSecundaria).append(" KB\n");
        info.append("Memoria Disponible: ").append(memoriaDisponible).append(" KB\n");
        info.append("Procesos en Principal: ").append(procesosEnMemoriaPrincipal).append("\n");
        info.append("Procesos en Secundaria: ").append(procesosEnMemoriaSecundaria).append("\n");
        info.append("Total Asignaciones: ").append(totalAsignaciones).append("\n");
        info.append("Total Liberaciones: ").append(totalLiberaciones).append("\n");
        info.append("Total Suspensiones: ").append(totalSuspensiones).append("\n");
        info.append("Política Reemplazo: ").append(politicaReemplazo).append("\n");
        return info.toString();
    }

    // Getters y Setters
    public int getTamanioMemoriaPrincipal() {
        return tamanioMemoriaPrincipal;
    }

    public int getTamanioMemoriaSecundaria() {
        return tamanioMemoriaSecundaria;
    }

    public int getMemoriaDisponible() {
        return memoriaDisponible;
    }

    public int getProcesosEnMemoriaPrincipal() {
        return procesosEnMemoriaPrincipal;
    }

    public int getProcesosEnMemoriaSecundaria() {
        return procesosEnMemoriaSecundaria;
    }

    public PoliticaReemplazo getPoliticaReemplazo() {
        return politicaReemplazo;
    }

    public void setPoliticaReemplazo(PoliticaReemplazo politicaReemplazo) {
        this.politicaReemplazo = politicaReemplazo;
    }

    public enum PoliticaReemplazo {
        LRU, // Least Recently Used
        FIFO, // First In First Out
        PRIORIDAD // Por prioridad
    }
}

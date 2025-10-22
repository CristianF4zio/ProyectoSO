package main.gestor;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GestorMemoria {

    // Configuración de memoria
    private int tamanioMemoriaPrincipal;
    private int tamanioMemoriaSecundaria;
    private int memoriaDisponible;

    // Mapas de memoria
    private Map<Integer, Proceso> memoriaPrincipal; // ID -> Proceso
    private Map<Integer, Proceso> memoriaSecundaria; // ID -> Proceso
    private Map<Integer, Integer> direccionesMemoria; // ID -> Dirección en memoria

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

        this.memoriaPrincipal = new ConcurrentHashMap<>();
        this.memoriaSecundaria = new ConcurrentHashMap<>();
        this.direccionesMemoria = new ConcurrentHashMap<>();

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
        memoriaPrincipal.put(proceso.getId(), proceso);
        direccionesMemoria.put(proceso.getId(), memoriaDisponible - tamanio);
        memoriaDisponible -= tamanio;
        procesosEnMemoriaPrincipal++;
        totalAsignaciones++;

        System.out.println("Memoria principal asignada a " + proceso.getNombre() +
                " (ID: " + proceso.getId() + ", Tamaño: " + tamanio + " KB)");
        return true;
    }

    private boolean asignarMemoriaSecundaria(Proceso proceso, int tamanio) {
        memoriaSecundaria.put(proceso.getId(), proceso);
        direccionesMemoria.put(proceso.getId(), -1); // -1 indica memoria secundaria
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

        if (memoriaPrincipal.containsKey(id)) {
            return liberarMemoriaPrincipal(proceso);
        } else if (memoriaSecundaria.containsKey(id)) {
            return liberarMemoriaSecundaria(proceso);
        }

        return false;
    }

    private boolean liberarMemoriaPrincipal(Proceso proceso) {
        int id = proceso.getId();
        int tamanio = calcularTamanioProceso(proceso);

        memoriaPrincipal.remove(id);
        direccionesMemoria.remove(id);
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

        memoriaSecundaria.remove(id);
        direccionesMemoria.remove(id);
        procesosEnMemoriaSecundaria--;
        totalLiberaciones++;

        System.out.println("Memoria secundaria liberada de " + proceso.getNombre() +
                " (ID: " + id + ", Tamaño: " + tamanio + " KB)");
        return true;
    }

    private boolean liberarMemoria(int tamanioNecesario) {
        List<Proceso> candidatos = obtenerCandidatosReemplazo();

        for (Proceso proceso : candidatos) {
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

        if (memoriaPrincipal.containsKey(id)) {
            Proceso procesoSuspendido = memoriaPrincipal.remove(id);
            memoriaSecundaria.put(id, procesoSuspendido);

            int tamanio = calcularTamanioProceso(proceso);
            memoriaDisponible += tamanio;
            direccionesMemoria.put(id, -1);

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

        if (memoriaSecundaria.containsKey(id) && memoriaDisponible >= tamanio) {
            Proceso procesoReactivado = memoriaSecundaria.remove(id);
            memoriaPrincipal.put(id, procesoReactivado);

            memoriaDisponible -= tamanio;
            direccionesMemoria.put(id, memoriaDisponible + tamanio);

            proceso.setEstado(EstadoProceso.LISTO);
            procesosEnMemoriaSecundaria--;
            procesosEnMemoriaPrincipal++;

            System.out.println("Proceso reactivado: " + proceso.getNombre() +
                    " (ID: " + id + ") - Movido a memoria principal");
            return true;
        }

        return false;
    }

    private List<Proceso> obtenerCandidatosReemplazo() {
        List<Proceso> candidatos = new ArrayList<>(memoriaPrincipal.values());

        switch (politicaReemplazo) {
            case LRU:
                // Least Recently Used - ordenar por tiempo de acceso
                candidatos.sort((p1, p2) -> Long.compare(p1.getTiempoCreacion().toEpochSecond(java.time.ZoneOffset.UTC),
                        p2.getTiempoCreacion().toEpochSecond(java.time.ZoneOffset.UTC)));
                break;
            case FIFO:
                // First In First Out - ordenar por tiempo de creación
                candidatos.sort((p1, p2) -> p1.getId() - p2.getId());
                break;
            case PRIORIDAD:
                // Por prioridad - ordenar por prioridad (menor prioridad = mayor número)
                candidatos.sort((p1, p2) -> p2.getPrioridad() - p1.getPrioridad());
                break;
        }

        return candidatos;
    }

    private int calcularTamanioProceso(Proceso proceso) {
        // Tamaño base + tamaño por instrucción
        return 4 + (proceso.getNumInstrucciones() / 10); // 4 KB base + 1 KB por cada 10 instrucciones
    }

    public boolean estaEnMemoriaPrincipal(Proceso proceso) {
        return memoriaPrincipal.containsKey(proceso.getId());
    }

    public boolean estaEnMemoriaSecundaria(Proceso proceso) {
        return memoriaSecundaria.containsKey(proceso.getId());
    }

    public int obtenerDireccionMemoria(Proceso proceso) {
        return direccionesMemoria.getOrDefault(proceso.getId(), -1);
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

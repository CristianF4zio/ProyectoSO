package main.utilidades;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import main.modelo.TipoProceso;
import main.gestor.GestorProcesos;
import main.estructuras.ListaSimple;
import main.estructuras.MapaSimple;
import main.estructuras.ColaSimple;

public class OptimizadorRendimiento {

    // Cache para procesos
    private final MapaSimple<Integer, Proceso> cacheProcesos;
    private final MapaSimple<String, ListaSimple<Proceso>> cacheBusquedas;

    // Pool de objetos
    private final ObjectPool<Proceso> poolProcesos;
    private final ObjectPool<StringBuilder> poolStringBuilders;

    // Contadores de rendimiento
    private int hitsCache;
    private int missesCache;
    private int objetosReutilizados;

    // Configuración de optimización
    private final int maxCacheSize;
    private final int maxPoolSize;
    private final boolean habilitarCache;
    private final boolean habilitarPool;

    public OptimizadorRendimiento() {
        this.cacheProcesos = new MapaSimple<>();
        this.cacheBusquedas = new MapaSimple<>();
        this.poolProcesos = new ObjectPool<>(() -> new Proceso(0, "", TipoProceso.CPU_BOUND, 0, 0), 100);
        this.poolStringBuilders = new ObjectPool<>(StringBuilder::new, 50);

        this.hitsCache = 0;
        this.missesCache = 0;
        this.objetosReutilizados = 0;

        this.maxCacheSize = 1000;
        this.maxPoolSize = 100;
        this.habilitarCache = true;
        this.habilitarPool = true;
    }

    public Proceso obtenerProcesoOptimizado(int id, GestorProcesos gestorProcesos) {
        if (!habilitarCache) {
            return gestorProcesos.buscarProcesoPorId(id);
        }

        // Verificar cache
        Proceso proceso = cacheProcesos.obtener(id);
        if (proceso != null) {
            hitsCache++;
            return proceso;
        }

        // Cache miss - buscar en gestor
        proceso = gestorProcesos.buscarProcesoPorId(id);
        if (proceso != null) {
            // Agregar al cache si hay espacio
            if (cacheProcesos.tamaño() < maxCacheSize) {
                cacheProcesos.poner(id, proceso);
            }
            missesCache++;
        }

        return proceso;
    }

    public ListaSimple<Proceso> buscarProcesosPorNombreOptimizado(String nombre, GestorProcesos gestorProcesos) {
        if (!habilitarCache) {
            return gestorProcesos.buscarProcesosPorNombre(nombre);
        }

        // Verificar cache
        ListaSimple<Proceso> procesos = cacheBusquedas.obtener(nombre);
        if (procesos != null) {
            hitsCache++;
            return procesos;
        }

        // Cache miss - buscar en gestor
        procesos = gestorProcesos.buscarProcesosPorNombre(nombre);
        if (procesos != null && cacheBusquedas.tamaño() < maxCacheSize) {
            cacheBusquedas.poner(nombre, procesos);
        }
        missesCache++;

        return procesos;
    }

    public ListaSimple<Proceso> buscarProcesosPorEstadoOptimizado(EstadoProceso estado, GestorProcesos gestorProcesos) {
        return gestorProcesos.getProcesosPorEstado(estado);
    }

    public Proceso crearProcesoOptimizado(String nombre, int numInstrucciones, TipoProceso tipoProceso, int prioridad,
            GestorProcesos gestorProcesos) {
        if (habilitarPool) {
            Proceso proceso = poolProcesos.obtener();
            if (proceso != null) {
                // Reutilizar objeto del pool
                proceso.setId(gestorProcesos.getSiguienteId());
                proceso.setNombre(nombre);
                proceso.setNumInstrucciones(numInstrucciones);
                proceso.setTipo(tipoProceso);
                proceso.setPrioridad(prioridad);
                objetosReutilizados++;
                return proceso;
            }
        }

        // Crear nuevo objeto
        return new Proceso(gestorProcesos.getSiguienteId(), nombre, tipoProceso, numInstrucciones, prioridad);
    }

    public void liberarProceso(Proceso proceso) {
        if (habilitarPool && proceso != null) {
            poolProcesos.liberar(proceso);
        }
    }

    public StringBuilder obtenerStringBuilder() {
        if (habilitarPool) {
            StringBuilder sb = poolStringBuilders.obtener();
            if (sb != null) {
                sb.setLength(0); // Limpiar contenido
                objetosReutilizados++;
                return sb;
            }
        }
        return new StringBuilder();
    }

    public void liberarStringBuilder(StringBuilder sb) {
        if (habilitarPool && sb != null) {
            poolStringBuilders.liberar(sb);
        }
    }

    public void limpiarCache() {
        if (cacheProcesos.tamaño() > maxCacheSize * 0.8) {
            cacheProcesos.limpiar();
            cacheBusquedas.limpiar();
        }
    }

    public String getEstadisticasRendimiento() {
        int totalHits = hitsCache + missesCache;
        double hitRate = totalHits > 0 ? (double) hitsCache / totalHits * 100 : 0;

        return String.format(
                "Optimizador Rendimiento:\n" +
                        "  Cache Hit Rate: %.2f%%\n" +
                        "  Hits: %d, Misses: %d\n" +
                        "  Objetos reutilizados: %d\n" +
                        "  Tamaño cache: %d/%d\n" +
                        "  Pool objetos: %d/%d",
                hitRate, hitsCache, missesCache,
                objetosReutilizados, cacheProcesos.tamaño(), maxCacheSize,
                poolProcesos.tamaño(), maxPoolSize);
    }

    public void reiniciarEstadisticas() {
        hitsCache = 0;
        missesCache = 0;
        objetosReutilizados = 0;
        cacheProcesos.limpiar();
        cacheBusquedas.limpiar();
    }

    private static class ObjectPool<T> {
        private final ColaSimple<T> pool;
        private final java.util.function.Supplier<T> factory;
        private final int maxSize;

        public ObjectPool(java.util.function.Supplier<T> factory, int maxSize) {
            this.pool = new ColaSimple<>();
            this.factory = factory;
            this.maxSize = maxSize;
        }

        public T obtener() {
            T obj = pool.poll();
            if (obj == null) {
                obj = factory.get();
            }
            return obj;
        }

        public void liberar(T obj) {
            if (pool.tamaño() < maxSize) {
                pool.offer(obj);
            }
        }

        public int tamaño() {
            return pool.tamaño();
        }
    }
}
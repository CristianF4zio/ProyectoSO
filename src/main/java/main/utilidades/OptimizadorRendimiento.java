package main.utilidades;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import main.modelo.TipoProceso;
import main.gestor.GestorProcesos;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OptimizadorRendimiento {

    // Cache para procesos
    private final ConcurrentHashMap<Integer, Proceso> cacheProcesos;
    private final ConcurrentHashMap<String, List<Proceso>> cacheBusquedas;

    // Pool de objetos
    private final ObjectPool<Proceso> poolProcesos;
    private final ObjectPool<StringBuilder> poolStringBuilders;

    // Contadores de rendimiento
    private final AtomicInteger hitsCache;
    private final AtomicInteger missesCache;
    private final AtomicInteger objetosReutilizados;

    // Configuración de optimización
    private final int maxCacheSize;
    private final int maxPoolSize;
    private final boolean habilitarCache;
    private final boolean habilitarPool;

    public OptimizadorRendimiento() {
        this.cacheProcesos = new ConcurrentHashMap<>();
        this.cacheBusquedas = new ConcurrentHashMap<>();
        this.poolProcesos = new ObjectPool<>(() -> new Proceso(0, "", TipoProceso.CPU_BOUND, 0, 0), 100);
        this.poolStringBuilders = new ObjectPool<>(StringBuilder::new, 50);

        this.hitsCache = new AtomicInteger(0);
        this.missesCache = new AtomicInteger(0);
        this.objetosReutilizados = new AtomicInteger(0);

        this.maxCacheSize = 1000;
        this.maxPoolSize = 100;
        this.habilitarCache = true;
        this.habilitarPool = true;
    }

    public OptimizadorRendimiento(int maxCacheSize, int maxPoolSize,
            boolean habilitarCache, boolean habilitarPool) {
        this.cacheProcesos = new ConcurrentHashMap<>();
        this.cacheBusquedas = new ConcurrentHashMap<>();
        this.poolProcesos = new ObjectPool<>(() -> new Proceso(0, "", TipoProceso.CPU_BOUND, 0, 0), maxPoolSize);
        this.poolStringBuilders = new ObjectPool<>(StringBuilder::new, maxPoolSize);

        this.hitsCache = new AtomicInteger(0);
        this.missesCache = new AtomicInteger(0);
        this.objetosReutilizados = new AtomicInteger(0);

        this.maxCacheSize = maxCacheSize;
        this.maxPoolSize = maxPoolSize;
        this.habilitarCache = habilitarCache;
        this.habilitarPool = habilitarPool;
    }

    public Proceso buscarProcesoOptimizado(int id, GestorProcesos gestorProcesos) {
        if (!habilitarCache) {
            return gestorProcesos.buscarProcesoPorId(id);
        }

        // Verificar cache
        Proceso proceso = cacheProcesos.get(id);
        if (proceso != null) {
            hitsCache.incrementAndGet();
            return proceso;
        }

        // Buscar en gestor
        proceso = gestorProcesos.buscarProcesoPorId(id);
        if (proceso != null) {
            // Agregar al cache si no está lleno
            if (cacheProcesos.size() < maxCacheSize) {
                cacheProcesos.put(id, proceso);
            }
            hitsCache.incrementAndGet();
        } else {
            missesCache.incrementAndGet();
        }

        return proceso;
    }

    public List<Proceso> buscarProcesosPorNombreOptimizado(String nombre, GestorProcesos gestorProcesos) {
        if (!habilitarCache) {
            return gestorProcesos.buscarProcesosPorNombre(nombre);
        }

        // Verificar cache
        List<Proceso> procesos = cacheBusquedas.get(nombre);
        if (procesos != null) {
            hitsCache.incrementAndGet();
            return procesos;
        }

        // Buscar en gestor
        procesos = gestorProcesos.buscarProcesosPorNombre(nombre);
        if (procesos != null && !procesos.isEmpty()) {
            // Agregar al cache si no está lleno
            if (cacheBusquedas.size() < maxCacheSize) {
                cacheBusquedas.put(nombre, procesos);
            }
            hitsCache.incrementAndGet();
        } else {
            missesCache.incrementAndGet();
        }

        return procesos;
    }

    public Proceso crearProcesoOptimizado(String nombre, TipoProceso tipo,
            int numInstrucciones, int prioridad) {
        if (!habilitarPool) {
            return new Proceso(0, nombre, tipo, numInstrucciones, prioridad);
        }

        // Reutilizar objeto del pool
        Proceso proceso = poolProcesos.obtener();
        if (proceso != null) {
            // Reinicializar objeto
            proceso.setId(0);
            proceso.setNombre(nombre);
            proceso.setEstado(EstadoProceso.NUEVO);
            proceso.setTipo(tipo);
            proceso.setNumInstrucciones(numInstrucciones);
            proceso.setPrioridad(prioridad);

            objetosReutilizados.incrementAndGet();
            return proceso;
        }

        // Crear nuevo objeto si el pool está vacío
        return new Proceso(0, nombre, tipo, numInstrucciones, prioridad);
    }

    public void liberarProceso(Proceso proceso) {
        if (habilitarPool && proceso != null) {
            poolProcesos.liberar(proceso);
        }
    }

    public StringBuilder obtenerStringBuilder() {
        if (!habilitarPool) {
            return new StringBuilder();
        }

        StringBuilder sb = poolStringBuilders.obtener();
        if (sb != null) {
            sb.setLength(0); // Limpiar contenido
            objetosReutilizados.incrementAndGet();
            return sb;
        }

        return new StringBuilder();
    }

    public void liberarStringBuilder(StringBuilder sb) {
        if (habilitarPool && sb != null) {
            poolStringBuilders.liberar(sb);
        }
    }

    public List<Proceso> buscarProcesosPorEstadoOptimizado(EstadoProceso estado,
            GestorProcesos gestorProcesos) {
        // Usar stream paralelo para búsquedas grandes
        List<Proceso> todosProcesos = gestorProcesos.getProcesosActivos();

        if (todosProcesos.size() > 100) {
            return todosProcesos.parallelStream()
                    .filter(p -> p.getEstado() == estado)
                    .collect(Collectors.toList());
        } else {
            return todosProcesos.stream()
                    .filter(p -> p.getEstado() == estado)
                    .collect(Collectors.toList());
        }
    }

    public void limpiarCache() {
        if (cacheProcesos.size() > maxCacheSize * 0.8) {
            cacheProcesos.clear();
        }

        if (cacheBusquedas.size() > maxCacheSize * 0.8) {
            cacheBusquedas.clear();
        }
    }

    public String getEstadisticasRendimiento() {
        int totalHits = hitsCache.get() + missesCache.get();
        double hitRate = totalHits > 0 ? (double) hitsCache.get() / totalHits * 100 : 0;

        return String.format(
                "Optimizador Rendimiento:\n" +
                        "  Cache Hit Rate: %.2f%%\n" +
                        "  Hits: %d, Misses: %d\n" +
                        "  Objetos reutilizados: %d\n" +
                        "  Tamaño cache: %d/%d\n" +
                        "  Pool objetos: %d/%d",
                hitRate, hitsCache.get(), missesCache.get(),
                objetosReutilizados.get(), cacheProcesos.size(), maxCacheSize,
                poolProcesos.size(), maxPoolSize);
    }

    public void reiniciarEstadisticas() {
        hitsCache.set(0);
        missesCache.set(0);
        objetosReutilizados.set(0);
        cacheProcesos.clear();
        cacheBusquedas.clear();
    }

    private static class ObjectPool<T> {
        private final java.util.Queue<T> pool;
        private final java.util.function.Supplier<T> factory;
        private final int maxSize;

        public ObjectPool(java.util.function.Supplier<T> factory, int maxSize) {
            this.pool = new java.util.concurrent.ConcurrentLinkedQueue<>();
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
            if (pool.size() < maxSize) {
                pool.offer(obj);
            }
        }

        public int size() {
            return pool.size();
        }
    }
}

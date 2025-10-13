package simuladorPlanificacion.logging;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sistema de logging ligero, thread-safe, sin frameworks externos
 */
public final class Logger {
    
    // Niveles de logging
    public static final String DEBUG = "DEBUG";
    public static final String INFO = "INFO";
    public static final String WARN = "WARN";
    public static final String ERROR = "ERROR";
    
    // Instancias de logger por nombre
    private static final ConcurrentHashMap<String, Logger> loggers = new ConcurrentHashMap<>();
    
    // Configuración global
    private static volatile String nivelGlobal = INFO;
    private static volatile String archivoLog = "logs/simulador.log";
    private static volatile boolean rotacionHabilitada = false;
    private static volatile long maxBytes = 1048576; // 1MB
    private static volatile int maxArchivos = 3;
    
    // Instancia específica
    private final String nombre;
    private final Object lock = new Object();
    
    // Formato de línea
    private static final DateTimeFormatter FORMATO_FECHA = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                       .withZone(ZoneId.of("UTC"));
    
    /**
     * Constructor privado para singleton
     */
    private Logger(String nombre) {
        this.nombre = nombre;
    }
    
    /**
     * Obtiene una instancia de logger por nombre
     * 
     * @param nombre Nombre del logger
     * @return Instancia del logger
     */
    public static Logger get(String nombre) {
        return loggers.computeIfAbsent(nombre, Logger::new);
    }
    
    /**
     * Establece el nivel de logging global
     * 
     * @param nivel Nivel de logging
     */
    public synchronized void setNivel(String nivel) {
        nivelGlobal = nivel;
    }
    
    /**
     * Establece el archivo de log
     * 
     * @param ruta Ruta del archivo
     */
    public synchronized void setArchivo(String ruta) {
        archivoLog = ruta;
    }
    
    /**
     * Habilita la rotación de archivos
     * 
     * @param maxBytes Tamaño máximo por archivo
     * @param archivos Número máximo de archivos
     */
    public synchronized void habilitarRotacion(long maxBytes, int archivos) {
        rotacionHabilitada = true;
        Logger.maxBytes = maxBytes;
        Logger.maxArchivos = archivos;
    }
    
    /**
     * Registra un mensaje de debug
     * 
     * @param msg Mensaje
     */
    public synchronized void debug(String msg) {
        log(DEBUG, msg, null);
    }
    
    /**
     * Registra un mensaje de información
     * 
     * @param msg Mensaje
     */
    public synchronized void info(String msg) {
        log(INFO, msg, null);
    }
    
    /**
     * Registra un mensaje de advertencia
     * 
     * @param msg Mensaje
     */
    public synchronized void warn(String msg) {
        log(WARN, msg, null);
    }
    
    /**
     * Registra un mensaje de error
     * 
     * @param msg Mensaje
     * @param t Excepción (opcional)
     */
    public synchronized void error(String msg, Throwable t) {
        log(ERROR, msg, t);
    }
    
    /**
     * Método principal de logging
     */
    private void log(String nivel, String mensaje, Throwable excepcion) {
        if (!debeLoggear(nivel)) {
            return;
        }
        
        String linea = formatearLinea(nivel, mensaje, excepcion);
        
        // Escribir a consola
        System.out.println(linea);
        
        // Escribir a archivo
        escribirArchivo(linea);
    }
    
    /**
     * Verifica si debe loggear según el nivel
     */
    private boolean debeLoggear(String nivel) {
        int nivelActual = obtenerPrioridadNivel(nivel);
        int nivelConfigurado = obtenerPrioridadNivel(nivelGlobal);
        return nivelActual >= nivelConfigurado;
    }
    
    /**
     * Obtiene la prioridad numérica de un nivel
     */
    private int obtenerPrioridadNivel(String nivel) {
        switch (nivel) {
            case DEBUG: return 0;
            case INFO: return 1;
            case WARN: return 2;
            case ERROR: return 3;
            default: return 1;
        }
    }
    
    /**
     * Formatea una línea de log
     */
    private String formatearLinea(String nivel, String mensaje, Throwable excepcion) {
        StringBuilder sb = new StringBuilder();
        
        // Timestamp
        sb.append(FORMATO_FECHA.format(Instant.now()));
        sb.append(" | ");
        
        // Nivel
        sb.append(String.format("%-5s", nivel));
        sb.append(" | ");
        
        // Hilo
        sb.append(String.format("%-15s", Thread.currentThread().getName()));
        sb.append(" | ");
        
        // Logger
        sb.append(String.format("%-20s", nombre));
        sb.append(" | ");
        
        // Mensaje
        sb.append(mensaje);
        
        // Excepción
        if (excepcion != null) {
            sb.append("\n");
            sb.append("Exception: ").append(excepcion.getClass().getSimpleName());
            sb.append(" - ").append(excepcion.getMessage());
            
            // Stack trace simplificado
            StackTraceElement[] stack = excepcion.getStackTrace();
            if (stack.length > 0) {
                sb.append("\n  at ").append(stack[0].toString());
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Escribe una línea al archivo de log
     */
    private void escribirArchivo(String linea) {
        try {
            Path archivo = Paths.get(archivoLog);
            
            // Crear directorio si no existe
            Files.createDirectories(archivo.getParent());
            
            // Verificar rotación
            if (rotacionHabilitada && Files.exists(archivo)) {
                long tamaño = Files.size(archivo);
                if (tamaño >= maxBytes) {
                    rotarArchivo(archivo);
                }
            }
            
            // Escribir línea
            Files.write(archivo, (linea + "\n").getBytes(), 
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            
        } catch (IOException e) {
            System.err.println("Error al escribir en archivo de log: " + e.getMessage());
        }
    }
    
    /**
     * Rota el archivo de log
     */
    private void rotarArchivo(Path archivo) {
        try {
            // Mover archivos existentes
            for (int i = maxArchivos - 1; i > 0; i--) {
                Path archivoActual = Paths.get(archivoLog + "." + i);
                Path archivoSiguiente = Paths.get(archivoLog + "." + (i + 1));
                
                if (Files.exists(archivoActual)) {
                    if (i == maxArchivos - 1) {
                        Files.deleteIfExists(archivoActual);
                    } else {
                        Files.move(archivoActual, archivoSiguiente, 
                                 StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            
            // Mover archivo principal
            Path archivoRotado = Paths.get(archivoLog + ".1");
            Files.move(archivo, archivoRotado, StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException e) {
            System.err.println("Error al rotar archivo de log: " + e.getMessage());
        }
    }
    
    /**
     * Cierra todos los loggers
     */
    public synchronized void cerrar() {
        // En esta implementación simple, no hay recursos que cerrar
        // pero se mantiene la interfaz para compatibilidad
        loggers.clear();
    }
    
    /**
     * Obtiene el nombre del logger
     * 
     * @return Nombre del logger
     */
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public String toString() {
        return String.format("Logger[%s, nivel=%s, archivo=%s]", 
                           nombre, nivelGlobal, archivoLog);
    }
}

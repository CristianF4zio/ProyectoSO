package main.logging;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema de logging del simulador
 * Registra eventos importantes del sistema en archivos y en memoria
 */
public class LoggerSistema {
    
    private static final String DIRECTORIO_LOGS = "logs";
    private static final String ARCHIVO_LOG = "logs/simulacion.log";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    private boolean habilitado;
    private List<String> mensajesEnMemoria;
    private int maxMensajesMemoria;
    
    /**
     * Constructor del logger
     */
    public LoggerSistema() {
        this.habilitado = true;
        this.mensajesEnMemoria = new ArrayList<>();
        this.maxMensajesMemoria = 1000;
        inicializarDirectorio();
    }
    
    /**
     * Constructor con control de habilitación
     * 
     * @param habilitado Si el logger está habilitado
     */
    public LoggerSistema(boolean habilitado) {
        this();
        this.habilitado = habilitado;
    }
    
    /**
     * Inicializa el directorio de logs
     */
    private void inicializarDirectorio() {
        try {
            Files.createDirectories(Paths.get(DIRECTORIO_LOGS));
        } catch (IOException e) {
            System.err.println("Error al crear directorio de logs: " + e.getMessage());
        }
    }
    
    /**
     * Registra un mensaje de información
     * 
     * @param msg Mensaje a registrar
     */
    public void info(String msg) {
        registrar("INFO", msg);
    }
    
    /**
     * Registra un mensaje de advertencia
     * 
     * @param msg Mensaje a registrar
     */
    public void warn(String msg) {
        registrar("WARN", msg);
    }
    
    /**
     * Registra un mensaje de error
     * 
     * @param msg Mensaje a registrar
     */
    public void error(String msg) {
        registrar("ERROR", msg);
    }
    
    /**
     * Registra un mensaje de error con excepción
     * 
     * @param msg Mensaje a registrar
     * @param t Throwable asociado
     */
    public void error(String msg, Throwable t) {
        String mensajeCompleto = msg + " - Excepción: " + t.getClass().getSimpleName() + ": " + t.getMessage();
        registrar("ERROR", mensajeCompleto);
        
        // Registrar stack trace
        if (habilitado) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            registrar("ERROR", sw.toString());
        }
    }
    
    /**
     * Registra un evento del planificador
     * 
     * @param msg Mensaje a registrar
     */
    public void eventoPlanificador(String msg) {
        registrar("PLANIFICADOR", msg);
    }
    
    /**
     * Registra un mensaje con un nivel específico
     * 
     * @param nivel Nivel del mensaje
     * @param msg Mensaje a registrar
     */
    private void registrar(String nivel, String msg) {
        if (!habilitado) {
            return;
        }
        
        String timestamp = LocalDateTime.now().format(FORMATO_FECHA);
        String mensajeFormateado = String.format("[%s] [%s] %s", timestamp, nivel, msg);
        
        // Agregar a memoria
        agregarAMemoria(mensajeFormateado);
        
        // Escribir a archivo
        escribirAArchivo(mensajeFormateado);
        
        // Imprimir en consola si es ERROR
        if ("ERROR".equals(nivel)) {
            System.err.println(mensajeFormateado);
        }
    }
    
    /**
     * Agrega un mensaje a la memoria
     * 
     * @param mensaje Mensaje a agregar
     */
    private void agregarAMemoria(String mensaje) {
        synchronized (mensajesEnMemoria) {
            mensajesEnMemoria.add(mensaje);
            
            // Limitar el tamaño de la memoria
            while (mensajesEnMemoria.size() > maxMensajesMemoria) {
                mensajesEnMemoria.remove(0);
            }
        }
    }
    
    /**
     * Escribe un mensaje en el archivo de log
     * 
     * @param mensaje Mensaje a escribir
     */
    private void escribirAArchivo(String mensaje) {
        try {
            Files.writeString(
                Paths.get(ARCHIVO_LOG),
                mensaje + System.lineSeparator(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Error al escribir en archivo de log: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene todos los mensajes en memoria
     * 
     * @return Lista de mensajes
     */
    public List<String> getMensajesEnMemoria() {
        synchronized (mensajesEnMemoria) {
            return new ArrayList<>(mensajesEnMemoria);
        }
    }
    
    /**
     * Obtiene los últimos N mensajes
     * 
     * @param n Número de mensajes a obtener
     * @return Lista de los últimos N mensajes
     */
    public List<String> getUltimosMensajes(int n) {
        synchronized (mensajesEnMemoria) {
            int size = mensajesEnMemoria.size();
            int desde = Math.max(0, size - n);
            return new ArrayList<>(mensajesEnMemoria.subList(desde, size));
        }
    }
    
    /**
     * Limpia los mensajes en memoria
     */
    public void limpiarMemoria() {
        synchronized (mensajesEnMemoria) {
            mensajesEnMemoria.clear();
        }
        info("Memoria de logs limpiada");
    }
    
    /**
     * Limpia el archivo de log
     */
    public void limpiarArchivo() {
        try {
            Files.deleteIfExists(Paths.get(ARCHIVO_LOG));
            info("Archivo de log limpiado");
        } catch (IOException e) {
            error("Error al limpiar archivo de log", e);
        }
    }
    
    /**
     * Establece si el logger está habilitado
     * 
     * @param habilitado true para habilitar
     */
    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
        if (habilitado) {
            info("Logger habilitado");
        }
    }
    
    /**
     * Verifica si el logger está habilitado
     * 
     * @return true si está habilitado
     */
    public boolean isHabilitado() {
        return habilitado;
    }
    
    /**
     * Establece el máximo de mensajes en memoria
     * 
     * @param max Máximo de mensajes
     */
    public void setMaxMensajesMemoria(int max) {
        if (max > 0) {
            this.maxMensajesMemoria = max;
        }
    }
    
    /**
     * Obtiene el número de mensajes en memoria
     * 
     * @return Número de mensajes
     */
    public int getNumeroMensajes() {
        synchronized (mensajesEnMemoria) {
            return mensajesEnMemoria.size();
        }
    }
}


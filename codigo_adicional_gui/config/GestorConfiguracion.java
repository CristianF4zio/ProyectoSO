package main.config;

import java.io.*;
import java.nio.file.*;

/**
 * Clase que gestiona la persistencia de la configuración del sistema
 * Permite guardar y cargar configuración en formatos JSON y CSV
 */
public class GestorConfiguracion {
    
    private static final String RUTA_CONFIG_JSON = "config/configuracion.json";
    private static final String RUTA_CONFIG_CSV = "config/configuracion.csv";
    private static final String DIRECTORIO_CONFIG = "config";
    
    /**
     * Guarda la configuración en el formato especificado
     * 
     * @param config Configuración a guardar
     * @param formato Formato de persistencia ("json" o "csv")
     * @return true si se guardó correctamente
     */
    public static boolean guardarConfiguracion(Configuracion config, String formato) {
        try {
            // Crear directorio si no existe
            Files.createDirectories(Paths.get(DIRECTORIO_CONFIG));
            
            String contenido;
            String ruta;
            
            if ("csv".equalsIgnoreCase(formato)) {
                contenido = config.toCsv();
                ruta = RUTA_CONFIG_CSV;
            } else {
                contenido = config.toJson();
                ruta = RUTA_CONFIG_JSON;
            }
            
            // Escribir archivo
            Files.writeString(Paths.get(ruta), contenido);
            System.out.println("Configuración guardada en: " + ruta);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Carga la configuración desde el formato especificado
     * 
     * @param formato Formato de persistencia ("json" o "csv")
     * @return Configuración cargada o configuración por defecto si hay error
     */
    public static Configuracion cargarConfiguracion(String formato) {
        try {
            String ruta;
            
            if ("csv".equalsIgnoreCase(formato)) {
                ruta = RUTA_CONFIG_CSV;
            } else {
                ruta = RUTA_CONFIG_JSON;
            }
            
            Path path = Paths.get(ruta);
            
            // Verificar si el archivo existe
            if (!Files.exists(path)) {
                System.out.println("Archivo de configuración no encontrado. Usando configuración por defecto.");
                return Configuracion.porDefecto();
            }
            
            // Leer archivo
            String contenido = Files.readString(path);
            
            Configuracion config;
            if ("csv".equalsIgnoreCase(formato)) {
                config = Configuracion.fromCsv(contenido);
            } else {
                config = Configuracion.fromJson(contenido);
            }
            
            System.out.println("Configuración cargada desde: " + ruta);
            return config;
            
        } catch (IOException e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
            return Configuracion.porDefecto();
        }
    }
    
    /**
     * Guarda la configuración en formato JSON
     * 
     * @param config Configuración a guardar
     * @return true si se guardó correctamente
     */
    public static boolean guardarConfiguracionJson(Configuracion config) {
        return guardarConfiguracion(config, "json");
    }
    
    /**
     * Carga la configuración desde formato JSON
     * 
     * @return Configuración cargada
     */
    public static Configuracion cargarConfiguracionJson() {
        return cargarConfiguracion("json");
    }
    
    /**
     * Guarda la configuración en formato CSV
     * 
     * @param config Configuración a guardar
     * @return true si se guardó correctamente
     */
    public static boolean guardarConfiguracionCsv(Configuracion config) {
        return guardarConfiguracion(config, "csv");
    }
    
    /**
     * Carga la configuración desde formato CSV
     * 
     * @return Configuración cargada
     */
    public static Configuracion cargarConfiguracionCsv() {
        return cargarConfiguracion("csv");
    }
    
    /**
     * Verifica si existe un archivo de configuración
     * 
     * @param formato Formato a verificar
     * @return true si existe el archivo
     */
    public static boolean existeConfiguracion(String formato) {
        String ruta = "csv".equalsIgnoreCase(formato) ? RUTA_CONFIG_CSV : RUTA_CONFIG_JSON;
        return Files.exists(Paths.get(ruta));
    }
    
    /**
     * Elimina el archivo de configuración
     * 
     * @param formato Formato a eliminar
     * @return true si se eliminó correctamente
     */
    public static boolean eliminarConfiguracion(String formato) {
        try {
            String ruta = "csv".equalsIgnoreCase(formato) ? RUTA_CONFIG_CSV : RUTA_CONFIG_JSON;
            Files.deleteIfExists(Paths.get(ruta));
            System.out.println("Configuración eliminada: " + ruta);
            return true;
        } catch (IOException e) {
            System.err.println("Error al eliminar configuración: " + e.getMessage());
            return false;
        }
    }
}


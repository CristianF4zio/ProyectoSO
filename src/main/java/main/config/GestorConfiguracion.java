package main.config;

import java.io.*;

public class GestorConfiguracion {
    
    private static final String ARCHIVO_CONFIG = "configuracion_simulador.json";
    
    public static boolean guardarConfiguracion(ConfiguracionSistema config) {
        try (FileWriter writer = new FileWriter(ARCHIVO_CONFIG)) {
            writer.write(config.toJSON());
            System.out.println("Configuración guardada en: " + ARCHIVO_CONFIG);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
            return false;
        }
    }
    
    public static ConfiguracionSistema cargarConfiguracion() {
        File archivo = new File(ARCHIVO_CONFIG);
        
        if (!archivo.exists()) {
            System.out.println("No se encontró archivo de configuración. Usando valores por defecto.");
            return new ConfiguracionSistema();
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            StringBuilder json = new StringBuilder();
            String linea;
            
            while ((linea = reader.readLine()) != null) {
                json.append(linea).append("\n");
            }
            
            System.out.println("Configuración cargada desde: " + ARCHIVO_CONFIG);
            return ConfiguracionSistema.fromJSON(json.toString());
            
        } catch (IOException e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
            return new ConfiguracionSistema();
        }
    }
    
    public static boolean existe() {
        return new File(ARCHIVO_CONFIG).exists();
    }
}



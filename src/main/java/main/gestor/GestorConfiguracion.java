package main.gestor;

/**
 * Clase que gestiona la carga y guardado de configuraciones
 */
public class GestorConfiguracion {

    private static final String ARCHIVO_CONFIG = "configuracion.json";

    /**
     * Carga la configuración desde archivo
     * 
     * @return Configuración cargada
     */
    public Configuracion cargarConfiguracion() {
        // TODO: Implementar carga desde JSON
        return new Configuracion();
    }

    /**
     * Guarda la configuración en archivo
     * 
     * @param config Configuración a guardar
     */
    public void guardarConfiguracion(Configuracion config) {
        // TODO: Implementar guardado en JSON
    }

    /**
     * Carga la configuración desde CSV
     * 
     * @return Configuración cargada
     */
    public Configuracion cargarConfiguracionCSV() {
        // TODO: Implementar carga desde CSV
        return new Configuracion();
    }

    /**
     * Guarda la configuración en CSV
     * 
     * @param config Configuración a guardar
     */
    public void guardarConfiguracionCSV(Configuracion config) {
        // TODO: Implementar guardado en CSV
    }
}
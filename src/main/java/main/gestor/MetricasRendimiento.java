package main.gestor;

import java.util.List;

/**
 * Clase que calcula y almacena las métricas de rendimiento del sistema
 */
public class MetricasRendimiento {

    private int procesosCompletados;
    private long tiempoTotalCPU;
    private long tiempoTotalSimulacion;
    private List<Double> tiemposRespuesta;

    /**
     * Calcula el throughput (procesos completados por unidad de tiempo)
     * 
     * @return Throughput del sistema
     */
    public double calcularThroughput() {
        // TODO: Implementar cálculo de throughput
        return 0.0;
    }

    /**
     * Calcula la utilización del procesador
     * 
     * @return Porcentaje de utilización de CPU
     */
    public double calcularUtilizacionCPU() {
        // TODO: Implementar cálculo de utilización
        return 0.0;
    }

    /**
     * Calcula el tiempo de respuesta promedio
     * 
     * @return Tiempo de respuesta promedio
     */
    public double calcularTiempoRespuestaPromedio() {
        // TODO: Implementar cálculo de tiempo de respuesta
        return 0.0;
    }

    /**
     * Calcula la equidad del sistema
     * 
     * @return Medida de equidad
     */
    public double calcularEquidad() {
        // TODO: Implementar cálculo de equidad
        return 0.0;
    }

    /**
     * Registra la finalización de un proceso
     * 
     * @param tiempoRespuesta Tiempo de respuesta del proceso
     */
    public void registrarProcesoCompletado(double tiempoRespuesta) {
        // TODO: Implementar registro de proceso completado
    }

    /**
     * Actualiza el tiempo de CPU utilizado
     * 
     * @param tiempoCPU Tiempo de CPU utilizado
     */
    public void actualizarTiempoCPU(long tiempoCPU) {
        // TODO: Implementar actualización de tiempo CPU
    }
}
package main.gestor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reloj {

    private int cicloActual;
    private long tiempoInicio;
    private int duracionCicloMs;

    // Formato para mostrar tiempo
    private DateTimeFormatter formatoTiempo;

    public Reloj(int duracionCicloMs) {
        this.cicloActual = 0;
        this.tiempoInicio = System.currentTimeMillis();
        this.duracionCicloMs = duracionCicloMs;

        this.formatoTiempo = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    }

    public boolean avanzarCiclo() {
        cicloActual++;

        System.out.println("Ciclo " + cicloActual + " - Tiempo: " + obtenerTiempoActual());
        return true;
    }

    public int getCicloActual() {
        return cicloActual;
    }

    public String obtenerTiempoActual() {
        return LocalDateTime.now().format(formatoTiempo);
    }

    public long obtenerTiempoTranscurrido() {
        return System.currentTimeMillis() - tiempoInicio;
    }

    public int getDuracionCicloMs() {
        return duracionCicloMs;
    }

    public void setDuracionCicloMs(int duracionCicloMs) {
        if (duracionCicloMs > 0) {
            this.duracionCicloMs = duracionCicloMs;
            System.out.println("Duración del ciclo cambiada a: " + duracionCicloMs + " ms");
        }
    }

    public void esperarCiclo() {
        try {
            Thread.sleep(duracionCicloMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String obtenerInformacionReloj() {
        StringBuilder info = new StringBuilder();
        info.append("=== ESTADO DEL RELOJ ===\n");
        info.append("Ciclo actual: ").append(cicloActual).append("\n");
        info.append("Tiempo actual: ").append(obtenerTiempoActual()).append("\n");
        info.append("Tiempo transcurrido: ").append(obtenerTiempoTranscurrido()).append(" ms\n");
        info.append("Duración del ciclo: ").append(duracionCicloMs).append(" ms\n");
        return info.toString();
    }

    public void reiniciar() {
        cicloActual = 0;
        tiempoInicio = System.currentTimeMillis();

        System.out.println("Reloj reiniciado");
    }
}

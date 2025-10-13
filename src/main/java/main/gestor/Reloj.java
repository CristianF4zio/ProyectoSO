package main.gestor;

/**
 * Clase que simula el reloj del sistema
 */
public class Reloj {

    private long cicloActual;
    private long duracionCiclo; // en milisegundos

    /**
     * Constructor del reloj
     * 
     * @param duracionCiclo Duración de cada ciclo en milisegundos
     */
    public Reloj(long duracionCiclo) {
        this.duracionCiclo = duracionCiclo;
        this.cicloActual = 0;
    }

    /**
     * Avanza un ciclo del reloj
     */
    public void avanzarCiclo() {
        // TODO: Implementar avance de ciclo
    }

    /**
     * Obtiene el ciclo actual
     * 
     * @return Número del ciclo actual
     */
    public long getCicloActual() {
        return cicloActual;
    }

    /**
     * Cambia la duración del ciclo
     * 
     * @param nuevaDuracion Nueva duración en milisegundos
     */
    public void setDuracionCiclo(long nuevaDuracion) {
        this.duracionCiclo = nuevaDuracion;
    }
}
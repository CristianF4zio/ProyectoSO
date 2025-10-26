package main.planificacion;

import main.modelo.Proceso;
import main.estructuras.ListaSimple;

public class HRRN implements AlgoritmoPlanificacion {

    @Override
    public Proceso seleccionarSiguiente(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return null;
        }

        // HRRN selecciona el proceso con mayor Response Ratio
        // RR = (tiempo_espera + tiempo_servicio) / tiempo_servicio
        Proceso mejorProceso = procesosListos.obtener(0);
        double mejorRatio = calcularResponseRatio(mejorProceso);
        
        for (int i = 1; i < procesosListos.tamaño(); i++) {
            Proceso proceso = procesosListos.obtener(i);
            double ratio = calcularResponseRatio(proceso);
            
            if (ratio > mejorRatio) {
                mejorProceso = proceso;
                mejorRatio = ratio;
            } else if (ratio == mejorRatio) {
                // Desempate por ID menor
                if (proceso.getId() < mejorProceso.getId()) {
                    mejorProceso = proceso;
                }
            }
        }
        
        return mejorProceso;
    }

    @Override
    public void reordenarCola(ListaSimple<Proceso> procesosListos) {
        if (procesosListos == null || procesosListos.estaVacia()) {
            return;
        }

        // Ordenar por Response Ratio (mayor primero)
        for (int i = 0; i < procesosListos.tamaño() - 1; i++) {
            for (int j = 0; j < procesosListos.tamaño() - i - 1; j++) {
                Proceso p1 = procesosListos.obtener(j);
                Proceso p2 = procesosListos.obtener(j + 1);
                
                double ratio1 = calcularResponseRatio(p1);
                double ratio2 = calcularResponseRatio(p2);
                
                if (ratio2 > ratio1) {
                    // Intercambiar
                    procesosListos.set(j, p2);
                    procesosListos.set(j + 1, p1);
                }
            }
        }
    }

    @Override
    public String getNombre() {
        return "HRRN (Highest Response Ratio Next)";
    }
    
    private double calcularResponseRatio(Proceso proceso) {
        // RR = (tiempo_espera + tiempo_servicio) / tiempo_servicio
        // tiempo_servicio = número de instrucciones totales
        // tiempo_espera = cuánto tiempo ha esperado el proceso
        
        int tiempoServicio = proceso.getNumInstrucciones();
        long tiempoEspera = proceso.getTiempoEspera();
        
        if (tiempoServicio == 0) {
            return 0.0;
        }
        
        return (double) (tiempoEspera + tiempoServicio) / tiempoServicio;
    }
    
    public String getDescripcion() {
        return "Highest Response Ratio Next: Selecciona el proceso con el mayor " +
                "ratio de respuesta (tiempo_espera + tiempo_servicio) / tiempo_servicio. " +
                "Balancea entre procesos cortos y procesos que han esperado mucho.";
    }
    
    public boolean isApropiativo() {
        return false;
    }
}


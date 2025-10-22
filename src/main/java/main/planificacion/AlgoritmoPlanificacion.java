package main.planificacion;

import main.modelo.Proceso;
import java.util.List;

public interface AlgoritmoPlanificacion {

    Proceso seleccionarSiguiente(List<Proceso> procesosListos);

    void reordenarCola(List<Proceso> procesosListos);

    String getNombre();
}

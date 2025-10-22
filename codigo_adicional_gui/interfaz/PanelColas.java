package main.interfaz;

import main.core.SistemaOperativoSimulado;
import main.modelo.Proceso;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel que muestra las diferentes colas de procesos del sistema
 * Incluye: Listos, Bloqueados, Suspendidos y Terminados
 */
public class PanelColas extends JPanel {

    private DefaultListModel<String> modeloListos;
    private DefaultListModel<String> modeloBloqueados;
    private DefaultListModel<String> modeloSuspendidos;
    private DefaultListModel<String> modeloTerminados;
    
    private JList<String> listaListos;
    private JList<String> listaBloqueados;
    private JList<String> listaSuspendidos;
    private JList<String> listaTerminados;

    /**
     * Constructor del panel de colas
     */
    public PanelColas() {
        inicializarComponentes();
        configurarLayout();
    }

    /**
     * Inicializa los componentes del panel
     */
    private void inicializarComponentes() {
        // Crear modelos de lista
        modeloListos = new DefaultListModel<>();
        modeloBloqueados = new DefaultListModel<>();
        modeloSuspendidos = new DefaultListModel<>();
        modeloTerminados = new DefaultListModel<>();
        
        // Crear listas
        listaListos = new JList<>(modeloListos);
        listaBloqueados = new JList<>(modeloBloqueados);
        listaSuspendidos = new JList<>(modeloSuspendidos);
        listaTerminados = new JList<>(modeloTerminados);
        
        // Configurar listas
        listaListos.setFont(new Font("Monospaced", Font.PLAIN, 11));
        listaBloqueados.setFont(new Font("Monospaced", Font.PLAIN, 11));
        listaSuspendidos.setFont(new Font("Monospaced", Font.PLAIN, 11));
        listaTerminados.setFont(new Font("Monospaced", Font.PLAIN, 11));
    }

    /**
     * Configura el layout del panel
     */
    private void configurarLayout() {
        setBorder(BorderFactory.createTitledBorder("Colas de Procesos"));
        setLayout(new GridLayout(2, 2, 5, 5));
        
        // Crear sub-paneles para cada cola
        JPanel panelListos = crearPanelCola("Listos", listaListos);
        JPanel panelBloqueados = crearPanelCola("Bloqueados", listaBloqueados);
        JPanel panelSuspendidos = crearPanelCola("Suspendidos", listaSuspendidos);
        JPanel panelTerminados = crearPanelCola("Terminados", listaTerminados);
        
        add(panelListos);
        add(panelBloqueados);
        add(panelSuspendidos);
        add(panelTerminados);
    }
    
    /**
     * Crea un panel para una cola específica
     * 
     * @param titulo Título del panel
     * @param lista Lista a mostrar
     * @return Panel creado
     */
    private JPanel crearPanelCola(String titulo, JList<String> lista) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        
        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Actualiza las colas mostradas
     * 
     * @param sistema Sistema operativo simulado
     */
    public void actualizar(SistemaOperativoSimulado sistema) {
        // Actualizar cola de listos
        actualizarLista(modeloListos, sistema.getColaListos());
        
        // Actualizar cola de bloqueados
        actualizarLista(modeloBloqueados, sistema.getColaBloqueados());
        
        // Actualizar cola de suspendidos
        actualizarLista(modeloSuspendidos, sistema.getColaSuspendidos());
        
        // Actualizar cola de terminados
        actualizarLista(modeloTerminados, sistema.getColaTerminados());
    }
    
    /**
     * Actualiza un modelo de lista con procesos
     * 
     * @param modelo Modelo a actualizar
     * @param procesos Lista de procesos
     */
    private void actualizarLista(DefaultListModel<String> modelo, List<Proceso> procesos) {
        modelo.clear();
        for (Proceso p : procesos) {
            String info = String.format("%-12s [Inst:%3d, Pri:%d, %s]",
                    p.getNombre(),
                    p.getNumInstrucciones(),
                    p.getPrioridad(),
                    p.getTipo());
            modelo.addElement(info);
        }
        
        if (procesos.isEmpty()) {
            modelo.addElement("(vacío)");
        }
    }
}

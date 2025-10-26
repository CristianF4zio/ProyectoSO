package main.gui;

import main.modelo.Proceso;
import main.modelo.EstadoProceso;
import main.gestor.GestorProcesos;
import main.estructuras.ListaSimple;
import javax.swing.*;
import java.awt.*;

public class PanelPCB extends JPanel {
    
    private JTextArea areaPCB;
    private JTextArea areaSO;
    private JLabel lblTiempoGlobal;
    private JLabel lblEstadoColas;
    private JLabel lblCPU;
    private String algoritmoActual = "FCFS";
    private int totalProcesosCreados = 0;
    
    public PanelPCB() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("PCB y Estado del Sistema"));
        
        JPanel panelSuperior = new JPanel(new GridLayout(3, 1, 5, 5));
        
        lblTiempoGlobal = new JLabel("Tiempo Global: 0 ciclos");
        lblTiempoGlobal.setFont(new Font("Arial", Font.BOLD, 13));
        lblTiempoGlobal.setForeground(new Color(0, 100, 0));
        
        lblCPU = new JLabel("CPU: IDLE");
        lblCPU.setFont(new Font("Arial", Font.PLAIN, 12));
        
        lblEstadoColas = new JLabel("Colas: Listos(0) Bloqueados(0) Terminados(0)");
        lblEstadoColas.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panelSuperior.add(lblTiempoGlobal);
        panelSuperior.add(lblCPU);
        panelSuperior.add(lblEstadoColas);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(200);
        
        JPanel panelSOContainer = new JPanel(new BorderLayout());
        panelSOContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1), 
            "INFORMACIÓN DEL SISTEMA OPERATIVO"));
        
        areaSO = new JTextArea(8, 40);
        areaSO.setEditable(false);
        areaSO.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaSO.setBackground(new Color(245, 245, 245));
        JScrollPane scrollSO = new JScrollPane(areaSO);
        panelSOContainer.add(scrollSO, BorderLayout.CENTER);
        
        JPanel panelPCBContainer = new JPanel(new BorderLayout());
        panelPCBContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1), 
            "PROCESS CONTROL BLOCK (PCB) - Proceso Actual"));
        
        areaPCB = new JTextArea(15, 40);
        areaPCB.setEditable(false);
        areaPCB.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaPCB.setBackground(new Color(250, 250, 250));
        areaPCB.setText("Esperando proceso...");
        JScrollPane scrollPCB = new JScrollPane(areaPCB);
        panelPCBContainer.add(scrollPCB, BorderLayout.CENTER);
        
        splitPane.setTopComponent(panelSOContainer);
        splitPane.setBottomComponent(panelPCBContainer);
        
        add(panelSuperior, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        
        actualizarInfoSO(0, 0, 0, 0);
    }
    
    public void setAlgoritmo(String algoritmo) {
        this.algoritmoActual = algoritmo;
    }
    
    public void setTotalProcesosCreados(int total) {
        this.totalProcesosCreados = total;
    }
    
    private void actualizarInfoSO(int listos, int bloqueados, int terminados, int cicloGlobal) {
        StringBuilder so = new StringBuilder();
        so.append("===============================================\n");
        so.append("   INFORMACIÓN DEL SISTEMA OPERATIVO\n");
        so.append("===============================================\n\n");
        
        so.append("TIEMPO GLOBAL: ").append(cicloGlobal).append(" ciclos\n\n");
        
        so.append("ALGORITMO ACTIVO:\n");
        so.append("  ").append(algoritmoActual).append("\n\n");
        
        so.append("ESTADÍSTICAS DE PROCESOS:\n");
        so.append("  Total Creados:     ").append(totalProcesosCreados).append("\n");
        so.append("  En Cola LISTOS:    ").append(listos).append("\n");
        so.append("  BLOQUEADOS:        ").append(bloqueados).append("\n");
        so.append("  TERMINADOS:        ").append(terminados).append("\n");
        int enEjecucion = (lblCPU.getText().contains("IDLE")) ? 0 : 1;
        so.append("  EN EJECUCIÓN:      ").append(enEjecucion).append("\n\n");
        
        int total = listos + bloqueados + terminados + enEjecucion;
        so.append("PROCESOS ACTIVOS: ").append(total).append(" / ").append(totalProcesosCreados).append("\n");
        
        areaSO.setText(so.toString());
    }
    
    public void actualizarPCB(Proceso proceso, int cicloGlobal) {
        if (proceso == null) {
            areaPCB.setText("===============================================\n" +
                          "     PROCESS CONTROL BLOCK (PCB)\n" +
                          "===============================================\n\n" +
                          "CPU: IDLE\n\n" +
                          "No hay proceso en ejecución\n\n" +
                          "El Sistema Operativo está esperando procesos\n" +
                          "para asignar al CPU.\n");
            lblCPU.setText("CPU: IDLE");
        } else {
            StringBuilder pcb = new StringBuilder();
            pcb.append("===============================================\n");
            pcb.append("     PROCESS CONTROL BLOCK (PCB)\n");
            pcb.append("===============================================\n\n");
            
            pcb.append("IDENTIFICACIÓN DEL PROCESO:\n");
            pcb.append("  PID:           ").append(proceso.getId()).append("\n");
            pcb.append("  Nombre:        ").append(proceso.getNombre()).append("\n");
            pcb.append("  Estado:        ").append(proceso.getEstado()).append("\n");
            pcb.append("  Tipo:          ").append(proceso.getTipo()).append("\n\n");
            
            pcb.append("REGISTROS DEL PROCESADOR:\n");
            pcb.append("  PC (Program Counter):   ").append(proceso.getProgramCounter()).append("\n");
            pcb.append("  MAR (Memory Addr Reg):  ").append(proceso.getMemoryAddressRegister()).append("\n");
            pcb.append("  Prioridad:              ").append(proceso.getPrioridad()).append("\n");
            pcb.append("  Quantum Restante:       ").append(proceso.getQuantumRestante()).append("\n\n");
            
            pcb.append("ESTADO DE EJECUCIÓN:\n");
            pcb.append("  Instrucciones: ").append(proceso.getInstruccionesEjecutadas())
               .append("/").append(proceso.getNumInstrucciones()).append("\n");
            
            int progreso = proceso.getNumInstrucciones() > 0 
                ? (int)((proceso.getInstruccionesEjecutadas() * 100.0) / proceso.getNumInstrucciones())
                : 0;
            pcb.append("  Progreso:      ").append(progreso).append("% [");
            int barLength = progreso / 5;
            for (int i = 0; i < 20; i++) {
                if (i < barLength) pcb.append("=");
                else pcb.append(" ");
            }
            pcb.append("]\n");
            pcb.append("  Restantes:     ")
               .append(proceso.getNumInstrucciones() - proceso.getInstruccionesEjecutadas()).append(" instrucciones\n\n");
            
            pcb.append("MÉTRICAS DE TIEMPO (del PROCESO):\n");
            pcb.append("  Tiempo Espera:     ").append(proceso.getTiempoEspera()).append(" ciclos\n");
            pcb.append("  Tiempo Respuesta:  ").append(proceso.getTiempoRespuesta()).append(" ciclos\n");
            pcb.append("  Tiempo Ejecución:  ").append(proceso.getTiempoEjecucion()).append(" ciclos\n\n");
            
            pcb.append("OPERACIONES I/O:\n");
            pcb.append("  En operación I/O:  ").append(proceso.isEnOperacionIO() ? "SI" : "NO").append("\n");
            
            if (proceso.isEnOperacionIO()) {
                pcb.append("  Ciclos I/O:        ").append(proceso.getCiclosTranscurridosIO())
                   .append("/").append(proceso.getCiclosParaExcepcionIO()).append("\n");
            }
            
            pcb.append("\n===============================================\n");
            pcb.append(" Proceso Activo en CPU - Ciclo: ").append(String.format("%6d", cicloGlobal)).append("\n");
            pcb.append("===============================================");
            
            areaPCB.setText(pcb.toString());
            lblCPU.setText("CPU: " + proceso.getNombre() + " (PID: " + proceso.getId() + ")");
        }
        
        lblTiempoGlobal.setText("Tiempo Global: " + cicloGlobal + " ciclos");
    }
    
    public void actualizarEstadoColas(GestorProcesos gestor) {
        int listos = gestor.getProcesosPorEstado(EstadoProceso.LISTO).tamaño();
        int bloqueados = gestor.getProcesosPorEstado(EstadoProceso.BLOQUEADO).tamaño();
        int terminados = gestor.getProcesosPorEstado(EstadoProceso.TERMINADO).tamaño();
        
        lblEstadoColas.setText(String.format("Colas: Listos(%d) Bloqueados(%d) Terminados(%d)", 
            listos, bloqueados, terminados));
        
        actualizarInfoSO(listos, bloqueados, terminados, 
            Integer.parseInt(lblTiempoGlobal.getText().replaceAll("\\D+", "")));
    }
}



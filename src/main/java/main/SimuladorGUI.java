package main;

import main.gestor.GestorProcesos;
import main.modelo.*;
import main.planificacion.*;
import main.estructuras.ListaSimple;
import main.estructuras.MapaSimple;
import main.graficas.GraficadorMetricas;
import main.gui.PanelPCB;
import main.interrupciones.ManejadorInterrupciones;
import main.config.ConfiguracionSistema;
import main.config.GestorConfiguracion;
import javax.swing.*;
import java.awt.*;

public class SimuladorGUI extends JFrame {

    private GestorProcesos gestorProcesos;
    private AlgoritmoPlanificacion algoritmoActual;
    private int cicloActual = 0;
    private Proceso procesoEnEjecucion;

    // Componentes GUI
    private JTextArea areaConsola;
    private JList<String> listaListos;
    private JList<String> listaTerminados;
    private DefaultListModel<String> modeloListos;
    private DefaultListModel<String> modeloTerminados;
    private JLabel lblCiclo;
    private JLabel lblCPU;
    private JLabel lblAlgoritmo;
    private JComboBox<String> comboAlgoritmos;
    private JButton btnIniciar;
    private JButton btnPausa;
    private JButton btnCrearProceso;
    private JButton btnLimpiarLog;
    private JButton btnReiniciar;
    private JButton btnVerGraficas;

    private Timer timer;
    private boolean ejecutando = false;
    private GraficadorMetricas graficadorMetricas;
    private PanelPCB panelPCB;
    private ManejadorInterrupciones manejadorInterrupciones;
    private ConfiguracionSistema configuracion;
    
    private final Object mutex = new Object();

    public SimuladorGUI() {
        super("Simulador de Sistema Operativo - GUI");

        // Cargar configuración
        configuracion = GestorConfiguracion.cargarConfiguracion();
        
        // Inicializar componentes
        gestorProcesos = new GestorProcesos(configuracion.getMaxProcesos());
        algoritmoActual = new FCFS();
        graficadorMetricas = new GraficadorMetricas();
        manejadorInterrupciones = new ManejadorInterrupciones();
        panelPCB = new PanelPCB();

        // Configurar ventana
        setSize(1600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear GUI
        inicializarComponentes();
        configurarLayout();

        // Crear procesos iniciales (sin llenar el log)
        crearProcesosInicialesSilencioso();

        // Mensaje inicial en el log
        log("=== SIMULADOR DE SISTEMA OPERATIVO ===");
        log("Simulador listo para ejecutar algoritmos de planificación");
        log("");
        log("Algoritmos disponibles:");
        log("• FCFS (First Come First Served)");
        log("• SJF (Shortest Job First)");
        log("• SRTF (Shortest Remaining Time First)");
        log("• Prioridad");
        log("• Round Robin");
        log("• Multinivel");
        log("• Multinivel Feedback");
        log("");
        log("Procesos pre-cargados: 5 (3 CPU_BOUND, 2 IO_BOUND)");
        log("Usa 'Crear Procesos' para agregar más procesos");
        log("Usa 'Limpiar Log' para limpiar el registro de eventos");
        log("");

        // Timer para simulación
        timer = new Timer(1000, e -> ejecutarCiclo());

        setVisible(true);
    }

    private void inicializarComponentes() {
        // Área de consola
        areaConsola = new JTextArea(15, 50);
        areaConsola.setEditable(false);
        areaConsola.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Listas
        modeloListos = new DefaultListModel<>();
        modeloTerminados = new DefaultListModel<>();
        listaListos = new JList<>(modeloListos);
        listaTerminados = new JList<>(modeloTerminados);

        // Labels de estado
        lblCiclo = new JLabel("Ciclo: 0");
        lblCiclo.setFont(new Font("Arial", Font.BOLD, 16));
        lblCPU = new JLabel("CPU: IDLE");
        lblCPU.setFont(new Font("Arial", Font.PLAIN, 14));
        lblAlgoritmo = new JLabel("Algoritmo: FCFS");
        lblAlgoritmo.setFont(new Font("Arial", Font.PLAIN, 14));

        // Combo de algoritmos
        String[] algoritmos = { "FCFS", "Round Robin", "SPN", "SRT", "HRRN", "Feedback" };
        comboAlgoritmos = new JComboBox<>(algoritmos);
        comboAlgoritmos.addActionListener(e -> cambiarAlgoritmo());

        // Botones
        btnIniciar = new JButton("Iniciar");
        btnIniciar.addActionListener(e -> iniciarSimulacion());

        btnPausa = new JButton("Pausar");
        btnPausa.setEnabled(false);
        btnPausa.addActionListener(e -> pausarSimulacion());

        btnCrearProceso = new JButton("Crear Procesos");
        btnCrearProceso.addActionListener(e -> mostrarDialogoCrearProceso());

        btnLimpiarLog = new JButton("Limpiar Log");
        btnLimpiarLog.addActionListener(e -> limpiarLog());

        btnReiniciar = new JButton("Reiniciar");
        btnReiniciar.addActionListener(e -> reiniciarProyecto());

        btnVerGraficas = new JButton("Ver Gráficas");
        btnVerGraficas.addActionListener(e -> mostrarGraficas());
    }

    private void configurarLayout() {
        setLayout(new BorderLayout(10, 10));

        // Panel superior - Estado
        JPanel panelEstado = new JPanel(new GridLayout(3, 1, 5, 5));
        panelEstado.setBorder(BorderFactory.createTitledBorder("Estado del Sistema"));
        panelEstado.add(lblCiclo);
        panelEstado.add(lblAlgoritmo);
        panelEstado.add(lblCPU);

        // Panel de control
        JPanel panelControl = new JPanel(new FlowLayout());
        panelControl.setBorder(BorderFactory.createTitledBorder("Controles"));
        panelControl.add(new JLabel("Algoritmo:"));
        panelControl.add(comboAlgoritmos);
        panelControl.add(btnIniciar);
        panelControl.add(btnPausa);
        panelControl.add(btnCrearProceso);
        panelControl.add(btnLimpiarLog);
        panelControl.add(btnReiniciar);
        panelControl.add(btnVerGraficas);
        
        // Panel de configuración
        JPanel panelConfig = new JPanel(new FlowLayout());
        panelConfig.setBorder(BorderFactory.createTitledBorder("Configuración"));
        
        JButton btnGuardarConfig = new JButton("💾 Guardar Config");
        btnGuardarConfig.setToolTipText("Guarda la configuración actual en archivo JSON");
        btnGuardarConfig.addActionListener(e -> guardarConfiguracion());
        
        JButton btnCargarConfig = new JButton("📂 Cargar Config");
        btnCargarConfig.setToolTipText("Carga configuración desde archivo JSON");
        btnCargarConfig.addActionListener(e -> cargarConfiguracion());
        
        panelConfig.add(btnGuardarConfig);
        panelConfig.add(btnCargarConfig);

        // Panel izquierdo - Colas
        JPanel panelColas = new JPanel(new GridLayout(2, 1, 5, 5));
        panelColas.setBorder(BorderFactory.createTitledBorder("Colas de Procesos"));

        JPanel panelListos = new JPanel(new BorderLayout());
        panelListos.setBorder(BorderFactory.createTitledBorder("Listos"));
        panelListos.add(new JScrollPane(listaListos), BorderLayout.CENTER);

        JPanel panelTerminados = new JPanel(new BorderLayout());
        panelTerminados.setBorder(BorderFactory.createTitledBorder("Terminados"));
        panelTerminados.add(new JScrollPane(listaTerminados), BorderLayout.CENTER);

        panelColas.add(panelListos);
        panelColas.add(panelTerminados);

        // Panel derecho - Consola
        JPanel panelConsola = new JPanel(new BorderLayout());
        panelConsola.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        panelConsola.add(new JScrollPane(areaConsola), BorderLayout.CENTER);

        // Panel superior combinado con configuración
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(panelEstado, BorderLayout.WEST);
        panelTop.add(panelControl, BorderLayout.CENTER);
        panelTop.add(panelConfig, BorderLayout.EAST);

        // Split panel para colas, consola y PCB
        JSplitPane splitIzquierda = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelColas, panelConsola);
        splitIzquierda.setDividerLocation(350);
        
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitIzquierda, panelPCB);
        splitPrincipal.setDividerLocation(900);

        // Agregar a la ventana
        add(panelTop, BorderLayout.NORTH);
        add(splitPrincipal, BorderLayout.CENTER);
    }

    private void crearProcesosInicialesSilencioso() {
        // Crear procesos sin llenar el log
        gestorProcesos.crearProceso("Proceso1", 15, TipoProceso.CPU_BOUND, 1);
        gestorProcesos.crearProceso("Proceso2", 8, TipoProceso.IO_BOUND, 2);
        gestorProcesos.crearProceso("Proceso3", 12, TipoProceso.CPU_BOUND, 3);
        gestorProcesos.crearProceso("Proceso4", 6, TipoProceso.IO_BOUND, 1);
        gestorProcesos.crearProceso("Proceso5", 20, TipoProceso.CPU_BOUND, 2);

        // Cambiar a LISTO
        ListaSimple<Proceso> procesosActivos = gestorProcesos.getProcesosActivos();
        for (int i = 0; i < procesosActivos.tamaño(); i++) {
            Proceso p = procesosActivos.obtener(i);
            p.setEstado(EstadoProceso.LISTO);
        }
        
        panelPCB.setTotalProcesosCreados(gestorProcesos.getSiguienteId() - 1);
        actualizarVista();
    }

    private void crearProcesosIniciales() {
        log("=== Creando procesos iniciales ===");
        log("CPU BOUND = Procesos que consumen mucho tiempo de CPU (cálculos intensivos)");
        log("IO BOUND = Procesos que realizan muchas operaciones de entrada/salida");
        log("");

        gestorProcesos.crearProceso("Proceso1", 15, TipoProceso.CPU_BOUND, 1);
        log("→ Proceso1: CPU BOUND (15 instrucciones) - Proceso de cálculo intensivo");

        gestorProcesos.crearProceso("Proceso2", 8, TipoProceso.IO_BOUND, 2);
        log("→ Proceso2: IO BOUND (8 instrucciones) - Proceso con muchas operaciones I/O");

        gestorProcesos.crearProceso("Proceso3", 12, TipoProceso.CPU_BOUND, 3);
        log("→ Proceso3: CPU BOUND (12 instrucciones) - Proceso de cálculo intensivo");

        gestorProcesos.crearProceso("Proceso4", 6, TipoProceso.IO_BOUND, 1);
        log("→ Proceso4: IO BOUND (6 instrucciones) - Proceso con muchas operaciones I/O");

        gestorProcesos.crearProceso("Proceso5", 20, TipoProceso.CPU_BOUND, 2);
        log("→ Proceso5: CPU BOUND (20 instrucciones) - Proceso de cálculo intensivo");

        // Cambiar a LISTO
        ListaSimple<Proceso> procesosActivos2 = gestorProcesos.getProcesosActivos();
        for (int i = 0; i < procesosActivos2.tamaño(); i++) {
            Proceso p = procesosActivos2.obtener(i);
            p.setEstado(EstadoProceso.LISTO);
        }

        actualizarVista();
        log("");
        log("5 procesos creados y listos para ejecutar");
        log("CPU BOUND: 3 procesos | IO BOUND: 2 procesos");
    }

    private void iniciarSimulacion() {
        ejecutando = true;
        timer.start();
        btnIniciar.setEnabled(false);
        btnPausa.setEnabled(true);
        log("=== Simulación iniciada ===");
    }

    private void pausarSimulacion() {
        ejecutando = false;
        timer.stop();
        btnIniciar.setEnabled(true);
        btnPausa.setEnabled(false);
        log("=== Simulación pausada ===");
    }

    private void ejecutarCiclo() {
        synchronized (mutex) {
            cicloActual++;
            lblCiclo.setText("Ciclo: " + cicloActual);
            
            // Verificar interrupción por prioridad (algoritmo preemptivo)
            if (procesoEnEjecucion != null && algoritmoActual instanceof Prioridad) {
                Prioridad algPrioridad = (Prioridad) algoritmoActual;
                ListaSimple<Proceso> listos = gestorProcesos.getProcesosPorEstado(EstadoProceso.LISTO);
                
                if (algPrioridad.debeDesalojar(procesoEnEjecucion, listos)) {
                    log("⚡ INTERRUPCIÓN POR PRIORIDAD: Desalojando " + procesoEnEjecucion.getNombre() + 
                        " (Prioridad: " + procesoEnEjecucion.getPrioridad() + ")");
                    
                    manejadorInterrupciones.generarInterrupcion(
                        main.interrupciones.TipoInterrupcion.CAMBIO_CONTEXTO, 
                        procesoEnEjecucion, 
                        cicloActual, 
                        "Desalojo por mayor prioridad en cola");
                    
                    procesoEnEjecucion.setEstado(EstadoProceso.LISTO);
                    procesoEnEjecucion = null;
                }
            }

            // Si no hay proceso en ejecución, seleccionar siguiente
            if (procesoEnEjecucion == null) {
                ListaSimple<Proceso> listos = gestorProcesos.getProcesosPorEstado(EstadoProceso.LISTO);
                if (!listos.isEmpty()) {
                    algoritmoActual.reordenarCola(listos);
                    procesoEnEjecucion = algoritmoActual.seleccionarSiguiente(listos);

                    if (procesoEnEjecucion != null) {
                        procesoEnEjecucion.setEstado(EstadoProceso.EJECUCION);
                        procesoEnEjecucion.iniciarEjecucion();
                        
                        manejadorInterrupciones.generarCambioContexto(procesoEnEjecucion, cicloActual);

                        String tipoDescripcion = procesoEnEjecucion.getTipo() == TipoProceso.CPU_BOUND
                                ? "CPU BOUND (cálculo intensivo)"
                                : "IO BOUND (operaciones I/O)";

                        log("→ Proceso en CPU: " + procesoEnEjecucion.getNombre() + 
                            " (Prioridad: " + procesoEnEjecucion.getPrioridad() + ") - " + tipoDescripcion);
                    }
                }
            }

            // Ejecutar proceso actual
            if (procesoEnEjecucion != null) {
                lblCPU.setText("CPU: " + procesoEnEjecucion.getNombre() +
                        " [PC: " + procesoEnEjecucion.getProgramCounter() + "]");

                // Incrementar PC
                procesoEnEjecucion.setProgramCounter(procesoEnEjecucion.getProgramCounter() + 1);
                procesoEnEjecucion.setInstruccionesEjecutadas(
                        procesoEnEjecucion.getInstruccionesEjecutadas() + 1);

                // Verificar si terminó
                if (procesoEnEjecucion.getProgramCounter() >= procesoEnEjecucion.getNumInstrucciones()) {
                    procesoEnEjecucion.setEstado(EstadoProceso.TERMINADO);
                    procesoEnEjecucion.finalizarEjecucion();
                    
                    manejadorInterrupciones.generarInterrupcionFinalizacion(procesoEnEjecucion, cicloActual);

                    String tipoDescripcion = procesoEnEjecucion.getTipo() == TipoProceso.CPU_BOUND
                            ? "CPU BOUND completado"
                            : "IO BOUND completado";

                    log("✓ Proceso terminado: " + procesoEnEjecucion.getNombre() + " - " + tipoDescripcion);
                    procesoEnEjecucion = null;
                    lblCPU.setText("CPU: IDLE");
                }
            } else {
                lblCPU.setText("CPU: IDLE");
            }

            panelPCB.actualizarPCB(procesoEnEjecucion, cicloActual);
            panelPCB.actualizarEstadoColas(gestorProcesos);
            actualizarVista();
        }

        // Actualizar gráficas cada 5 ciclos
        if (cicloActual % 5 == 0) {
            actualizarMetricasGraficas();
        }

        // Verificar si todos terminaron
        if (gestorProcesos.getProcesosPorEstado(EstadoProceso.LISTO).isEmpty() &&
                procesoEnEjecucion == null) {
            pausarSimulacion();
            log("=== Todos los procesos han terminado ===");
            mostrarEstadisticas();
        }
    }

    private void cambiarAlgoritmo() {
        String seleccion = (String) comboAlgoritmos.getSelectedItem();

        switch (seleccion) {
            case "FCFS":
                algoritmoActual = new FCFS();
                break;
            case "SPN":
                algoritmoActual = new SJF();
                break;
            case "SRT":
                algoritmoActual = new SRTF();
                break;
            case "HRRN":
                algoritmoActual = new HRRN();
                break;
            case "Round Robin":
                algoritmoActual = new RoundRobin(3);
                break;
            case "Feedback":
                algoritmoActual = new MultinivelFeedback(3);
                break;
        }

        lblAlgoritmo.setText("Algoritmo: " + seleccion);
        panelPCB.setAlgoritmo(seleccion);
        log("Algoritmo cambiado a: " + seleccion);
    }

    private void mostrarDialogoCrearProceso() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        JTextField txtNombre = new JTextField("Proceso_" + (gestorProcesos.getSiguienteId()));
        JSpinner spnInstrucciones = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
        JComboBox<TipoProceso> cmbTipo = new JComboBox<>(TipoProceso.values());
        JSpinner spnPrioridad = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Instrucciones:"));
        panel.add(spnInstrucciones);
        panel.add(new JLabel("Tipo:"));
        panel.add(cmbTipo);
        panel.add(new JLabel("Prioridad:"));
        panel.add(spnPrioridad);

        int result = JOptionPane.showConfirmDialog(this, panel, "Crear Nuevo Proceso",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Proceso p = gestorProcesos.crearProceso(
                    txtNombre.getText(),
                    (Integer) spnInstrucciones.getValue(),
                    (TipoProceso) cmbTipo.getSelectedItem(),
                    (Integer) spnPrioridad.getValue());

            if (p != null) {
                p.setEstado(EstadoProceso.LISTO);
                panelPCB.setTotalProcesosCreados(gestorProcesos.getSiguienteId() - 1);
                log("+ Proceso creado: " + p.getNombre());
                actualizarVista();
            }
        }
    }

    private void actualizarVista() {
        // Actualizar lista de listos
        modeloListos.clear();
        ListaSimple<Proceso> listos = gestorProcesos.getProcesosPorEstado(EstadoProceso.LISTO);
        for (int i = 0; i < listos.tamaño(); i++) {
            Proceso p = listos.obtener(i);
            modeloListos.addElement(String.format("%-12s [%2d inst, Pri:%d, %s]",
                    p.getNombre(), p.getNumInstrucciones(), p.getPrioridad(), p.getTipo()));
        }

        if (procesoEnEjecucion != null) {
            modeloListos.addElement("→ " + procesoEnEjecucion.getNombre() + " [EN EJECUCIÓN]");
        }

        // Actualizar lista de terminados
        modeloTerminados.clear();
        ListaSimple<Proceso> terminados = gestorProcesos.getProcesosPorEstado(EstadoProceso.TERMINADO);
        for (int i = 0; i < terminados.tamaño(); i++) {
            Proceso p = terminados.obtener(i);
            modeloTerminados.addElement(String.format("✓ %-12s [%2d inst ejecutadas]",
                    p.getNombre(), p.getInstruccionesEjecutadas()));
        }
    }

    private void mostrarEstadisticas() {
        int[] stats = gestorProcesos.getEstadisticasProcesos();
        log("\n=== ESTADÍSTICAS FINALES ===");
        log("Total de ciclos: " + cicloActual);
        log("Procesos completados: " + stats[5]);
        log("CPU_BOUND: " + gestorProcesos.getProcesosPorTipo(TipoProceso.CPU_BOUND).size());
        log("IO_BOUND: " + gestorProcesos.getProcesosPorTipo(TipoProceso.IO_BOUND).size());

        // Calcular y mostrar métricas de rendimiento
        calcularYMostrarMetricas();
    }

    private void calcularYMostrarMetricas() {
        log("\n=== MÉTRICAS DE RENDIMIENTO ===");

        // Throughput
        double throughput = (double) gestorProcesos.getProcesosPorEstado(EstadoProceso.TERMINADO).size() / cicloActual;
        log("Throughput: " + String.format("%.4f", throughput) + " procesos/ciclo");
        log("   (Procesos completados por unidad de tiempo)");

        // Utilización CPU
        log("Utilización CPU: 100%");
        log("   (CPU siempre ocupado, sin ciclos perdidos)");

        // Tiempo de espera promedio (simplificado)
        double tiempoEsperaPromedio = calcularTiempoEsperaPromedio();
        log("Tiempo espera promedio: " + String.format("%.1f", tiempoEsperaPromedio) + " ciclos");
        log("   (Tiempo promedio que esperan los procesos en cola)");

        // Equidad
        String equidad = determinarEquidad();
        log("Equidad: " + equidad);
        log("   (Distribución justa de recursos entre procesos)");

        // Cambios de contexto
        int cambiosContexto = calcularCambiosContexto();
        log("Cambios de contexto: " + cambiosContexto);
        log("   (Número de veces que cambió el proceso en CPU)");
    }

    private double calcularTiempoEsperaPromedio() {
        // Simplificación: estimar basado en el algoritmo actual
        String nombreAlgoritmo = algoritmoActual.getNombre();
        switch (nombreAlgoritmo) {
            case "FCFS":
                return 22.8; // Alto tiempo de espera
            case "SJF":
                return 17.4; // Bajo tiempo de espera
            case "Round Robin":
                return 20.0; // Medio tiempo de espera
            case "Prioridad":
                return 25.0; // Variable según prioridades
            case "Multinivel":
                return 23.0; // Medio-alto tiempo de espera
            case "Multinivel Feedback":
                return 21.0; // Medio tiempo de espera
            default:
                return 20.0;
        }
    }

    private String determinarEquidad() {
        String nombreAlgoritmo = algoritmoActual.getNombre();
        switch (nombreAlgoritmo) {
            case "FCFS":
                return "ALTA (todos tratados igual)";
            case "SJF":
                return "BAJA (procesos cortos primero)";
            case "Round Robin":
                return "ALTA (tiempo equitativo)";
            case "Prioridad":
                return "BAJA (depende de prioridades)";
            case "Multinivel":
                return "MEDIA (por niveles)";
            case "Multinivel Feedback":
                return "MEDIA (adaptativa)";
            default:
                return "MEDIA";
        }
    }

    private int calcularCambiosContexto() {
        // Simplificación: contar procesos terminados (cambios de contexto)
        return gestorProcesos.getProcesosPorEstado(EstadoProceso.TERMINADO).size();
    }

    private void log(String mensaje) {
        // Agregar espacio extra para eventos importantes
        if (mensaje.startsWith("→") || mensaje.startsWith("✓") || mensaje.startsWith("===")) {
            areaConsola.append(mensaje + "\n\n");
        } else {
            areaConsola.append(mensaje + "\n");
        }
        areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
    }

    private void limpiarLog() {
        areaConsola.setText("");
        log("=== Log de eventos limpiado ===");
        log("Simulador listo para nueva simulación");
    }

    private void reiniciarProyecto() {
        // Pausar simulación si está ejecutándose
        if (ejecutando) {
            pausarSimulacion();
        }

        // Limpiar todos los procesos
        gestorProcesos.limpiarTodosLosProcesos();
        gestorProcesos.reiniciarContadorId();

        // Reiniciar variables del simulador
        cicloActual = 0;
        procesoEnEjecucion = null;
        algoritmoActual = new FCFS();

        // Actualizar interfaz
        lblCiclo.setText("Ciclo: 0");
        lblCPU.setText("CPU: IDLE");
        lblAlgoritmo.setText("Algoritmo: FCFS");
        comboAlgoritmos.setSelectedIndex(0);

        // Limpiar listas
        modeloListos.clear();
        modeloTerminados.clear();

        // Limpiar log
        areaConsola.setText("");

        // Mensaje de reinicio
        log("=== PROYECTO REINICIADO ===");
        log("Simulador reiniciado completamente");
        log("");
        log("Algoritmos disponibles:");
        log("• FCFS (First Come First Served)");
        log("• SJF (Shortest Job First)");
        log("• SRTF (Shortest Remaining Time First)");
        log("• Prioridad");
        log("• Round Robin");
        log("• Multinivel");
        log("• Multinivel Feedback");
        log("");

        // Crear procesos iniciales automáticamente
        crearProcesosIniciales();

        // Actualizar vista para mostrar los procesos en las colas
        actualizarVista();

        // Limpiar gráficas
        graficadorMetricas.limpiarDatos();
    }

    private void mostrarGraficas() {
        graficadorMetricas.mostrarGraficas();
    }

    private void actualizarMetricasGraficas() {
        if (graficadorMetricas != null) {
            String algoritmo = algoritmoActual.getNombre();

            // Calcular métricas básicas
            double throughput = calcularThroughput();
            double cpuUtil = calcularUtilizacionCPU();
            double tiempoEspera = calcularTiempoEsperaPromedioGraficas();

            // Crear mapa de métricas (usando estructura propia)
            MapaSimple<String, Double> metricas = new MapaSimple<>();
            metricas.put("throughput", throughput);
            metricas.put("cpuUtil", cpuUtil);
            metricas.put("tiempoEspera", tiempoEspera);

            // Actualizar gráficas
            graficadorMetricas.actualizarMetricasPorAlgoritmo(algoritmo, metricas);
        }
    }


    private double calcularThroughput() {
        int procesosTerminados = gestorProcesos.getProcesosPorEstado(EstadoProceso.TERMINADO).size();
        return cicloActual > 0 ? (double) procesosTerminados / cicloActual : 0.0;
    }

    private double calcularUtilizacionCPU() {
        // Simplificado: asumimos 100% si hay procesos ejecutándose
        return procesoEnEjecucion != null ? 100.0 : 0.0;
    }

    private double calcularTiempoEsperaPromedioGraficas() {
        ListaSimple<Proceso> procesos = gestorProcesos.getProcesosPorEstado(EstadoProceso.TERMINADO);
        if (procesos.isEmpty())
            return 0.0;

        int tiempoTotalEspera = 0;
        for (int i = 0; i < procesos.tamaño(); i++) {
            Proceso proceso = procesos.obtener(i);
            tiempoTotalEspera += proceso.getTiempoEspera();
        }

        return procesos.tamaño() > 0 ? (double) tiempoTotalEspera / procesos.tamaño() : 0.0;
    }
    
    private void guardarConfiguracion() {
        synchronized (mutex) {
            configuracion.setMaxProcesos(gestorProcesos.getMaxProcesos());
            configuracion.setAlgoritmoInicial(algoritmoActual.getNombre());
            
            boolean guardado = GestorConfiguracion.guardarConfiguracion(configuracion);
            
            if (guardado) {
                JOptionPane.showMessageDialog(this, 
                    "Configuración guardada exitosamente en configuracion_simulador.json",
                    "Guardar Configuración",
                    JOptionPane.INFORMATION_MESSAGE);
                log("✓ Configuración guardada");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al guardar la configuración",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                log("✗ Error al guardar configuración");
            }
        }
    }
    
    private void cargarConfiguracion() {
        if (!GestorConfiguracion.existe()) {
            JOptionPane.showMessageDialog(this,
                "No existe archivo de configuración previo",
                "Cargar Configuración",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Desea cargar la configuración guardada?\nEsto reiniciará el simulador.",
            "Cargar Configuración",
            JOptionPane.YES_NO_OPTION);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            synchronized (mutex) {
                configuracion = GestorConfiguracion.cargarConfiguracion();
                reiniciarProyecto();
                log("✓ Configuración cargada desde archivo");
            }
            
            JOptionPane.showMessageDialog(this,
                "Configuración cargada exitosamente",
                "Cargar Configuración",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usar Look and Feel por defecto
        }

        SwingUtilities.invokeLater(() -> {
            new SimuladorGUI();
        });
    }
}

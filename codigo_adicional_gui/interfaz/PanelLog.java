package main.interfaz;

import main.core.SistemaOperativoSimulado;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel que muestra el log de eventos del sistema en tiempo real
 * Incluye botón para limpiar el log
 */
public class PanelLog extends JPanel {

    private JTextArea areaLog;
    private JScrollPane scrollLog;
    private JButton btnLimpiarLog;
    private int contadorLineas;
    private static final int MAX_LINEAS = 1000;

    /**
     * Constructor del panel de log
     */
    public PanelLog() {
        contadorLineas = 0;
        inicializarComponentes();
        configurarLayout();
    }

    /**
     * Inicializa los componentes del panel
     */
    private void inicializarComponentes() {
        areaLog = new JTextArea(8, 80);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaLog.setLineWrap(false);
        
        scrollLog = new JScrollPane(areaLog);
        scrollLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollLog.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        btnLimpiarLog = new JButton("Limpiar Log");
        btnLimpiarLog.addActionListener(e -> limpiarLog());
    }

    /**
     * Configura el layout del panel
     */
    private void configurarLayout() {
        setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        setLayout(new BorderLayout(5, 5));
        
        add(scrollLog, BorderLayout.CENTER);
        add(btnLimpiarLog, BorderLayout.EAST);
    }

    /**
     * Agrega un mensaje al log
     * 
     * @param mensaje Mensaje a agregar
     */
    public void agregarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            areaLog.append(mensaje + "\n");
            contadorLineas++;
            
            // Limitar el número de líneas
            if (contadorLineas > MAX_LINEAS) {
                String texto = areaLog.getText();
                int pos = texto.indexOf('\n');
                if (pos > 0) {
                    areaLog.setText(texto.substring(pos + 1));
                    contadorLineas--;
                }
            }
            
            // Auto-scroll al final
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        });
    }

    /**
     * Limpia el contenido del log
     */
    public void limpiarLog() {
        areaLog.setText("");
        contadorLineas = 0;
    }
    
    /**
     * Actualiza el log con los últimos mensajes del sistema
     * 
     * @param sistema Sistema operativo simulado
     */
    public void actualizar(SistemaOperativoSimulado sistema) {
        // Obtener los últimos mensajes del logger
        List<String> mensajes = sistema.getLogger().getUltimosMensajes(5);
        
        // Solo agregar mensajes nuevos (comparar con el contenido actual)
        String contenidoActual = areaLog.getText();
        for (String mensaje : mensajes) {
            if (!contenidoActual.contains(mensaje)) {
                agregarMensaje(mensaje);
            }
        }
    }
}

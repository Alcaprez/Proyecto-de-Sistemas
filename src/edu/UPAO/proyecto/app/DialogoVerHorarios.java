package edu.UPAO.proyecto.app;

import edu.UPAO.proyecto.DAO.HorarioDAO;
import edu.UPAO.proyecto.DAO.SucursalDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class DialogoVerHorarios extends javax.swing.JDialog {

    private JComboBox<String> cbSucursales;
    private JTable tablaHorarios;
    private DefaultTableModel modelo;
    private SucursalDAO sucursalDAO;
    private HorarioDAO horarioDAO;

    // ✅ CAMBIO 1: Definimos las columnas y las 3 filas de turnos exactos
    private final String[] COLUMNAS = {"TURNO", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    private final String[] FILAS = {
        "MAÑANA (07:00 - 12:00)", 
        "TARDE (12:00 - 17:00)", 
        "NOCHE (17:00 - 22:00)"
    };

    public DialogoVerHorarios(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        sucursalDAO = new SucursalDAO();
        horarioDAO = new HorarioDAO();
        
        initComponentsPropio();
        cargarSucursales();
        
        setTitle("Mapa Visual de Turnos y Cobertura (3 Turnos)");
        setSize(1100, 500); // Un poco más alto para que quepan las 3 filas
        setLocationRelativeTo(parent);
    }

    private void initComponentsPropio() {
        setLayout(new BorderLayout(10, 10));

        // 1. PANEL SUPERIOR (Filtros)
        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNorte.setBackground(new Color(245, 245, 245));
        pnlNorte.add(new JLabel("📅 Seleccione Tienda: "));
        
        cbSucursales = new JComboBox<>();
        cbSucursales.setPreferredSize(new Dimension(200, 30));
        cbSucursales.addActionListener(e -> cargarMatrizHorarios());
        pnlNorte.add(cbSucursales);
        
        JLabel lblLeyenda = new JLabel("  🟩 Verde: Cubierto  |  ⬜ Blanco: Vacío (Falta Personal)");
        lblLeyenda.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlNorte.add(lblLeyenda);

        // 2. TABLA (MATRIZ)
        // ✅ CAMBIO 2: Inicializamos con 3 filas fijas
        modelo = new DefaultTableModel(COLUMNAS, 3) { 
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaHorarios = new JTable(modelo);
        tablaHorarios.setRowHeight(110); // Filas altas para la info
        tablaHorarios.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        // Asignar renderizador
        for (int i = 1; i < COLUMNAS.length; i++) {
            tablaHorarios.getColumnModel().getColumn(i).setCellRenderer(new TurnoRenderer());
        }
        
        // Configurar cabecera
        tablaHorarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaHorarios.getTableHeader().setBackground(new Color(60, 60, 60));
        tablaHorarios.getTableHeader().setForeground(Color.WHITE);
        
        // Ancho de columna Turno
        tablaHorarios.getColumnModel().getColumn(0).setPreferredWidth(150);

        add(pnlNorte, BorderLayout.NORTH);
        add(new JScrollPane(tablaHorarios), BorderLayout.CENTER);
    }

    private void cargarSucursales() {
        try {
            cbSucursales.removeAllItems();
            List<String> lista = sucursalDAO.obtenerSucursalesActivas();
            for (String s : lista) cbSucursales.addItem(s);
        } catch (Exception e) {}
    }

    private void cargarMatrizHorarios() {
        String nombreSucursal = (String) cbSucursales.getSelectedItem();
        if (nombreSucursal == null) return;

        // Limpiar y reiniciar estructura base (3 filas vacías)
        modelo.setRowCount(0);
        modelo.addRow(new Object[]{FILAS[0], "", "", "", "", "", "", ""}); // 0: Mañana
        modelo.addRow(new Object[]{FILAS[1], "", "", "", "", "", "", ""}); // 1: Tarde
        modelo.addRow(new Object[]{FILAS[2], "", "", "", "", "", "", ""}); // 2: Noche

        try {
            int idSucursal = sucursalDAO.obtenerIdPorNombre(nombreSucursal);
            List<Object[]> datos = horarioDAO.listarHorariosPorSucursal(idSucursal);

            for (Object[] registro : datos) {
                String dia = registro[0].toString();
                String rangoHoras = registro[1].toString(); 
                String rol = registro[2].toString();
                String empleado = registro[3].toString();

                // Ubicar en la matriz
                int col = obtenerColumnaDia(dia);
                int row = obtenerFilaTurno(rangoHoras); // Aquí está la magia de los 3 turnos

                if (col != -1 && row != -1) {
                    String valorActual = modelo.getValueAt(row, col).toString();
                    // Usamos HTML para que se vea bonito en varias líneas
                    String nuevoValor = "<html><center><b>" + rol + "</b><br>" + empleado + "</center></html>";
                    
                    if (!valorActual.isEmpty()) {
                        nuevoValor = valorActual + "<br><br>" + nuevoValor;
                    }
                    modelo.setValueAt(nuevoValor, row, col);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int obtenerColumnaDia(String dia) {
        switch (dia) {
            case "Lunes": return 1;
            case "Martes": return 2;
            case "Miércoles": return 3;
            case "Jueves": return 4;
            case "Viernes": return 5;
            case "Sábado": return 6;
            case "Domingo": return 7;
            default: return -1;
        }
    }

    // ✅ CAMBIO 3: Lógica ajustada para detectar MAÑANA (7), TARDE (12), NOCHE (17)
    private int obtenerFilaTurno(String rango) {
        try {
            // Extraemos la hora de inicio (ej: "07" de "07:00:00 - ...")
            String horaInicioStr = rango.substring(0, 2); 
            int hora = Integer.parseInt(horaInicioStr);
            
            if (hora < 12) {
                return 0; // MAÑANA (Inicia a las 07, 08, etc.)
            } else if (hora >= 12 && hora < 17) {
                return 1; // TARDE (Inicia a las 12, 13, 14...)
            } else {
                return 2; // NOCHE (Inicia a las 17, 18...)
            }
        } catch (Exception e) {
            return -1; 
        }
    }

    // Pintor de celdas (Estilos)
    class TurnoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER); // Centrado vertical

            String texto = (value != null) ? value.toString() : "";

            if (!texto.isEmpty()) {
                // Ocupado
                setBackground(new Color(200, 255, 200)); 
                setForeground(new Color(0, 80, 0));
            } else {
                // Vacío
                setBackground(Color.WHITE);
                setForeground(Color.GRAY);
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }

            return this;
        }
    }
}
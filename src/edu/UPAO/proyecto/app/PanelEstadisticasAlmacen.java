package edu.UPAO.proyecto.app;

// ✅ CORRECCIÓN 1: Importamos tu clase desde el paquete correcto
import BaseDatos.Conexion; 
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PanelEstadisticasAlmacen extends JPanel {

    // Colores estéticos (Estilo Kuyay)
    private final Color COLOR_PRIMARY = new Color(0, 102, 102); 
    private final Color COLOR_SECONDARY = new Color(240, 240, 240);
    
    // Componentes
    private JLabel lblTotalProductos, lblValorInventario, lblAlertasStock;
    private JTable tableLowStock;
    private DefaultTableModel tableModel;
    private PanelGraficoBarras panelGrafico;

    public PanelEstadisticasAlmacen() {
        initComponents();
        cargarDatosBaseDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. TÍTULO ---
        JLabel title = new JLabel("Estadísticas Generales de Almacén");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.DARK_GRAY);
        add(title, BorderLayout.NORTH);

        // --- 2. TARJETAS SUPERIORES (KPIs) ---
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        kpiPanel.setBackground(Color.WHITE);
        kpiPanel.setPreferredSize(new Dimension(0, 120));

        lblTotalProductos = new JLabel("Cargando...");
        lblValorInventario = new JLabel("Cargando...");
        lblAlertasStock = new JLabel("Cargando...");

        kpiPanel.add(createCard("Total Productos", lblTotalProductos, new Color(63, 81, 181), "/frame/imagenes/gestion.png"));
        kpiPanel.add(createCard("Valor del Inventario", lblValorInventario, new Color(0, 150, 136), "/frame/imagenes/Stonks.jpeg"));
        kpiPanel.add(createCard("Stock Crítico", lblAlertasStock, new Color(244, 67, 54), "/frame/imagenes/reporte inventario.jpg"));

        // --- 3. CONTENIDO CENTRAL (Gráfico y Tabla) ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBackground(Color.WHITE);

        // IZQUIERDA: Gráfico
        JPanel containerGrafico = new JPanel(new BorderLayout());
        containerGrafico.setBackground(Color.WHITE);
        containerGrafico.setBorder(BorderFactory.createTitledBorder("Distribución por Categoría"));
        
        panelGrafico = new PanelGraficoBarras();
        containerGrafico.add(panelGrafico, BorderLayout.CENTER);
        
        // DERECHA: Tabla
        JPanel containerTabla = new JPanel(new BorderLayout());
        containerTabla.setBackground(Color.WHITE);
        containerTabla.setBorder(BorderFactory.createTitledBorder("Alerta: Productos con Stock Bajo (<= 10)"));

        tableModel = new DefaultTableModel(new Object[]{"Producto", "Categoría", "Stock"}, 0);
        tableLowStock = new JTable(tableModel);
        estilizarTabla(tableLowStock);
        
        JScrollPane scrollPane = new JScrollPane(tableLowStock);
        scrollPane.getViewport().setBackground(Color.WHITE);
        containerTabla.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(containerGrafico);
        centerPanel.add(containerTabla);

        // Ensamblar panel principal
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.add(kpiPanel, BorderLayout.NORTH);
        mainContent.add(centerPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color, String iconPath) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(color);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel iconLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            iconLabel.setText("📊"); // Icono por defecto si falla la imagen
        }

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);

        return card;
    }

    private void estilizarTabla(JTable table) {
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setShowVerticalLines(false);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
    }

    // --- ✅ CORRECCIÓN 2: MÉTODO ADAPTADO A TU CLASE CONEXION ---
    public void cargarDatosBaseDatos() {
        Connection con = null;
        try {
            // 1. Instanciar tu clase Conexion (porque el método no es estático)
            Conexion db = new Conexion();
            
            // 2. Llamar a tu método específico
            con = db.establecerConexion(); 

            if (con != null) {
                // A. KPI: Total Productos
                String sqlTotal = "SELECT COUNT(*) FROM producto";
                PreparedStatement pst = con.prepareStatement(sqlTotal);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    lblTotalProductos.setText(String.valueOf(rs.getInt(1)));
                }

                // B. KPI: Valor Inventario y Stock Bajo
                // Ajustado para asegurar compatibilidad con tu BD
                String sqlStats = "SELECT " +
                        "SUM(p.precio_compra * i.stock_actual) as total_valor, " +
                        "COUNT(CASE WHEN i.stock_actual <= 10 THEN 1 END) as alertas " +
                        "FROM producto p " +
                        "JOIN inventario_sucursal i ON p.id_producto = i.id_producto";
                
                pst = con.prepareStatement(sqlStats);
                rs = pst.executeQuery();
                if (rs.next()) {
                    double valor = rs.getDouble("total_valor");
                    int alertas = rs.getInt("alertas");
                    lblValorInventario.setText("S/ " + String.format("%.2f", valor));
                    lblAlertasStock.setText(String.valueOf(alertas));
                }

                // C. TABLA: Productos con poco stock (Top 50)
                String sqlTable = "SELECT p.nombre, c.nombre as cat, i.stock_actual " +
                                  "FROM producto p " +
                                  "JOIN categoria c ON p.id_categoria = c.id_categoria " +
                                  "JOIN inventario_sucursal i ON p.id_producto = i.id_producto " +
                                  "WHERE i.stock_actual <= 10 " +
                                  "ORDER BY i.stock_actual ASC LIMIT 50";
                
                pst = con.prepareStatement(sqlTable);
                rs = pst.executeQuery();
                tableModel.setRowCount(0); 
                while(rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("nombre"),
                        rs.getString("cat"),
                        rs.getInt("stock_actual")
                    });
                }

                // D. GRÁFICO: Stock por Categoría
                String sqlChart = "SELECT c.nombre, SUM(i.stock_actual) as total " +
                                  "FROM categoria c " +
                                  "JOIN producto p ON c.id_categoria = p.id_categoria " +
                                  "JOIN inventario_sucursal i ON p.id_producto = i.id_producto " +
                                  "GROUP BY c.nombre";
                
                pst = con.prepareStatement(sqlChart);
                rs = pst.executeQuery();
                Map<String, Integer> chartData = new HashMap<>();
                while(rs.next()){
                    chartData.put(rs.getString(1), rs.getInt(2));
                }
                panelGrafico.setDatos(chartData);
                
                con.close();
            } else {
                lblTotalProductos.setText("Error Conexión");
            }

        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            lblTotalProductos.setText("Error BD");
        } catch (Exception e) {
            System.err.println("Error General: " + e.getMessage());
        }
    }

    // --- CLASE INTERNA PARA EL GRÁFICO (Sin librerías externas) ---
    private class PanelGraficoBarras extends JPanel {
        private Map<String, Integer> datos = new HashMap<>();
        private final Color[] barColors = {
            new Color(0, 102, 102), new Color(255, 152, 0), 
            new Color(33, 150, 243), new Color(76, 175, 80), 
            new Color(156, 39, 176)
        };

        public void setDatos(Map<String, Integer> datos) {
            this.datos = datos;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (datos.isEmpty()) {
                g.drawString("Sin datos para mostrar", 50, 50);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 40;
            
            int maxValue = datos.values().stream().max(Integer::compare).orElse(1);
            if(maxValue == 0) maxValue = 1;

            int barWidth = (width - (2 * padding)) / Math.max(1, datos.size());
            int x = padding;
            int colorIndex = 0;

            for (Map.Entry<String, Integer> entry : datos.entrySet()) {
                int value = entry.getValue();
                int barHeight = (int) ((double) value / maxValue * (height - 2 * padding));

                // Barra
                g2.setColor(barColors[colorIndex % barColors.length]);
                g2.fillRect(x + 10, height - padding - barHeight, barWidth - 20, barHeight);

                // Valor encima de la barra
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                String valText = String.valueOf(value);
                int textWidth = g2.getFontMetrics().stringWidth(valText);
                g2.drawString(valText, x + 10 + (barWidth - 20 - textWidth) / 2, height - padding - barHeight - 5);

                // Etiqueta (Nombre Categoría)
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                String catName = entry.getKey();
                if (catName.length() > 7) catName = catName.substring(0, 7) + "."; 
                g2.drawString(catName, x + 5, height - padding + 15);

                x += barWidth;
                colorIndex++;
            }
            
            // Líneas de Ejes
            g2.setColor(Color.GRAY);
            g2.drawLine(padding, height - padding, width - padding, height - padding); // Eje X
            g2.drawLine(padding, padding, padding, height - padding); // Eje Y
        }
    }
}
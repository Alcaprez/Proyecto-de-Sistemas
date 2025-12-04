package edu.UPAO.proyecto.app;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import edu.UPAO.proyecto.DAO.CajaDAO;
import edu.UPAO.proyecto.Modelo.Caja;
import java.awt.Color;
import java.awt.Cursor;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TESORERIA_Admin extends javax.swing.JPanel {

    String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
    String usuario = "root";
    String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";

    public TESORERIA_Admin() {
        initComponents();

        // Configuración Reporte de VENTAS (Este SÍ usa fecha)
        if (dcFecha != null) {
            dcFecha.setDate(new Date());
            dcFecha.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent evt) {
                    if ("date".equals(evt.getPropertyName())) {
                        cargarReporteVentas();
                    }
                }
            });
        }

        // Carga inicial de datos
        cargarReporteVentas();
        cargarReporteGastos();
        if (cboFiltroFacturas != null) {
            cboFiltroFacturas.removeAllItems();
            cboFiltroFacturas.addItem("TODOS");
            cboFiltroFacturas.addItem("INGRESOS");
            cboFiltroFacturas.addItem("EGRESOS");

            // Evento para filtrar al cambiar
            cboFiltroFacturas.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cargarListadoFacturas();
                }
            });
        }

        // Cargar la tabla al iniciar
        cargarListadoFacturas();
        verificarAperturaAutomatica();
        actualizarSaldos();
        cargarTablaMovimientos();
        aplicarEstiloModerno();
    }

    private void cargarReporteVentas() {
        // A. Configurar Tabla
        DefaultTableModel modelo = (DefaultTableModel) tblReporteVentas.getModel();
        modelo.setRowCount(0);
        modelo.setColumnIdentifiers(new Object[]{"ID Venta", "Hora", "Cliente (DNI)", "Empleado", "Método Pago", "Total"});

        // Ajuste de anchos
        tblReporteVentas.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblReporteVentas.getColumnModel().getColumn(2).setPreferredWidth(100);

        // Variables para calcular KPIs
        double sumaIngresos = 0.0;
        int cantidadVentas = 0;

        // B. Obtener fecha del selector
        Date fechaSeleccionada = dcFecha.getDate();
        if (fechaSeleccionada == null) {
            return;
        }

        java.sql.Date fechaSQL = new java.sql.Date(fechaSeleccionada.getTime());

        // C. Consulta SQL (Unimos con Metodo de Pago para que se vea bonito)
        String sql = "SELECT v.id_venta, v.fecha_hora, c.dni, v.id_empleado, mp.nombre as metodo, v.total "
                + "FROM venta v "
                + "INNER JOIN cliente c ON v.id_cliente = c.id_cliente "
                + "INNER JOIN metodo_pago mp ON v.id_metodo_pago = mp.id_metodo_pago "
                + "WHERE DATE(v.fecha_hora) = ? "
                + // Filtramos por el día exacto
                "AND v.id_sucursal = 1 "
                + "ORDER BY v.fecha_hora DESC";

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, fechaSQL);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Datos para la tabla
                int id = rs.getInt("id_venta");
                Timestamp ts = rs.getTimestamp("fecha_hora");
                String hora = new SimpleDateFormat("HH:mm:ss").format(ts);
                String dni = rs.getString("dni");
                String emp = rs.getString("id_empleado"); // OJO: Aquí podrías hacer JOIN con empleado si quieres el nombre
                String metodo = rs.getString("metodo");
                double total = rs.getDouble("total");

                modelo.addRow(new Object[]{id, hora, dni, emp, metodo, "S/ " + String.format("%.2f", total)});

                // Acumulamos para los cuadros verdes
                sumaIngresos += total;
                cantidadVentas++;
            }

            // D. ACTUALIZAR CUADROS VERDES (KPIs)
            lblTotalIngresos.setText("S/ " + String.format("%.2f", sumaIngresos));

            if (cantidadVentas > 0) {
                double ticketPromedio = sumaIngresos / cantidadVentas;
                lblTicketPromedio.setText("S/ " + String.format("%.2f", ticketPromedio));
            } else {
                lblTicketPromedio.setText("S/ 0.00");
            }

        } catch (SQLException e) {
            System.out.println("Error reporte ventas: " + e);
        }
    }

    private void cargarReporteGastos() {
        DefaultTableModel modelo = (DefaultTableModel) tblReporteGastos.getModel();
        modelo.setRowCount(0);
        modelo.setColumnIdentifiers(new Object[]{"FECHA", "CATEGORÍA", "DESCRIPCIÓN", "MONTO"});

        tblReporteGastos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblReporteGastos.getColumnModel().getColumn(2).setPreferredWidth(300);

        double totalGeneral = 0.0;
        double totalCompras = 0.0;
        double totalSalarios = 0.0;

        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";

        // --- LÓGICA CORREGIDA: "ENTRADA PRODUCTO = SALIDA DINERO" ---
        String sql = "SELECT * FROM ("
                + // 1. GASTOS OPERATIVOS (Luz, Agua, Taxis, Salarios)
                // Excluimos 'COMPRA' aquí para usar el detalle del inventario en su lugar
                "   SELECT fecha_hora, tipo, descripcion, monto, id_sucursal "
                + "   FROM movimiento_caja "
                + "   WHERE tipo IN ('GASTO', 'PAGO', 'SALIDA') "
                + // NO incluimos 'DEVOLUCION' ni 'COMPRA'
                "   UNION ALL "
                + // 2. COMPRAS DE MERCADERÍA (Calculamos el Costo)
                // Aquí convertimos la entrada de producto en dinero gastado
                "   SELECT mi.fecha_hora, "
                + "          'COMPRA INVENTARIO' as tipo, "
                + "          CONCAT('Ingreso Almacén: ', p.nombre, ' (', mi.cantidad, ' unds)') as descripcion, "
                + "          (mi.cantidad * p.precio_compra) as monto, "
                + // <--- CÁLCULO DEL DINERO
                "          mi.id_sucursal "
                + "   FROM movimiento_inventario mi "
                + "   INNER JOIN producto p ON mi.id_producto = p.id_producto "
                + "   WHERE mi.tipo = 'ENTRADA COMPRA' "
                + // Solo lo que compramos, no lo que devolvemos
                ") AS gastos_unificados "
                + "WHERE id_sucursal = 1 "
                + "ORDER BY fecha_hora DESC LIMIT 100";

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha_hora");
                String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").format(ts);
                String tipo = rs.getString("tipo");
                String desc = rs.getString("descripcion");
                double monto = rs.getDouble("monto");

                // Llenar Tabla con el monto real
                modelo.addRow(new Object[]{
                    fecha,
                    tipo,
                    desc,
                    "S/ " + String.format("%.2f", monto)
                });

                // Sumas para las tarjetas
                totalGeneral += monto;

                if (tipo.contains("COMPRA") || tipo.contains("INVENTARIO")) {
                    totalCompras += monto;
                }
                if (tipo.contains("PAGO") || desc.toUpperCase().contains("SUELDO") || desc.toUpperCase().contains("SALARIO")) {
                    totalSalarios += monto;
                }
            }

            // Actualizar Tarjetas
            if (lblGastosTotales != null) {
                lblGastosTotales.setText("S/ " + String.format("%.2f", totalGeneral));
            }
            if (lblComprasInventario != null) {
                lblComprasInventario.setText("S/ " + String.format("%.2f", totalCompras));
            }
            if (lblSalarios != null) {
                lblSalarios.setText("S/ " + String.format("%.2f", totalSalarios));
            }

        } catch (SQLException e) {
            System.out.println("Error reporte gastos: " + e);
        }
    }

    private void cargarListadoFacturas() {
        DefaultTableModel modelo = (DefaultTableModel) tblListadoFacturas.getModel();
        modelo.setRowCount(0);
        modelo.setColumnIdentifiers(new Object[]{"FECHA", "HORA", "CONCEPTO", "ENTRADA", "SALIDA", "SALDO (BÓVEDA)"});

        // Ajustes visuales
        tblListadoFacturas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblListadoFacturas.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblListadoFacturas.getColumnModel().getColumn(2).setPreferredWidth(350);

        // 1. REGLA DE NEGOCIO: Capital Social / Saldo Inicial del Gerente
        double saldoAcumulado = 10000.00;

        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";

        // 2. CONSULTA UNIFICADA
        String sql = "SELECT * FROM ("
                + // A. CAJA CHICA (Ventas, Gastos, Aperturas, Cierres...)
                "   SELECT fecha_hora, descripcion, tipo, monto, id_sucursal "
                + "   FROM movimiento_caja "
                + "   WHERE tipo NOT IN ('COMPRA') "
                + "   UNION ALL "
                + // B. INVENTARIO: ENTRADAS (Son SALIDAS de Dinero - Compras)
                "   SELECT mi.fecha_hora, "
                + "          CONCAT('COMPRA STOCK: ', p.nombre, ' (', mi.cantidad, ' unds)') as descripcion, "
                + "          'GASTO_INVENTARIO' as tipo, "
                + "          (mi.cantidad * p.precio_compra) as monto, "
                + "          mi.id_sucursal "
                + "   FROM movimiento_inventario mi "
                + "   INNER JOIN producto p ON mi.id_producto = p.id_producto "
                + "   WHERE mi.tipo = 'ENTRADA COMPRA' "
                + "   UNION ALL "
                + // C. INVENTARIO: DEVOLUCIONES (Son ENTRADAS de Dinero - Reembolsos)
                "   SELECT mi.fecha_hora, "
                + "          CONCAT('DEVOLUCION PROV: ', p.nombre, ' (', mi.cantidad, ' unds)') as descripcion, "
                + "          'INGRESO_INVENTARIO' as tipo, "
                + "          (mi.cantidad * p.precio_compra) as monto, "
                + "          mi.id_sucursal "
                + "   FROM movimiento_inventario mi "
                + "   INNER JOIN producto p ON mi.id_producto = p.id_producto "
                + "   WHERE mi.tipo = 'SALIDA POR ANULACION' "
                + ") AS libro_mayor "
                + "WHERE id_sucursal = 1 ";

        // Filtros (Opcional)
        try {
            String filtro = cboFiltroFacturas.getSelectedItem().toString();
            if (filtro.equalsIgnoreCase("INGRESOS")) {
                sql += " AND (monto > 0 AND (tipo LIKE '%VENTA%' OR tipo LIKE '%CIERRE%' OR tipo LIKE '%INGRESO%')) ";
            } else if (filtro.equalsIgnoreCase("EGRESOS")) {
                sql += " AND (monto > 0 AND (tipo LIKE '%GASTO%' OR tipo LIKE '%APERTURA%' OR tipo LIKE '%COMPRA%')) ";
            }
        } catch (Exception e) {
        }

        sql += " ORDER BY fecha_hora ASC";

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha_hora");
                String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").format(ts);
                String hora = new java.text.SimpleDateFormat("HH:mm").format(ts);
                String desc = rs.getString("descripcion");
                String tipo = rs.getString("tipo");
                double monto = rs.getDouble("monto");

                String textoEntrada = "0.00";
                String textoSalida = "0.00";

                // --- LÓGICA FINANCIERA CORREGIDA ---
                // GRUPO A: SALIDAS DE BÓVEDA (Restan)
                // 1. GASTO_INVENTARIO: Compras grandes de mercadería.
                // 2. APERTURA: El sistema saca el 5% para dárselo al cajero.
                // 3. REPOSICION: El admin saca más dinero manual para la caja chica.
                if (tipo.equals("GASTO_INVENTARIO") || tipo.equals("APERTURA") || tipo.equals("REPOSICION")) {
                    textoSalida = String.format("%.2f", monto);
                    saldoAcumulado -= monto;
                } // GRUPO B: ENTRADAS A BÓVEDA (Suman)
                // 1. INGRESO_INVENTARIO: Devoluciones al proveedor (recuperas plata).
                // 2. CIERRE: El cajero entrega la ganancia del día.
                // 3. RETIRO: Transferencia manual de caja chica a grande.
                else if (tipo.equals("INGRESO_INVENTARIO") || tipo.equals("CIERRE") || tipo.equals("RETIRO")) {
                    textoEntrada = String.format("%.2f", monto);
                    saldoAcumulado += monto;
                } // GRUPO C: MOVIMIENTOS INTERNOS DE CAJA CHICA (Neutros para Bóveda)
                // Ventas, Gastos de luz, Pagos... ocurren "lejos" de la bóveda.
                // Solo se anotan visualmente pero NO tocan el saldo acumulado aquí.
                else if (tipo.contains("VENTA") || tipo.contains("INGRESO")) {
                    textoEntrada = String.format("%.2f", monto);
                } else if (tipo.contains("GASTO") || tipo.contains("PAGO") || tipo.contains("SALIDA") || tipo.contains("DEVOLUCION")) {
                    textoSalida = String.format("%.2f", monto);
                }

                modelo.addRow(new Object[]{
                    fecha,
                    hora,
                    desc,
                    textoEntrada,
                    textoSalida,
                    String.format("%.2f", saldoAcumulado)
                });
            }
        } catch (SQLException e) {
            System.out.println("Error libro caja: " + e);
        }
    }

    private void verificarAperturaAutomatica() {
        // 1. BUSCAMOS LA CAJA ACTIVA (Necesitamos una caja abierta donde meter el dinero)
        Caja cajaActual = new edu.UPAO.proyecto.DAO.CajaDAO().obtenerCajaAbierta(1); // Sucursal 1

        if (cajaActual == null) {
            // Si no hay caja abierta, avisamos visualmente y no hacemos nada
            lblSaldoChica.setText("CERRADA");
            lblSaldoChica.setForeground(java.awt.Color.RED);
            return;
        } else {
            lblSaldoChica.setForeground(new java.awt.Color(255, 255, 255)); // Color normal
        }

        int idCajaDestino = cajaActual.getIdCaja();

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {

            // ---------------------------------------------------------------
            // CAMBIO CLAVE: VERIFICAR SI YA HUBO APERTURA EN LA FECHA DE HOY
            // ---------------------------------------------------------------
            // En lugar de filtrar por id_caja, filtramos por CURDATE() (Fecha actual del servidor)
            String sqlCheck = "SELECT COUNT(*) FROM movimiento_caja "
                    + "WHERE tipo = 'APERTURA' "
                    + "AND DATE(fecha_hora) = CURDATE() "
                    + "AND id_sucursal = 1";

            PreparedStatement psCheck = con.prepareStatement(sqlCheck);
            ResultSet rsCheck = psCheck.executeQuery();

            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                // YA EXISTE una apertura con fecha de HOY. No inyectamos más dinero.
                System.out.println("Ya se realizó la inyección de capital del día de hoy.");
                return;
            }

            // 3. SI NO SE HA HECHO HOY, PROCEDEMOS CON EL 5%
            String sqlPresu = "SELECT presupuesto FROM sucursal WHERE id_sucursal = 1";
            ResultSet rsPresu = con.prepareStatement(sqlPresu).executeQuery();

            double presupuestoActual = 0;
            if (rsPresu.next()) {
                presupuestoActual = rsPresu.getDouble("presupuesto");
            }

            if (presupuestoActual > 0) {
                double montoApertura = presupuestoActual * 0.05;

                // A. Restar de Bóveda (Sucursal)
                String sqlResta = "UPDATE sucursal SET presupuesto = presupuesto - ? WHERE id_sucursal = 1";
                PreparedStatement psResta = con.prepareStatement(sqlResta);
                psResta.setDouble(1, montoApertura);
                psResta.executeUpdate();

                // B. Sumar a la Caja Chica que esté abierta en este momento
                String sqlSuma = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_sucursal, estado, id_caja) "
                        + "VALUES ('APERTURA', ?, NOW(), 'Capital Inicial Diario (5% Bóveda)', 1, 'ACTIVO', ?)";
                PreparedStatement psSuma = con.prepareStatement(sqlSuma);
                psSuma.setDouble(1, montoApertura);
                psSuma.setInt(2, idCajaDestino);
                psSuma.executeUpdate();

                // Mensaje informativo actualizado
                JOptionPane.showMessageDialog(this, "<html><b>¡PRIMERA APERTURA DEL DÍA DETECTADA!</b><br>"
                        + "Fecha: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()) + "<br>"
                        + "Se ha transferido el 5% del presupuesto (S/ " + String.format("%.2f", montoApertura) + ")<br>"
                        + "a la Caja #" + idCajaDestino + " para iniciar operaciones del día.</html>");

                actualizarSaldos();
                cargarTablaMovimientos(); // Recargar tabla para ver el movimiento
            }
        } catch (SQLException e) {
            System.out.println("Error apertura auto: " + e);
            e.printStackTrace();
        }
    }

    private void actualizarSaldos() {
        try (Connection con = DriverManager.getConnection(url, usuario, password)) {

            // 1. CAJA GRANDE (Bóveda) - Lee directo de la tabla sucursal
            String sqlGrande = "SELECT presupuesto FROM sucursal WHERE id_sucursal = 1";
            ResultSet rsG = con.prepareStatement(sqlGrande).executeQuery();
            if (rsG.next()) {
                lblSaldoGrande.setText("S/ " + String.format("%.2f", rsG.getDouble("presupuesto")));
            }

            // 2. CAJA CHICA (El dinero del cajón)
            // CORRECCIÓN: Excluimos el tipo 'COMPRA' porque ese dinero sale de Bóveda, no del cajón.
            // Solo restamos: GASTOS (Luz, Agua...), PAGOS, RETIROS (Cierres)
            Caja cajaActual = new edu.UPAO.proyecto.DAO.CajaDAO().obtenerCajaAbierta(1);

            if (cajaActual != null) {
                String sqlChica = "SELECT "
                        + "SUM(CASE WHEN tipo IN ('APERTURA', 'VENTA', 'INGRESO', 'REPOSICION') THEN monto ELSE 0 END) - "
                        + "SUM(CASE WHEN tipo IN ('GASTO', 'PAGO', 'CIERRE', 'RETIRO') THEN monto ELSE 0 END) "
                        + // <--- OJO: Aquí quitamos 'COMPRA'
                        "as saldo_caja FROM movimiento_caja WHERE id_caja = ?";

                PreparedStatement psC = con.prepareStatement(sqlChica);
                psC.setInt(1, cajaActual.getIdCaja());
                ResultSet rsC = psC.executeQuery();

                double saldo = 0.0;
                if (rsC.next()) {
                    saldo = rsC.getDouble("saldo_caja");
                }

                lblSaldoChica.setText("S/ " + String.format("%.2f", saldo));
            } else {
                lblSaldoChica.setText("CERRADA");
            }

            // 3. TOTAL DE HOY (Solo Ventas)
            String sqlVentas = "SELECT SUM(monto) FROM movimiento_caja WHERE tipo = 'VENTA' AND DATE(fecha_hora) = CURDATE() AND id_sucursal = 1";
            ResultSet rsV = con.prepareStatement(sqlVentas).executeQuery();
            if (rsV.next()) {
                lblTotalHoy.setText("S/ " + String.format("%.2f", rsV.getDouble(1)));
            }

        } catch (SQLException e) {
            System.out.println("Error saldos: " + e);
        }
    }

    private void cargarTablaMovimientos() {
        DefaultTableModel modelo = (DefaultTableModel) tblMovimientosCaja.getModel();
        modelo.setRowCount(0);
        modelo.setColumnIdentifiers(new Object[]{"HORA", "TIPO", "DESCRIPCIÓN", "MONTO"});

        Caja cajaActual = new edu.UPAO.proyecto.DAO.CajaDAO().obtenerCajaAbierta(1);
        if (cajaActual == null) {
            return; // Si está cerrada no mostramos nada reciente
        }
        String sql = "SELECT fecha_hora, tipo, descripcion, monto FROM movimiento_caja WHERE id_caja = ? ORDER BY fecha_hora DESC";

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, cajaActual.getIdCaja());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String hora = new SimpleDateFormat("HH:mm").format(rs.getTimestamp("fecha_hora"));
                modelo.addRow(new Object[]{hora, rs.getString("tipo"), rs.getString("descripcion"), "S/ " + rs.getDouble("monto")});
            }
        } catch (SQLException e) {
        }
    }

    private void exportarPDF(JTable tabla, String tituloReporte) {
        // 1. Configurar el selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        // Filtro para que solo muestre PDFs
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));

        // Sugerir un nombre por defecto (Ej: Reporte_Ventas.pdf)
        fileChooser.setSelectedFile(new java.io.File("Reporte_" + System.currentTimeMillis() + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            // Obtener la ruta elegida por el usuario
            String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();

            // Asegurar que termine en .pdf
            if (!rutaArchivo.toLowerCase().endsWith(".pdf")) {
                rutaArchivo += ".pdf";
            }

            try {
                // 2. Crear el documento PDF
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
                document.open();

                // 3. Agregar Título
                com.itextpdf.text.Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                Paragraph titulo = new Paragraph(tituloReporte, fuenteTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                titulo.setSpacingAfter(20);
                document.add(titulo);

                // 4. Agregar Tabla
                PdfPTable tablePDF = new PdfPTable(tabla.getColumnCount());
                tablePDF.setWidthPercentage(100); // Ancho completo

                // A. Encabezados
                for (int i = 0; i < tabla.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(tabla.getColumnName(i)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                    cell.setPadding(8);
                    tablePDF.addCell(cell);
                }

                // B. Datos
                for (int i = 0; i < tabla.getRowCount(); i++) {
                    for (int j = 0; j < tabla.getColumnCount(); j++) {
                        Object valor = tabla.getValueAt(i, j);
                        String texto = (valor == null) ? "" : valor.toString();

                        PdfPCell cell = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 10)));
                        cell.setPadding(5);
                        // Alinear a la derecha si es dinero
                        if (texto.contains("S/") || texto.contains("$")) {
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        }
                        tablePDF.addCell(cell);
                    }
                }

                document.add(tablePDF);
                document.close();

                // 5. MENSAJE DE ÉXITO CON LA RUTA
                JOptionPane.showMessageDialog(this,
                        "¡Reporte generado exitosamente!\n\nGuardado en:\n" + rutaArchivo,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    //-------------------------------------------------------------------------
    // --- MÉTODOS DE DISEÑO (COPIAR AL FINAL DE LA CLASE) ---

    private void aplicarEstiloModerno() {
        // 1. Colores Generales del Tema
        Color colorFondo = new Color(245, 247, 251); // Gris azulado muy suave
        this.setBackground(colorFondo);

        // Estilo del TabbedPane (Pestañas)
        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jTabbedPane1.setBackground(Color.WHITE);

        // 2. Unificar Fondos de las Pestañas
        jPanel1.setBackground(colorFondo); // Ventas
        jPanel2.setBackground(colorFondo); // Gastos
        jPanel3.setBackground(colorFondo); // Facturas
        jPanel4.setBackground(colorFondo); // Gestión Caja

        // 3. Estilizar Tarjetas KPI (Los cuadros de colores de arriba)
        // Pestaña Ventas
        estilizarKPI(jPanel5, new Color(46, 125, 50), new Color(232, 245, 233)); // Ingresos (Verde)
        estilizarKPI(jPanel6, new Color(239, 108, 0), new Color(255, 243, 224)); // Ticket (Naranja)
        estilizarDateChooser(dcFecha);

        // Pestaña Gastos
        estilizarKPI(jPanel7, new Color(198, 40, 40), new Color(255, 235, 238)); // Gastos Totales (Rojo)
        estilizarKPI(jPanel8, new Color(230, 81, 0), new Color(255, 248, 225)); // Compras (Naranja suave)
        estilizarKPI(jPanel9, new Color(0, 121, 107), new Color(224, 242, 241)); // Salarios (Teal)

        // 4. Estilizar Panel de Gestión de Caja (El cuadro verde grande)
        // Lo convertimos en una "Tarjeta de Control" blanca y limpia
        jPanel10.setBackground(Color.WHITE);
        jPanel10.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220)),
                javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Corregir colores de etiquetas dentro del Panel de Caja (porque antes eran blancas sobre fondo verde)
        fixLabelsCaja(jPanel10);

        // 5. Estilizar Tablas (Card Style)
        estilizarTabla(tblReporteVentas, jScrollPane1);
        estilizarTabla(tblReporteGastos, jScrollPane2);
        estilizarTabla(tblListadoFacturas, jScrollPane3);
        estilizarTabla(tblMovimientosCaja, jScrollPane4);

        // 6. Estilizar Botones
        estilizarBoton(jButton1, new Color(0, 153, 102)); // Exportar PDF (Verde)
        estilizarBoton(btnPasarGrande, new Color(255, 102, 0)); // Naranja
        estilizarBoton(btnPasarChica, new Color(2, 119, 189));  // Azul
        estilizarBoton(btnGenerarPago, new Color(220, 53, 69)); // Rojo (Gasto)

        // 7. Estilizar Inputs y Combos
        estilizarInput(txtMontoTransferencia);
        estilizarCombo(cboFiltroFacturas);

        // 8. Títulos
        estilizarTitulo(jLabel1); // "Detalle ingresos"
        estilizarTitulo(jLabel4); // "Registro Gastos"
        estilizarTitulo(jLabel6); // "Listado Facturas"
        estilizarTitulo(jLabel10); // "Detalles Caja"
    }

    // Transforma los paneles sólidos en tarjetas modernas con borde inferior de color
    private void estilizarKPI(JPanel panel, Color colorBorde, Color colorFondo) {
        panel.setBackground(colorFondo);
        panel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 4, 0, colorBorde));

        for (java.awt.Component comp : panel.getComponents()) {
            if (comp instanceof javax.swing.JLabel) {
                javax.swing.JLabel lbl = (javax.swing.JLabel) comp;
                // Títulos en el color del tema, números en gris oscuro
                if (lbl.getFont().getSize() < 15) { // Asumimos que fuente pequeña es título
                    lbl.setForeground(colorBorde);
                    lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
                } else {
                    lbl.setForeground(Color.DARK_GRAY);
                    lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
                }
            }
        }
    }

    private void estilizarTabla(JTable tabla, javax.swing.JScrollPane scroll) {
        tabla.setRowHeight(35);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tabla.setSelectionBackground(new Color(232, 240, 254));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setBackground(Color.WHITE);

        javax.swing.table.JTableHeader header = tabla.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 100, 100));
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBackground(Color.WHITE);
        scroll.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0),
                javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220))
        ));
    }

    private void fixLabelsCaja(JPanel panel) {
        for (java.awt.Component comp : panel.getComponents()) {
            if (comp instanceof javax.swing.JLabel) {
                javax.swing.JLabel lbl = (javax.swing.JLabel) comp;
                lbl.setForeground(new Color(50, 50, 50)); // Gris oscuro en vez de blanco
                if (lbl.getText().contains("s/")) {
                    lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
                    lbl.setForeground(new Color(0, 100, 0)); // Verde oscuro para dinero
                }
            }
        }
    }

    private void estilizarBoton(javax.swing.JButton btn, Color colorFondo) {
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void estilizarInput(javax.swing.JTextField txt) {
        txt.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        txt.setBackground(Color.WHITE);
        txt.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200)),
                javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void estilizarCombo(javax.swing.JComboBox cbo) {
        cbo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        cbo.setBackground(Color.WHITE);
    }

    private void estilizarTitulo(javax.swing.JLabel lbl) {
        lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lbl.setForeground(new Color(60, 60, 60)); // Gris oscuro profesional
    }

    //--------------------- SI EL CALENDARIO NO FUNCIO SOLO BORRA ESTO--------------------------
    // 
    private void estilizarDateChooser(com.toedter.calendar.JDateChooser dc) {
        if (dc == null) {
            return;
        }

        // 1. Estilo del Contenedor Principal
        dc.setBackground(Color.WHITE);
        dc.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));

        // 2. Estilo de la Caja de Texto Interna (Donde sale la fecha)
        // JDateChooser tiene un método getDateEditor() que nos da acceso al input
        javax.swing.JComponent editor = (javax.swing.JComponent) dc.getDateEditor().getUiComponent();

        if (editor instanceof javax.swing.JTextField) {
            javax.swing.JTextField txt = (javax.swing.JTextField) editor;
            txt.setBackground(Color.WHITE);
            txt.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
            txt.setForeground(Color.DARK_GRAY);

            // Borde moderno (Gris suave con padding)
            txt.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
        }

        // 3. Estilo del Botón del Calendario (El ícono pequeño)
        // Buscamos el botón dentro de los componentes del JDateChooser
        for (java.awt.Component comp : dc.getComponents()) {
            if (comp instanceof javax.swing.JButton) {
                javax.swing.JButton btn = (javax.swing.JButton) comp;

                // Le damos un fondo sutil o lo hacemos plano
                btn.setBackground(new Color(245, 247, 251));
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                btn.setContentAreaFilled(false); // Quita el relieve 3D feo
                btn.setFocusPainted(false);
                btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5)); // Espacio alrededor
            }
        }
    }
    //----------------------------------------------------------------------------------------------------------------------------
    //----------------- SU WEBADA DE DETALLES DE INGRESO POR VENTAS DE POR NO HACIA CUANDO HICE CLONE NANDA NOMAS DIGO------------
    //---------------------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        dcFecha = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReporteVentas = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblTotalIngresos = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblTicketPromedio = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lblGastosTotales = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lblComprasInventario = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        lblSalarios = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblReporteGastos = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        cboFiltroFacturas = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblListadoFacturas = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblSaldoGrande = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblSaldoChica = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblTotalHoy = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtMontoTransferencia = new javax.swing.JTextField();
        btnPasarGrande = new javax.swing.JButton();
        btnPasarChica = new javax.swing.JButton();
        btnGenerarPago = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblMovimientosCaja = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 102, 0));

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));

        tblReporteVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblReporteVentas);

        jLabel1.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
        jLabel1.setText("Detalle de ingresos por ventas ");

        jPanel5.setBackground(new java.awt.Color(0, 153, 0));

        jLabel2.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("INGRESO");

        lblTotalIngresos.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblTotalIngresos.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalIngresos.setText("$000");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(jLabel2)
                .addContainerGap(73, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotalIngresos)
                .addGap(96, 96, 96))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(lblTotalIngresos)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 102, 0));

        jLabel3.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("TICKET PROMEDIO");

        lblTicketPromedio.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblTicketPromedio.setForeground(new java.awt.Color(255, 255, 255));
        lblTicketPromedio.setText("000");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(lblTicketPromedio)
                        .addGap(95, 95, 95))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(lblTicketPromedio)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(dcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 780, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(dcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(67, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("REPORTE DE VENTAS", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 204));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(255, 51, 51));

        jLabel5.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("GASTOS TOTALES");

        lblGastosTotales.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblGastosTotales.setForeground(new java.awt.Color(255, 255, 255));
        lblGastosTotales.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGastosTotales.setText("$000");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblGastosTotales, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jLabel5)
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblGastosTotales)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Arial Black", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 102, 102));
        jLabel4.setText("REGISTRO DE GASTOS Y SALIDAS DE DINERO");

        jPanel8.setBackground(new java.awt.Color(255, 102, 0));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("COMPRAS INVENTARIO");

        lblComprasInventario.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblComprasInventario.setForeground(new java.awt.Color(255, 255, 255));
        lblComprasInventario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblComprasInventario.setText("$000");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel7)
                .addContainerGap(37, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblComprasInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblComprasInventario)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(0, 153, 153));

        jLabel9.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("SALARIOS");

        lblSalarios.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblSalarios.setForeground(new java.awt.Color(255, 255, 255));
        lblSalarios.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSalarios.setText("$000");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSalarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(95, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(82, 82, 82))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblSalarios)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        tblReporteGastos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblReporteGastos);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addGap(76, 76, 76))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(206, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("REPORTE DE GASTOS", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        cboFiltroFacturas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboFiltroFacturas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFiltroFacturasActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial Black", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 102, 102));
        jLabel6.setText("LISTADO DE FACTURAS");

        tblListadoFacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tblListadoFacturas);

        jButton1.setBackground(new java.awt.Color(0, 153, 102));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Exportar a PDF");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cboFiltroFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1078, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(cboFiltroFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        jTabbedPane1.addTab("FACTURAS", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 204));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        jPanel10.setBackground(new java.awt.Color(204, 255, 204));
        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel8.setText("Caja grande:");

        lblSaldoGrande.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblSaldoGrande.setText("s/ 000");

        jLabel11.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel11.setText("Caja chica:");

        lblSaldoChica.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblSaldoChica.setText("s/ 000");

        jLabel13.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel13.setText("Total de hoy:");

        lblTotalHoy.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblTotalHoy.setText("s/ 000");

        jLabel15.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel15.setText("Monto del dia a pasar:");

        txtMontoTransferencia.setBackground(new java.awt.Color(204, 204, 204));
        txtMontoTransferencia.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        txtMontoTransferencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoTransferenciaActionPerformed(evt);
            }
        });

        btnPasarGrande.setBackground(new java.awt.Color(255, 102, 0));
        btnPasarGrande.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnPasarGrande.setText("CAJA GRANDE");
        btnPasarGrande.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasarGrandeActionPerformed(evt);
            }
        });

        btnPasarChica.setBackground(new java.awt.Color(0, 102, 153));
        btnPasarChica.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnPasarChica.setText(" CAJA CHICA");
        btnPasarChica.setActionCommand("");
        btnPasarChica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasarChicaActionPerformed(evt);
            }
        });

        btnGenerarPago.setBackground(new java.awt.Color(0, 102, 51));
        btnGenerarPago.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnGenerarPago.setText("GENERAR PAGO");
        btnGenerarPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarPagoActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Arial Black", 3, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 102, 102));
        jLabel10.setText("DETALLES DE CAJA ");

        jLabel12.setFont(new java.awt.Font("Arial", 3, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Pasar a : ");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(btnPasarGrande, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnPasarChica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel15))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblSaldoGrande, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblSaldoChica, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTotalHoy, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMontoTransferencia, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(btnGenerarPago, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jLabel10)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel10)
                .addGap(35, 35, 35)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblSaldoGrande))
                .addGap(25, 25, 25)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(lblSaldoChica))
                .addGap(29, 29, 29)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(lblTotalHoy))
                .addGap(22, 22, 22)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtMontoTransferencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPasarGrande, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPasarChica, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(btnGenerarPago, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );

        tblMovimientosCaja.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(tblMovimientosCaja);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4))
                .addContainerGap(61, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("GESTION DE CAJA", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("VENTAS");
    }// </editor-fold>//GEN-END:initComponents

    private void btnPasarGrandeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasarGrandeActionPerformed
        // 1. BUSCAR LA CAJA ACTUAL (Usando la instancia correcta)
        Caja cajaActual = new edu.UPAO.proyecto.DAO.CajaDAO().obtenerCajaAbierta(1);

        if (cajaActual == null) {
            JOptionPane.showMessageDialog(this, "No hay caja abierta para cerrar.");
            return;
        }

        int idCaja = cajaActual.getIdCaja();
        double saldoDelDia = 0.0;

        // 2. CALCULAR CUÁNTO DINERO HAY REALMENTE
        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            String sqlSaldo = "SELECT "
                    + "SUM(CASE WHEN tipo IN ('APERTURA', 'VENTA', 'INGRESO', 'REPOSICION') THEN monto ELSE 0 END) - "
                    + "SUM(CASE WHEN tipo IN ('GASTO', 'PAGO', 'COMPRA', 'CIERRE', 'RETIRO') THEN monto ELSE 0 END) "
                    + "as saldo_actual FROM movimiento_caja WHERE id_caja = ?";

            PreparedStatement ps = con.prepareStatement(sqlSaldo);
            ps.setInt(1, idCaja);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                saldoDelDia = rs.getDouble("saldo_actual");
            }

            if (saldoDelDia <= 0) {
                JOptionPane.showMessageDialog(this, "No hay dinero en caja para transferir.");
                return;
            }

            // 3. LA ALERTA (Lo que pediste)
            int confirm = JOptionPane.showConfirmDialog(this,
                    "<html>El saldo acumulado del día es: <font color='blue' size='5'><b>S/ " + String.format("%.2f", saldoDelDia) + "</b></font><br><br>"
                    + "¿Desea mover este dinero a la <b>Bóveda (Caja Grande)</b> y cerrar el turno?</html>",
                    "Transferencia a Bóveda", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // 4. EJECUTAR EL MOVIMIENTO
            // A. Sacar de Caja Chica (Registrar SALIDA)
            String sqlRetiro = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_sucursal, estado, id_caja) VALUES ('CIERRE', ?, NOW(), 'Transferencia de Ganancia del Día a Bóveda', 1, 'ACTIVO', ?)";
            PreparedStatement psRet = con.prepareStatement(sqlRetiro);
            psRet.setDouble(1, saldoDelDia);
            psRet.setInt(2, idCaja);
            psRet.executeUpdate();

            // B. Meter en Bóveda (Sumar Presupuesto)
            String sqlBoveda = "UPDATE sucursal SET presupuesto = presupuesto + ? WHERE id_sucursal = 1";
            PreparedStatement psUp = con.prepareStatement(sqlBoveda);
            psUp.setDouble(1, saldoDelDia);
            psUp.executeUpdate();

            // C. Cerrar la Caja en la BD (Para que mañana se cree una nueva)
            String sqlCerrar = "UPDATE caja SET fecha_hora_cierre = NOW(), saldo_final = ?, estado = 'CERRADA' WHERE id_caja = ?";
            PreparedStatement psClose = con.prepareStatement(sqlCerrar);
            psClose.setDouble(1, saldoDelDia);
            psClose.setInt(2, idCaja);
            psClose.executeUpdate();

            JOptionPane.showMessageDialog(this, "¡Transferencia Exitosa!\nEl dinero está seguro en la Bóveda.");

            // Actualizar pantalla
            lblSaldoChica.setText("CERRADA");
            lblSaldoChica.setForeground(java.awt.Color.RED);
            actualizarSaldos();
            cargarTablaMovimientos();

        } catch (SQLException e) {
            System.out.println("Error al transferir: " + e);
        }
    }//GEN-LAST:event_btnPasarGrandeActionPerformed

    private void btnPasarChicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasarChicaActionPerformed
        Caja cajaActual = new edu.UPAO.proyecto.DAO.CajaDAO().obtenerCajaAbierta(1);
        if (cajaActual == null) {
            JOptionPane.showMessageDialog(this, "No hay caja abierta para recibir el dinero.");
            return;
        }

        try {
            double monto = Double.parseDouble(txtMontoTransferencia.getText());
            if (monto <= 0) {
                return;
            }

            try (Connection con = DriverManager.getConnection(url, usuario, password)) {
                // 1. Restar de Bóveda
                String sqlRestarGrande = "UPDATE sucursal SET presupuesto = presupuesto - ? WHERE id_sucursal = 1";
                PreparedStatement psUp = con.prepareStatement(sqlRestarGrande);
                psUp.setDouble(1, monto);
                psUp.executeUpdate();

                // 2. Sumar a Caja Chica (REPOSICION)
                String sqlIngreso = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_sucursal, estado, id_caja) VALUES ('REPOSICION', ?, NOW(), 'Reposición Manual desde Bóveda', 1, 'ACTIVO', ?)";
                PreparedStatement psIng = con.prepareStatement(sqlIngreso);
                psIng.setDouble(1, monto);
                psIng.setInt(2, cajaActual.getIdCaja()); // ID Real
                psIng.executeUpdate();

                JOptionPane.showMessageDialog(this, "Efectivo agregado a Caja Chica.");
                txtMontoTransferencia.setText("");
                actualizarSaldos();
                cargarTablaMovimientos();
                cargarListadoFacturas();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Verifique el monto.");
        }
    }//GEN-LAST:event_btnPasarChicaActionPerformed

    private void btnGenerarPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarPagoActionPerformed
        Caja cajaActual = new edu.UPAO.proyecto.DAO.CajaDAO().obtenerCajaAbierta(1);
        if (cajaActual == null) {
            JOptionPane.showMessageDialog(this, "No hay caja abierta para registrar el pago.");
            return;
        }

        try {
            double monto = Double.parseDouble(txtMontoTransferencia.getText());
            if (monto <= 0) {
                return;
            }

            String motivo = JOptionPane.showInputDialog("Motivo del pago/gasto:");
            if (motivo == null || motivo.isEmpty()) {
                return;
            }

            try (Connection con = DriverManager.getConnection(url, usuario, password)) {
                String sqlGasto = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_sucursal, estado, id_caja) VALUES ('GASTO', ?, NOW(), ?, 1, 'ACTIVO', ?)";
                PreparedStatement psG = con.prepareStatement(sqlGasto);
                psG.setDouble(1, monto);
                psG.setString(2, motivo);
                psG.setInt(3, cajaActual.getIdCaja()); // ID Real
                psG.executeUpdate();

                JOptionPane.showMessageDialog(this, "Pago registrado correctamente.");
                txtMontoTransferencia.setText("");
                actualizarSaldos();
                cargarTablaMovimientos();
                cargarListadoFacturas();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Verifique el monto.");
        }
    }//GEN-LAST:event_btnGenerarPagoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        exportarPDF(tblListadoFacturas, "Reporte de Movimientos de Caja");        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cboFiltroFacturasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFiltroFacturasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboFiltroFacturasActionPerformed

    private void txtMontoTransferenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMontoTransferenciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMontoTransferenciaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerarPago;
    private javax.swing.JButton btnPasarChica;
    private javax.swing.JButton btnPasarGrande;
    private javax.swing.JComboBox<String> cboFiltroFacturas;
    private com.toedter.calendar.JDateChooser dcFecha;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblComprasInventario;
    private javax.swing.JLabel lblGastosTotales;
    private javax.swing.JLabel lblSalarios;
    private javax.swing.JLabel lblSaldoChica;
    private javax.swing.JLabel lblSaldoGrande;
    private javax.swing.JLabel lblTicketPromedio;
    private javax.swing.JLabel lblTotalHoy;
    private javax.swing.JLabel lblTotalIngresos;
    private javax.swing.JTable tblListadoFacturas;
    private javax.swing.JTable tblMovimientosCaja;
    private javax.swing.JTable tblReporteGastos;
    private javax.swing.JTable tblReporteVentas;
    private javax.swing.JTextField txtMontoTransferencia;
    // End of variables declaration//GEN-END:variables
}

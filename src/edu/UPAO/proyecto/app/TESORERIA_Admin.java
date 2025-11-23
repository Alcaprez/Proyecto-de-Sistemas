package edu.UPAO.proyecto.app;

import java.awt.Color;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

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
        modelo.setColumnIdentifiers(new Object[]{"FECHA", "HORA", "CONCEPTO", "ENTRADA", "SALIDA", "SALDO"});

        tblListadoFacturas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblListadoFacturas.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblListadoFacturas.getColumnModel().getColumn(2).setPreferredWidth(350);

        double saldoAcumulado = 0.0;

        String url = "jdbc:mysql://crossover.proxy.rlwy.net:17752/railway";
        String usuario = "root";
        String password = "wASzoGLiXaNsbdZbBQKwzjvJFcdoMTaU";

        // --- CONSULTA MAESTRA: CAJA + INVENTARIO (PRECIO REAL) ---
        String sql = "SELECT * FROM ("
                + // 1. MOVIMIENTOS DE CAJA (Excluyendo 'COMPRA' para no duplicar con inventario)
                "   SELECT fecha_hora, descripcion, tipo, monto, id_sucursal "
                + "   FROM movimiento_caja "
                + "   WHERE tipo NOT IN ('COMPRA') "
                + "   UNION ALL "
                + // 2. ENTRADAS DE INVENTARIO (Representan SALIDA de dinero - COMPRA)
                "   SELECT mi.fecha_hora, "
                + "          CONCAT('COMPRA STOCK: ', p.nombre, ' (', mi.cantidad, ' unds)') as descripcion, "
                + "          'GASTO_INVENTARIO' as tipo, "
                + "          (mi.cantidad * p.precio_compra) as monto, "
                + // Calculamos el costo
                "          mi.id_sucursal "
                + "   FROM movimiento_inventario mi "
                + "   INNER JOIN producto p ON mi.id_producto = p.id_producto "
                + "   WHERE mi.tipo = 'ENTRADA COMPRA' "
                + "   UNION ALL "
                + // 3. SALIDAS DE INVENTARIO POR ANULACIÓN (Representan ENTRADA de dinero - REEMBOLSO)
                "   SELECT mi.fecha_hora, "
                + "          CONCAT('DEVOLUCION PROV: ', p.nombre, ' (', mi.cantidad, ' unds)') as descripcion, "
                + "          'INGRESO_INVENTARIO' as tipo, "
                + "          (mi.cantidad * p.precio_compra) as monto, "
                + // Recuperamos el costo
                "          mi.id_sucursal "
                + "   FROM movimiento_inventario mi "
                + "   INNER JOIN producto p ON mi.id_producto = p.id_producto "
                + "   WHERE mi.tipo = 'SALIDA POR ANULACION' "
                + ") AS libro_caja "
                + "WHERE id_sucursal = 1 ";

        // Filtros del ComboBox
        try {
            String filtro = cboFiltroFacturas.getSelectedItem().toString();
            if (filtro.equalsIgnoreCase("INGRESOS")) {
                // Incluimos Ventas, Ingresos varios y Devoluciones de inventario (recupero plata)
                sql += " AND (tipo IN ('VENTA', 'INGRESO', 'APERTURA') OR tipo = 'INGRESO_INVENTARIO') ";
            } else if (filtro.equalsIgnoreCase("EGRESOS")) {
                // Incluimos Gastos, Pagos y Compras de inventario (gasto plata)
                sql += " AND (tipo IN ('GASTO', 'SALIDA', 'PAGO', 'DEVOLUCION') OR tipo = 'GASTO_INVENTARIO') ";
            }
        } catch (Exception e) {
        }

        sql += " ORDER BY fecha_hora ASC"; // Orden cronológico para calcular saldo correctamente

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

                double entrada = 0.0;
                double salida = 0.0;

                // --- LÓGICA DE SALDO ---
                // ¿Qué suma dinero a la caja? (Ventas, Ingresos Manuales, Devoluciones al proveedor)
                if (tipo.contains("VENTA") || tipo.contains("INGRESO") || tipo.contains("APERTURA")) {
                    entrada = monto;
                    saldoAcumulado += monto;
                } // ¿Qué resta dinero? (Gastos, Pagos, Compras de mercadería)
                else {
                    salida = monto;
                    saldoAcumulado -= monto;
                }

                modelo.addRow(new Object[]{
                    fecha,
                    hora,
                    desc,
                    (entrada > 0) ? String.format("%.2f", entrada) : "0.00",
                    (salida > 0) ? String.format("%.2f", salida) : "0.00",
                    String.format("%.2f", saldoAcumulado)
                });
            }
        } catch (SQLException e) {
            System.out.println("Error listado facturas: " + e);
        }
    }

    private void verificarAperturaAutomatica() {
        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            // A. ¿Ya existe una APERTURA hoy?
            String sqlCheck = "SELECT COUNT(*) FROM movimiento_caja WHERE tipo = 'APERTURA' AND DATE(fecha_hora) = CURDATE() AND id_sucursal = 1";
            PreparedStatement psCheck = con.prepareStatement(sqlCheck);
            ResultSet rsCheck = psCheck.executeQuery();

            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                // Ya se abrió hoy, no hacemos nada.
                return;
            }

            // B. Si no se ha abierto, procedemos con el 5%
            // 1. Consultar Presupuesto Actual (Caja Grande)
            String sqlPresu = "SELECT presupuesto FROM sucursal WHERE id_sucursal = 1";
            PreparedStatement psPresu = con.prepareStatement(sqlPresu);
            ResultSet rsPresu = psPresu.executeQuery();

            double presupuestoActual = 0;
            if (rsPresu.next()) {
                presupuestoActual = rsPresu.getDouble("presupuesto");
            }

            if (presupuestoActual <= 0) {
                return; // No hay dinero para sacar
            }
            // 2. Calcular el 5%
            double montoApertura = presupuestoActual * 0.05;

            // 3. TRANSACCIÓN: Restar de Grande -> Sumar a Chica
            String sqlResta = "UPDATE sucursal SET presupuesto = presupuesto - ? WHERE id_sucursal = 1";
            PreparedStatement psResta = con.prepareStatement(sqlResta);
            psResta.setDouble(1, montoApertura);
            psResta.executeUpdate();

            String sqlSuma = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_sucursal, estado) VALUES ('APERTURA', ?, NOW(), 'Apertura Automática (5% Bóveda)', 1, 'ACTIVO')";
            PreparedStatement psSuma = con.prepareStatement(sqlSuma);
            psSuma.setDouble(1, montoApertura);
            psSuma.executeUpdate();

            JOptionPane.showMessageDialog(this, "<html><b>¡CAJA ABIERTA AUTOMÁTICAMENTE!</b><br>"
                    + "Se ha transferido el 5% del presupuesto (S/ " + String.format("%.2f", montoApertura) + ")<br>"
                    + "para iniciar las operaciones del día.</html>");

        } catch (SQLException e) {
            System.out.println("Error en apertura automática: " + e);
        }
    }
    private void actualizarSaldos() {
        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            
            // 1. CAJA GRANDE (Presupuesto en BD)
            String sqlGrande = "SELECT presupuesto FROM sucursal WHERE id_sucursal = 1";
            ResultSet rsG = con.prepareStatement(sqlGrande).executeQuery();
            if (rsG.next()) {
                lblSaldoGrande.setText("S/ " + String.format("%.2f", rsG.getDouble("presupuesto")));
            }

            // 2. CAJA CHICA (Calculado: Entradas - Salidas de HOY)
            // Sumamos: APERTURA + VENTA + INGRESO + REPOSICION
            // Restamos: GASTO + PAGO + COMPRA + CIERRE + RETIRO
            String sqlChica = "SELECT " +
                              "SUM(CASE WHEN tipo IN ('APERTURA', 'VENTA', 'INGRESO', 'REPOSICION') THEN monto ELSE 0 END) - " +
                              "SUM(CASE WHEN tipo IN ('GASTO', 'PAGO', 'COMPRA', 'CIERRE', 'RETIRO') THEN monto ELSE 0 END) " +
                              "as saldo_dia FROM movimiento_caja WHERE DATE(fecha_hora) = CURDATE() AND id_sucursal = 1";
            
            ResultSet rsC = con.prepareStatement(sqlChica).executeQuery();
            double saldoChica = 0;
            if (rsC.next()) {
                saldoChica = rsC.getDouble("saldo_dia");
                lblSaldoChica.setText("S/ " + String.format("%.2f", saldoChica));
            }

            // 3. TOTAL DE HOY (Ventas Brutas)
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
        
        // Traemos solo lo de HOY
        String sql = "SELECT fecha_hora, tipo, descripcion, monto FROM movimiento_caja WHERE DATE(fecha_hora) = CURDATE() AND id_sucursal = 1 ORDER BY fecha_hora DESC";

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {
            ResultSet rs = con.prepareStatement(sql).executeQuery();
            while (rs.next()) {
                String hora = new java.text.SimpleDateFormat("HH:mm").format(rs.getTimestamp("fecha_hora"));
                modelo.addRow(new Object[]{
                    hora,
                    rs.getString("tipo"),
                    rs.getString("descripcion"),
                    "S/ " + rs.getDouble("monto")
                });
            }
        } catch (SQLException e) {}
    }
    
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
        jScrollPane4 = new javax.swing.JScrollPane();
        tblMovimientosCaja = new javax.swing.JTable();

        jTabbedPane1.setBackground(new java.awt.Color(204, 0, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

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

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Detalle de ingresos por ventas ");

        jPanel5.setBackground(new java.awt.Color(0, 153, 0));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel2.setText("INGRESO");

        lblTotalIngresos.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblTotalIngresos.setText("$000");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalIngresos)
                    .addComponent(jLabel2))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addGap(28, 28, 28)
                .addComponent(lblTotalIngresos)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(0, 153, 0));

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel3.setText("TICKET PROMEDIO");

        lblTicketPromedio.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblTicketPromedio.setText("000");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(lblTicketPromedio))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTicketPromedio)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 764, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dcFecha, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(dcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(176, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("REPORTE DE VENTAS", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(204, 204, 204));

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("GASTOS TOTALES");

        lblGastosTotales.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblGastosTotales.setForeground(new java.awt.Color(0, 0, 0));
        lblGastosTotales.setText("$000");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(lblGastosTotales)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(lblGastosTotales)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Panel de gastos y salidas de dinero");

        jPanel8.setBackground(new java.awt.Color(204, 204, 204));

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("COMPRAS INVENTARIO");

        lblComprasInventario.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblComprasInventario.setForeground(new java.awt.Color(0, 0, 0));
        lblComprasInventario.setText("$000");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(lblComprasInventario)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(lblComprasInventario)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(204, 204, 204));

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("SALARIOS");

        lblSalarios.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSalarios.setForeground(new java.awt.Color(0, 0, 0));
        lblSalarios.setText("$000");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblSalarios)
                .addGap(65, 65, 65))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel9)
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(lblSalarios)
                .addContainerGap(28, Short.MAX_VALUE))
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
                .addGap(318, 318, 318)
                .addComponent(jLabel4)
                .addContainerGap(325, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(169, 169, 169)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(76, 76, 76))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel4)
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        jTabbedPane1.addTab("REPORTE DE GASTOS", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        cboFiltroFacturas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Listado de facturas");

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

        jButton1.setText("Abrir PDF");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(431, 431, 431))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cboFiltroFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 822, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(127, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jLabel6)
                .addGap(38, 38, 38)
                .addComponent(cboFiltroFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(117, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("FACTURAS", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        jPanel10.setBackground(new java.awt.Color(0, 153, 255));

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Caja grande:");

        lblSaldoGrande.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSaldoGrande.setForeground(new java.awt.Color(0, 0, 0));
        lblSaldoGrande.setText("s/ 000");

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Caja chica:");

        lblSaldoChica.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblSaldoChica.setForeground(new java.awt.Color(255, 255, 255));
        lblSaldoChica.setText("s/ 000");

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("Total de hoy:");

        lblTotalHoy.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblTotalHoy.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalHoy.setText("s/ 000");

        jLabel15.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Monto del dia a pasar:");

        txtMontoTransferencia.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        txtMontoTransferencia.setText("jTextField1");

        btnPasarGrande.setText("PASAR A  CAJA GRANDE");
        btnPasarGrande.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasarGrandeActionPerformed(evt);
            }
        });

        btnPasarChica.setText("PASAR A CAJA CHICA");
        btnPasarChica.setActionCommand("");
        btnPasarChica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasarChicaActionPerformed(evt);
            }
        });

        btnGenerarPago.setText("GENERAR PAGO");
        btnGenerarPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarPagoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblSaldoChica, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                            .addComponent(lblSaldoGrande, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                            .addComponent(lblTotalHoy, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                            .addComponent(txtMontoTransferencia)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(btnPasarGrande)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPasarChica)))
                .addGap(15, 15, 15))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGenerarPago)
                .addGap(131, 131, 131))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblSaldoGrande))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(lblSaldoChica))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(lblTotalHoy))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtMontoTransferencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPasarChica)
                    .addComponent(btnPasarGrande))
                .addGap(33, 33, 33)
                .addComponent(btnGenerarPago)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(46, 46, 46)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE))
                .addContainerGap(106, Short.MAX_VALUE))
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
       try {
            double monto = Double.parseDouble(txtMontoTransferencia.getText());
            if (monto <= 0) return;

            // 1. Restar de Caja Chica (Insertar RETIRO)
            // ... aquí deberíamos validar si hay saldo en chica primero, pero asumiremos que el admin sabe ...
            
            Connection con = DriverManager.getConnection(url, usuario, password);
            
            String sqlRetiro = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_sucursal, estado) VALUES ('RETIRO', ?, NOW(), 'Transferencia a Bóveda', 1, 'ACTIVO')";
            PreparedStatement psRet = con.prepareStatement(sqlRetiro);
            psRet.setDouble(1, monto);
            psRet.executeUpdate();

            // 2. Sumar a Caja Grande (Update Sucursal)
            String sqlSumarGrande = "UPDATE sucursal SET presupuesto = presupuesto + ? WHERE id_sucursal = 1";
            PreparedStatement psUp = con.prepareStatement(sqlSumarGrande);
            psUp.setDouble(1, monto);
            psUp.executeUpdate();

            con.close();
            JOptionPane.showMessageDialog(this, "Dinero guardado en Bóveda exitosamente.");
            txtMontoTransferencia.setText("");
            actualizarSaldos();
            cargarTablaMovimientos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Verifique el monto.");
        }
    }//GEN-LAST:event_btnPasarGrandeActionPerformed

    private void btnPasarChicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasarChicaActionPerformed
       try {
            double monto = Double.parseDouble(txtMontoTransferencia.getText());
            if (monto <= 0) return;

            Connection con = DriverManager.getConnection(url, usuario, password);
            
            // 1. Restar de Caja Grande
            String sqlRestarGrande = "UPDATE sucursal SET presupuesto = presupuesto - ? WHERE id_sucursal = 1";
            PreparedStatement psUp = con.prepareStatement(sqlRestarGrande);
            psUp.setDouble(1, monto);
            psUp.executeUpdate();

            // 2. Sumar a Caja Chica (Insertar REPOSICION)
            String sqlIngreso = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_sucursal, estado) VALUES ('REPOSICION', ?, NOW(), 'Reposición Manual desde Bóveda', 1, 'ACTIVO')";
            PreparedStatement psIng = con.prepareStatement(sqlIngreso);
            psIng.setDouble(1, monto);
            psIng.executeUpdate();

            con.close();
            JOptionPane.showMessageDialog(this, "Efectivo agregado a Caja Chica.");
            txtMontoTransferencia.setText("");
            actualizarSaldos();
            cargarTablaMovimientos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Verifique el monto.");
        }
    }//GEN-LAST:event_btnPasarChicaActionPerformed

    private void btnGenerarPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarPagoActionPerformed
       try {
            double monto = Double.parseDouble(txtMontoTransferencia.getText());
            if (monto <= 0) return;
            
            String motivo = JOptionPane.showInputDialog("Motivo del pago/gasto:");
            if (motivo == null || motivo.isEmpty()) return;

            Connection con = DriverManager.getConnection(url, usuario, password);
            
            // Insertar GASTO (Resta automáticamente del saldo calculado de caja chica)
            String sqlGasto = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_sucursal, estado) VALUES ('GASTO', ?, NOW(), ?, 1, 'ACTIVO')";
            PreparedStatement psG = con.prepareStatement(sqlGasto);
            psG.setDouble(1, monto);
            psG.setString(2, motivo);
            psG.executeUpdate();

            con.close();
            JOptionPane.showMessageDialog(this, "Pago registrado correctamente.");
            txtMontoTransferencia.setText("");
            actualizarSaldos();
            cargarTablaMovimientos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Verifique el monto.");
        }
    }//GEN-LAST:event_btnGenerarPagoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerarPago;
    private javax.swing.JButton btnPasarChica;
    private javax.swing.JButton btnPasarGrande;
    private javax.swing.JComboBox<String> cboFiltroFacturas;
    private com.toedter.calendar.JDateChooser dcFecha;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
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

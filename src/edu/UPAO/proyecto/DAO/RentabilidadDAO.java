package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import java.sql.*;
import java.util.*;

public class RentabilidadDAO {

    private Connection conexion;
    private CompraDAO compraDAO;
    private MovimientoCajaDAO movimientoCajaDAO;

    public RentabilidadDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
            // ✅ INICIALIZAR LOS DAOs QUE FALTAN
            this.compraDAO = new CompraDAO();
            this.movimientoCajaDAO = new MovimientoCajaDAO();
            System.out.println("✅ RentabilidadDAO inicializado correctamente con todos los DAOs");
        } catch (Exception e) {
            System.err.println("❌ Error inicializando RentabilidadDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // CONSULTA CORREGIDA - VENTAS POR SUCURSAL
    public Map<String, Double> obtenerVentasPorSucursal(java.util.Date fechaInicio, java.util.Date fechaFin) {
        Map<String, Double> ventasPorSucursal = new LinkedHashMap<>();

        String sql = "SELECT s.nombre_sucursal, COALESCE(SUM(v.total), 0) as total_ventas "
                + "FROM sucursal s "
                + "LEFT JOIN venta v ON s.id_sucursal = v.id_sucursal "
                + "AND DATE(v.fecha_hora) BETWEEN DATE(?) AND DATE(?) "
                + "WHERE s.estado = 'ACTIVO' "
                + "GROUP BY s.id_sucursal, s.nombre_sucursal "
                + "ORDER BY s.id_sucursal";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String sucursal = rs.getString("nombre_sucursal");
                double ventas = rs.getDouble("total_ventas");
                ventasPorSucursal.put(sucursal, ventas);

                System.out.println("📊 Sucursal: " + sucursal + " - Ventas: " + ventas);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error en obtenerVentasPorSucursal: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("✅ Total sucursales con ventas: " + ventasPorSucursal.size());
        return ventasPorSucursal;
    }

    // MÉTODO PRINCIPAL CORREGIDO - CALCULAR RENTABILIDAD
    public Map<String, Double> calcularRentabilidadReal(java.util.Date fechaInicio, java.util.Date fechaFin, String sucursal) {
        Map<String, Double> resultados = new HashMap<>();

        try {
            // Obtener ID de sucursal si se especifica
            Integer idSucursal = null;
            if (sucursal != null && !sucursal.equals("TODAS")) {
                SucursalDAO sucursalDAO = new SucursalDAO();
                idSucursal = sucursalDAO.obtenerIdSucursalPorNombre(sucursal);
                if (idSucursal == -1) {
                    System.err.println("❌ Sucursal no encontrada: " + sucursal);
                    return resultados;
                }
            }

            // 1. CALCULAR INGRESOS TOTALES (VENTAS)
            double ingresosVentas = calcularIngresosTotales(fechaInicio, fechaFin, idSucursal);

            // 2. CALCULAR COSTOS TOTALES (COSTO DE PRODUCTOS VENDIDOS)
            double costosVentas = calcularCostosTotales(fechaInicio, fechaFin, idSucursal);

            // 3. CALCULAR GASTOS POR COMPRAS
            double gastosCompras = compraDAO.obtenerTotalCompras(fechaInicio, fechaFin, idSucursal);

            // 4. CALCULAR OTROS GASTOS E INGRESOS DE CAJA
            double otrosIngresos = movimientoCajaDAO.obtenerTotalMovimientosCaja(fechaInicio, fechaFin, idSucursal, "INGRESO");
            double otrosGastos = movimientoCajaDAO.obtenerTotalMovimientosCaja(fechaInicio, fechaFin, idSucursal, "GASTO");

            // 5. CALCULAR GANANCIAS
            double gananciaBruta = ingresosVentas - costosVentas;
            double gananciaOperativa = gananciaBruta - gastosCompras;
            double gananciaNeta = gananciaOperativa + otrosIngresos - otrosGastos;

            // Guardar resultados
            resultados.put("ingresos_totales", ingresosVentas);
            resultados.put("costos_totales", costosVentas);
            resultados.put("gastos_compras", gastosCompras);
            resultados.put("otros_ingresos", otrosIngresos);
            resultados.put("otros_gastos", otrosGastos);
            resultados.put("ganancia_bruta", gananciaBruta);
            resultados.put("ganancia_operativa", gananciaOperativa);
            resultados.put("ganancia_neta", gananciaNeta);

            System.out.println("💰 RESUMEN RENTABILIDAD COMPLETO:");
            System.out.println("   - Ingresos por ventas: " + ingresosVentas);
            System.out.println("   - Costos de ventas: " + costosVentas);
            System.out.println("   - Gastos por compras: " + gastosCompras);
            System.out.println("   - Otros ingresos: " + otrosIngresos);
            System.out.println("   - Otros gastos: " + otrosGastos);
            System.out.println("   - Ganancia Bruta: " + gananciaBruta);
            System.out.println("   - Ganancia Operativa: " + gananciaOperativa);
            System.out.println("   - Ganancia Neta: " + gananciaNeta);

        } catch (SQLException e) {
            System.err.println("❌ Error calculando rentabilidad: " + e.getMessage());
            e.printStackTrace();
        }

        return resultados;
    }

    // MÉTODO AUXILIAR - CALCULAR INGRESOS
    private double calcularIngresosTotales(java.util.Date fechaInicio, java.util.Date fechaFin, Integer idSucursal) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) as ingresos "
                + "FROM venta "
                + "WHERE DATE(fecha_hora) BETWEEN DATE(?) AND DATE(?)";

        if (idSucursal != null) {
            sql += " AND id_sucursal = ?";
        }

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            if (idSucursal != null) {
                stmt.setInt(3, idSucursal);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("ingresos");
            }
        }
        return 0.0;
    }

    // ÉTODO AUXILIAR - CALCULAR COSTOS
    private double calcularCostosTotales(java.util.Date fechaInicio, java.util.Date fechaFin, Integer idSucursal) throws SQLException {
        String sql = "SELECT COALESCE(SUM(dv.cantidad * p.precio_compra), 0) as costos "
                + "FROM detalle_venta dv "
                + "INNER JOIN venta v ON dv.id_venta = v.id_venta "
                + "INNER JOIN producto p ON dv.id_producto = p.id_producto "
                + "WHERE DATE(v.fecha_hora) BETWEEN DATE(?) AND DATE(?)";

        if (idSucursal != null) {
            sql += " AND v.id_sucursal = ?";
        }

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            if (idSucursal != null) {
                stmt.setInt(3, idSucursal);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("costos");
            }
        }
        return 0.0;
    }

    public List<Object[]> obtenerMovimientosFinancieros(java.util.Date fechaInicio, java.util.Date fechaFin, String sucursal) {
        List<Object[]> movimientos = new ArrayList<>();

        // ✅ CONSULTA CORREGIDA - INCLUIR MOVIMIENTOS DE CAJA
        String sql = "SELECT "
                + "DATE(mc.fecha_hora) as fecha, "
                + "s.nombre_sucursal, "
                + "CASE "
                + "  WHEN mc.tipo = 'VENTA' THEN CONCAT('VENTA - ', COALESCE((SELECT GROUP_CONCAT(p.nombre) FROM detalle_venta dv JOIN producto p ON dv.id_producto = p.id_producto WHERE dv.id_venta = mc.id_venta), 'Productos')) "
                + "  WHEN mc.tipo = 'COMPRA' THEN CONCAT('COMPRA - Proveedor: ', COALESCE((SELECT pr.dni FROM proveedor pr JOIN compra c ON pr.id_proveedor = c.id_proveedor WHERE c.id_compra = mc.id_compra), 'Proveedor')) "
                + "  ELSE mc.descripcion "
                + "END as descripcion, "
                + "COALESCE((SELECT SUM(dv.cantidad) FROM detalle_venta dv WHERE dv.id_venta = mc.id_venta), "
                + "        (SELECT SUM(dc.cantidad) FROM detalle_compra dc WHERE dc.id_compra = mc.id_compra), 0) as cantidad, "
                + "CASE WHEN mc.tipo = 'VENTA' THEN mc.monto ELSE 0 END as ingreso, "
                + "CASE WHEN mc.tipo IN ('COMPRA', 'GASTO') THEN mc.monto ELSE 0 END as costo, "
                + "CASE "
                + "  WHEN mc.tipo = 'VENTA' THEN mc.monto "
                + "  WHEN mc.tipo IN ('COMPRA', 'GASTO') THEN -mc.monto "
                + "  WHEN mc.tipo = 'INGRESO' THEN mc.monto "
                + "  ELSE 0 "
                + "END as ganancia "
                + "FROM movimiento_caja mc "
                + "INNER JOIN sucursal s ON mc.id_sucursal = s.id_sucursal "
                + "WHERE DATE(mc.fecha_hora) BETWEEN DATE(?) AND DATE(?) "
                + "AND (? = 'TODAS' OR s.nombre_sucursal = ?) "
                + "ORDER BY mc.fecha_hora DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            stmt.setString(3, sucursal);
            stmt.setString(4, sucursal);

            System.out.println("🔍 Ejecutando consulta de movimientos CORREGIDA...");
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                Object[] movimiento = {
                    rs.getDate("fecha"),
                    rs.getString("nombre_sucursal"),
                    limitarTexto(rs.getString("descripcion"), 50),
                    rs.getInt("cantidad"),
                    String.format("S/ %.2f", rs.getDouble("ingreso")),
                    String.format("S/ %.2f", rs.getDouble("costo")),
                    String.format("S/ %.2f", rs.getDouble("ganancia"))
                };
                movimientos.add(movimiento);
            }

            System.out.println("✅ Movimientos encontrados: " + count);

        } catch (SQLException e) {
            System.err.println("❌ Error en obtenerMovimientosFinancieros: " + e.getMessage());
            e.printStackTrace();
        }
        return movimientos;
    }

    // ✅ MÉTODO AUXILIAR - LIMITAR TEXTO
    private String limitarTexto(String texto, int maxLength) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= maxLength) {
            return texto;
        }
        return texto.substring(0, maxLength - 3) + "...";
    }

    private List<Object[]> obtenerMovimientosVentas(java.util.Date fechaInicio, java.util.Date fechaFin, String sucursal) {
        List<Object[]> movimientos = new ArrayList<>();

        String sql = "SELECT "
                + "DATE(v.fecha_hora) as fecha, "
                + "s.nombre_sucursal, "
                + "GROUP_CONCAT(p.nombre SEPARATOR ', ') as productos, "
                + "SUM(dv.cantidad) as cantidad_total, "
                + "SUM(dv.subtotal) as ingreso_total, "
                + "SUM(dv.cantidad * p.precio_compra) as costo_total, "
                + "SUM(dv.subtotal - (dv.cantidad * p.precio_compra)) as ganancia_total, "
                + "'VENTA' as tipo "
                + "FROM venta v "
                + "INNER JOIN detalle_venta dv ON v.id_venta = dv.id_venta "
                + "INNER JOIN producto p ON dv.id_producto = p.id_producto "
                + "INNER JOIN sucursal s ON v.id_sucursal = s.id_sucursal "
                + "WHERE DATE(v.fecha_hora) BETWEEN DATE(?) AND DATE(?) "
                + "AND (? = 'TODAS' OR s.nombre_sucursal = ?) "
                + "GROUP BY v.id_venta, DATE(v.fecha_hora), s.nombre_sucursal "
                + "ORDER BY v.fecha_hora DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            stmt.setString(3, sucursal);
            stmt.setString(4, sucursal);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] movimiento = {
                    rs.getDate("fecha"),
                    rs.getString("nombre_sucursal"),
                    limitarTexto(rs.getString("productos"), 50),
                    rs.getInt("cantidad_total"),
                    String.format("S/ %.2f", rs.getDouble("ingreso_total")),
                    String.format("S/ %.2f", rs.getDouble("costo_total")),
                    String.format("S/ %.2f", rs.getDouble("ganancia_total")),
                    rs.getString("tipo")
                };
                movimientos.add(movimiento);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo movimientos de ventas: " + e.getMessage());
            e.printStackTrace();
        }
        return movimientos;
    }

    // Mantener solo calcularRentabilidadReal
    public java.sql.Connection getConexion() {
        return this.conexion;
    }
}

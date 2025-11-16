package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Compra;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoCajaDAO {

    private Connection conexion;

    public MovimientoCajaDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
        } catch (Exception e) {
            System.err.println("❌ Error conectando MovimientoCajaDAO: " + e.getMessage());
        }
    }

    public boolean registrarMovimientoCajaVenta(double monto, int idVenta, int idSucursal, String metodoPago) {

        System.out.println("🎯 INICIANDO REGISTRO MOVIMIENTO CAJA:");
        System.out.println("   - MONTO RECIBIDO: " + monto);
        System.out.println("   - ID VENTA: " + idVenta);
        System.out.println("   - ID SUCURSAL: " + idSucursal);
        System.out.println("   - MÉTODO PAGO: " + metodoPago);

        // ✅ VERIFICAR QUE EL MONTO SEA VÁLIDO
        if (monto <= 0) {
            System.err.println("❌ ERROR: Monto inválido para movimiento de caja: " + monto);
            return false;
        }

        String sql = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_caja, id_venta, id_sucursal, estado) VALUES (?, ?, NOW(), ?, ?, ?, ?, 'ACTIVO')";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {

            // ✅ OBTENER CAJA ACTIVA
            int idCaja = obtenerIdCajaActiva();
            String descripcion = "VENTA #" + idVenta + " - " + metodoPago;

            // ✅ ESTABLECER PARÁMETROS
            stmt.setString(1, "VENTA");
            stmt.setDouble(2, monto);
            stmt.setString(3, descripcion);
            stmt.setInt(4, idCaja);
            stmt.setInt(5, idVenta);
            stmt.setInt(6, idSucursal);

            // ✅ EJECUTAR INSERCIÓN
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("✅ MOVIMIENTO CAJA REGISTRADO EXITOSAMENTE");
                System.out.println("   - Monto registrado: S/ " + monto);

                // ✅ VERIFICAR QUE SE REGISTRÓ CORRECTAMENTE
                verificarMovimientoRegistrado(idVenta, monto);
                return true;
            } else {
                System.err.println("❌ No se pudo insertar movimiento de caja");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error SQL registrando movimiento de caja: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

// ✅ MÉTODO PARA VERIFICAR QUE EL MOVIMIENTO SE REGISTRÓ CORRECTAMENTE
    private void verificarMovimientoRegistrado(int idVenta, double montoEsperado) {
        try {
            String sql = "SELECT monto FROM movimiento_caja WHERE id_venta = ? ORDER BY id_movimiento_caja DESC LIMIT 1";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idVenta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double montoRegistrado = rs.getDouble("monto");
                System.out.println("🔍 VERIFICACIÓN MOVIMIENTO CAJA:");
                System.out.println("   - Monto esperado: " + montoEsperado);
                System.out.println("   - Monto registrado: " + montoRegistrado);

                if (Math.abs(montoEsperado - montoRegistrado) > 0.01) {
                    System.err.println("❌ DISCREPANCIA CRÍTICA: Monto registrado difiere del esperado!");
                } else {
                    System.out.println("✅ Monto verificado correctamente");
                }
            } else {
                System.err.println("❌ No se pudo verificar movimiento registrado");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error verificando movimiento: " + e.getMessage());
        }
    }

    // ✅ REGISTRAR MOVIMIENTO DE CAJA POR COMPRA
    public boolean registrarMovimientoCajaCompra(Compra compra, int idCompra, String descripcion) {
        String sql = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_caja, id_compra, id_sucursal, estado) VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVO')";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            int idCaja = obtenerIdCajaActiva();

            stmt.setString(1, "COMPRA");
            stmt.setDouble(2, compra.getTotal());
            stmt.setTimestamp(3, new Timestamp(compra.getFechaHora().getTime()));
            stmt.setString(4, descripcion);
            stmt.setInt(5, idCaja);
            stmt.setInt(6, idCompra);
            stmt.setInt(7, compra.getIdSucursal());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("💰 Movimiento de caja por COMPRA registrado: S/ " + compra.getTotal());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error registrando movimiento de caja por compra: " + e.getMessage());
        }
        return false;
    }

    // ✅ REGISTRAR MOVIMIENTO DE CAJA GENÉRICO (INGRESO/GASTO)
    public boolean registrarMovimientoCaja(String tipo, double monto, String descripcion, int idSucursal) {
        String sql = "INSERT INTO movimiento_caja (tipo, monto, fecha_hora, descripcion, id_caja, id_sucursal, estado) VALUES (?, ?, NOW(), ?, ?, ?, 'ACTIVO')";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            int idCaja = obtenerIdCajaActiva();

            stmt.setString(1, tipo);
            stmt.setDouble(2, monto);
            stmt.setString(3, descripcion);
            stmt.setInt(4, idCaja);
            stmt.setInt(5, idSucursal);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("💰 Movimiento de caja registrado: " + tipo + " - S/ " + monto);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error registrando movimiento de caja: " + e.getMessage());
        }
        return false;
    }

    // ✅ OBTENER ID DE CAJA ACTIVA
    private int obtenerIdCajaActiva() throws SQLException {
        String sql = "SELECT id_caja FROM caja WHERE estado = 'ABIERTA' LIMIT 1";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int idCaja = rs.getInt("id_caja");
                System.out.println("✅ Caja activa encontrada: " + idCaja);
                return idCaja;
            } else {
                // Si no hay caja activa, usar la primera caja disponible
                System.out.println("⚠️ No hay caja activa, usando caja por defecto");
                return 1;
            }
        }
    }

    // ✅ MÉTODOS PARA RENTABILIDAD
    public double obtenerTotalMovimientosCaja(java.util.Date fechaInicio, java.util.Date fechaFin, Integer idSucursal, String tipo) {
        String sql = "SELECT COALESCE(SUM(monto), 0) as total FROM movimiento_caja "
                + "WHERE DATE(fecha_hora) BETWEEN DATE(?) AND DATE(?) AND tipo = ? AND estado = 'ACTIVO'";

        if (idSucursal != null) {
            sql += " AND id_sucursal = ?";
        }

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            stmt.setString(3, tipo);
            if (idSucursal != null) {
                stmt.setInt(4, idSucursal);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                System.out.println("💰 Total " + tipo + ": S/ " + total);
                return total;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo movimientos de caja: " + e.getMessage());
        }
        return 0.0;
    }

    public List<Object[]> obtenerMovimientosCajaDetallados(java.util.Date fechaInicio, java.util.Date fechaFin, String sucursal) {
        List<Object[]> movimientos = new ArrayList<>();

        String sql = "SELECT DATE(mc.fecha_hora) as fecha, s.nombre_sucursal, "
                + "mc.descripcion, 0 as cantidad, "
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
                + "AND mc.estado = 'ACTIVO' "
                + "AND (? = 'TODAS' OR s.nombre_sucursal = ?) "
                + "ORDER BY mc.fecha_hora DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            stmt.setString(3, sucursal);
            stmt.setString(4, sucursal);

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
            System.out.println("✅ Movimientos de caja encontrados: " + count);
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo movimientos de caja detallados: " + e.getMessage());
        }
        return movimientos;
    }

    // ✅ MÉTODO AUXILIAR PARA LIMITAR TEXTO
    private String limitarTexto(String texto, int maxLength) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= maxLength) {
            return texto;
        }
        return texto.substring(0, maxLength - 3) + "...";
    }
}

package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Caja;
import java.sql.*;

public class CajaDAO {

    private Connection conexion;

    public CajaDAO() {
        try {
            this.conexion = new Conexion().establecerConexion();
        } catch (Exception e) {
            System.err.println("Error conexión CajaDAO: " + e.getMessage());
        }
    }

    // ✅ MÉTODO SOLUCIÓN: Suma ABSOLUTA de todas las ventas de la sucursal (Histórico Total)
    public double obtenerVentasTotalesHistoricas(int idSucursal) {
        // Sumamos directamente de la tabla VENTA, ignorando cierres de caja
        String sql = "SELECT COALESCE(SUM(total), 0) FROM venta WHERE id_sucursal = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error sumando ventas históricas: " + e.getMessage());
        }
        return 0.0;
    }

    public double obtenerSaldoUltimoCierre(int idSucursal, String idEmpleado) {
        // Buscamos la última caja CERRADA de este empleado en esta tienda
        String sql = "SELECT saldo_final FROM caja "
                + "WHERE id_sucursal = ? AND id_empleado = ? AND estado = 'CERRADA' "
                + "ORDER BY fecha_hora_cierre DESC LIMIT 1";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            stmt.setString(2, idEmpleado);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("saldo_final");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo último saldo: " + e.getMessage());
        }
        // Si no ha trabajado antes o no tiene cierres, empieza en 0.00
        return 0.0;
    }

    public double obtenerSaldoAcumuladoHistorico(int idSucursal) {
        // 1. Sumar todas las VENTAS históricas de la sucursal
        String sqlVentas = "SELECT COALESCE(SUM(total), 0) FROM venta WHERE id_sucursal = ?";

        // 2. Sumar todos los RETIROS/GASTOS históricos de la caja (movimiento_caja)
        // Asumimos que 'SALIDA' es el tipo para retiros de dinero
        String sqlRetiros = "SELECT COALESCE(SUM(monto), 0) FROM movimiento_caja "
                + "WHERE id_sucursal = ? AND tipo IN ('SALIDA', 'RETIRO', 'GASTO')";

        double totalVentas = 0;
        double totalRetiros = 0;

        try {
            // Calcular Ventas
            PreparedStatement ps1 = conexion.prepareStatement(sqlVentas);
            ps1.setInt(1, idSucursal);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                totalVentas = rs1.getDouble(1);
            }

            // Calcular Retiros
            PreparedStatement ps2 = conexion.prepareStatement(sqlRetiros);
            ps2.setInt(1, idSucursal);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                totalRetiros = rs2.getDouble(1);
            }

        } catch (SQLException e) {
            System.err.println("Error calculando saldo histórico: " + e.getMessage());
        }

        // El dinero que DEBERÍA haber físicamente es: (Lo que entró - Lo que salió)
        return totalVentas - totalRetiros;
    }

    // --- MÉTODOS ESTÁNDAR NECESARIOS ---
    public Caja obtenerCajaAbierta(int idSucursal) {
        String sql = "SELECT * FROM caja WHERE id_sucursal = ? AND estado = 'ABIERTA' ORDER BY id_caja DESC LIMIT 1";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Caja c = new Caja();
                c.setIdCaja(rs.getInt("id_caja"));
                c.setFechaApertura(rs.getTimestamp("fecha_hora_apertura"));
                // IMPORTANTE: Aunque la BD tenga un saldo inicial guardado, 
                // nosotros usaremos el cálculo histórico en la interfaz.
                c.setSaldoInicial(rs.getDouble("saldo_inicial"));
                c.setEstado(rs.getString("estado"));
                return c;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    public boolean abrirCaja(int idSucursal, double saldoInicial) {
        String sql = "INSERT INTO caja (fecha_hora_apertura, saldo_inicial, saldo_final, estado, id_sucursal) VALUES (NOW(), ?, 0, 'ABIERTA', ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, saldoInicial);
            stmt.setInt(2, idSucursal);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean cerrarCaja(int idCaja, double saldoFinal) {
        String sql = "UPDATE caja SET fecha_hora_cierre = NOW(), saldo_final = ?, estado = 'CERRADA' WHERE id_caja = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, saldoFinal);
            stmt.setInt(2, idCaja);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public double obtenerTotalVentasSesion(int idCaja) {
        String sql = "SELECT COALESCE(SUM(monto), 0) FROM movimiento_caja WHERE id_caja = ? AND tipo = 'VENTA'";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idCaja);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
        }
        return 0.0;
    }

    // Ahora recibe idEmpleado y turno
    public boolean abrirCaja(int idSucursal, double saldoInicial, String idEmpleado, String turno) {
        // Buscamos el total histórico real para usarlo como base si es necesario, 
        // o usamos el saldoInicial que viene del cierre anterior (tu lógica de 'Caja Acumulada').

        String sql = "INSERT INTO caja (fecha_hora_apertura, saldo_inicial, saldo_final, estado, id_sucursal, id_empleado, turno) "
                + "VALUES (NOW(), ?, 0, 'ABIERTA', ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, saldoInicial);
            stmt.setInt(2, idSucursal);
            stmt.setString(3, idEmpleado); // <--- Nuevo
            stmt.setString(4, turno);      // <--- Nuevo
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error abriendo caja: " + e.getMessage());
            return false;
        }
    }

    // ✅ CORRECCIÓN: Buscar caja abierta ESPECÍFICA del empleado en esa sucursal
    public Caja obtenerCajaAbiertaPorUsuario(int idSucursal, String idEmpleado) {
        String sql = "SELECT * FROM caja WHERE id_sucursal = ? AND id_empleado = ? AND estado = 'ABIERTA' ORDER BY id_caja DESC LIMIT 1";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            stmt.setString(2, idEmpleado); // Filtramos por TU usuario

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Caja c = new Caja();
                c.setIdCaja(rs.getInt("id_caja"));
                c.setFechaApertura(rs.getTimestamp("fecha_hora_apertura"));
                c.setSaldoInicial(rs.getDouble("saldo_inicial"));
                c.setEstado(rs.getString("estado"));
                // c.setIdEmpleado(rs.getString("id_empleado")); // Si tienes este campo en tu modelo
                return c;
            }
        } catch (SQLException e) {
            System.err.println("Error buscando caja de usuario: " + e.getMessage());
        }
        return null; // Retorna null si ESTE empleado no tiene caja (permitiendo abrir una nueva)
    }

    // ✅ Método para el Arqueo: Calcula cuánto dinero debería haber según el sistema
    public double calcularSaldoTeorico(int idCaja) {
        double saldoInicial = 0;
        double totalMovimientos = 0;

        // 1. Obtener saldo inicial
        String sqlIni = "SELECT saldo_inicial FROM caja WHERE id_caja = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sqlIni)) {
            ps.setInt(1, idCaja);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                saldoInicial = rs.getDouble("saldo_inicial");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Sumar movimientos (VENTAS + INGRESOS - GASTOS - DEVOLUCIONES)
        // Asumiendo que los movimientos de SALIDA ya se guardaron como negativos o los restamos aquí.
        // Si en tu tabla guardas todo positivo y usas el campo 'tipo', ajusta la lógica:
        String sqlMovs = "SELECT tipo, monto FROM movimiento_caja WHERE id_caja = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sqlMovs)) {
            ps.setInt(1, idCaja);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String tipo = rs.getString("tipo").toUpperCase();
                double monto = rs.getDouble("monto");

                if (tipo.contains("SALIDA") || tipo.contains("GASTO") || tipo.contains("DEVOLUCION")) {
                    totalMovimientos -= monto;
                } else {
                    totalMovimientos += monto; // VENTAS, INGRESOS
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return saldoInicial + totalMovimientos;
    }

    // ✅ Método Actualizado: Cerrar Caja con datos del Arqueo
    public boolean cerrarCajaConArqueo(int idCaja, double saldoFinalReal, double diferencia, String observaciones) {
        String sql = "UPDATE caja SET fecha_hora_cierre = NOW(), saldo_final = ?, estado = 'CERRADA' WHERE id_caja = ?";
        // Podrías guardar la diferencia en otra tabla 'arqueo' si quisieras, por ahora cerramos la caja.

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, saldoFinalReal);
            stmt.setInt(2, idCaja);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cerrando caja: " + e.getMessage());
            return false;
        }
    }
}

package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Caja;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CajaDAO {

    // Eliminamos la variable de instancia 'conexion' para usar try-with-resources en cada método
    public CajaDAO() {
        // Constructor vacío
    }

    // ✅ MÉTODO SOLUCIÓN: Suma ABSOLUTA de todas las ventas de la sucursal (Histórico Total)
    public double obtenerVentasTotalesHistoricas(int idSucursal) {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM venta WHERE id_sucursal = ?";
        try (Connection conexion = new Conexion().establecerConexion(); PreparedStatement stmt = conexion.prepareStatement(sql)) {
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

    public boolean registrarTransferencia(int idSucursalOrigen, int idSucursalDestino, double monto, String motivo) {
        Connection con = null;
        try {
            con = new Conexion().establecerConexion();
            con.setAutoCommit(false); // Iniciar Transacción

            // 1. Obtener ID de la última caja de cada sucursal
            int idCajaOrigen = obtenerUltimaCaja(con, idSucursalOrigen);
            int idCajaDestino = obtenerUltimaCaja(con, idSucursalDestino);

            // Validar que existan cajas (si es una tienda nueva sin cajas, no se puede transferir)
            if (idCajaOrigen == -1 || idCajaDestino == -1) {
                System.err.println("No se encontraron cajas registradas para realizar la vinculación.");
                return false;
            }

            // 2. Registrar SALIDA en origen
            String sqlSalida = "INSERT INTO movimiento_caja (id_caja, tipo, monto, fecha_hora, descripcion, id_sucursal) "
                    + "VALUES (?, 'SALIDA', ?, NOW(), ?, ?)";

            try (PreparedStatement ps1 = con.prepareStatement(sqlSalida)) {
                ps1.setInt(1, idCajaOrigen); // <-- Aquí usamos el ID real, no NULL
                ps1.setDouble(2, monto);
                ps1.setString(3, "Transferencia enviada a Sucursal ID " + idSucursalDestino + ": " + motivo);
                ps1.setInt(4, idSucursalOrigen);
                ps1.executeUpdate();
            }

            // 3. Registrar ENTRADA en destino
            String sqlEntrada = "INSERT INTO movimiento_caja (id_caja, tipo, monto, fecha_hora, descripcion, id_sucursal) "
                    + "VALUES (?, 'INGRESO', ?, NOW(), ?, ?)";

            try (PreparedStatement ps2 = con.prepareStatement(sqlEntrada)) {
                ps2.setInt(1, idCajaDestino); // <-- Aquí usamos el ID real, no NULL
                ps2.setDouble(2, monto);
                ps2.setString(3, "Transferencia recibida de Sucursal ID " + idSucursalOrigen + ": " + motivo);
                ps2.setInt(4, idSucursalDestino);
                ps2.executeUpdate();
            }

            con.commit(); // Confirmar cambios
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private int obtenerUltimaCaja(Connection con, int idSucursal) {
        // Busca la última caja creada en esa sucursal (sea abierta o cerrada)
        String sql = "SELECT id_caja FROM caja WHERE id_sucursal = ? ORDER BY id_caja DESC LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSucursal);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_caja");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retorna -1 si no hay cajas
    }

    // --- NUEVO: LISTAR MOVIMIENTOS DE TRANSFERENCIA ---
    public List<Object[]> listarTransferencias() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT fecha_hora, tipo, monto, descripcion, s.nombre_sucursal "
                + "FROM movimiento_caja mc "
                + "JOIN sucursal s ON mc.id_sucursal = s.id_sucursal "
                + "WHERE descripcion LIKE 'Transferencia%' "
                + "ORDER BY fecha_hora DESC LIMIT 50";

        try (Connection con = new Conexion().establecerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getTimestamp("fecha_hora"),
                    rs.getString("nombre_sucursal"),
                    rs.getString("tipo"),
                    rs.getDouble("monto"),
                    rs.getString("descripcion")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public double obtenerSaldoUltimoCierre(int idSucursal, String idEmpleado) {
        String sql = "SELECT saldo_final FROM caja "
                + "WHERE id_sucursal = ? AND id_empleado = ? AND estado = 'CERRADA' "
                + "ORDER BY fecha_hora_cierre DESC LIMIT 1";

        try (Connection conexion = new Conexion().establecerConexion(); PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            stmt.setString(2, idEmpleado);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("saldo_final");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo último saldo: " + e.getMessage());
        }
        return 0.0;
    }

    // ✅ MÉTODO CORREGIDO: Ahora suma también las transferencias recibidas (INGRESO)
    public double obtenerSaldoAcumuladoHistorico(int idSucursal) {
        // 1. Sumar todas las VENTAS históricas
        String sqlVentas = "SELECT COALESCE(SUM(total), 0) FROM venta WHERE id_sucursal = ?";

        // 2. Sumar SALIDAS (Dinero que sale: Transferencias enviadas, gastos, retiros)
        String sqlSalidas = "SELECT COALESCE(SUM(monto), 0) FROM movimiento_caja "
                + "WHERE id_sucursal = ? AND tipo IN ('SALIDA', 'RETIRO', 'GASTO')";

        // 3. Sumar ENTRADAS (Dinero que entra: Transferencias recibidas, depósitos) <--- ESTO FALTABA
        String sqlEntradas = "SELECT COALESCE(SUM(monto), 0) FROM movimiento_caja "
                + "WHERE id_sucursal = ? AND tipo IN ('INGRESO', 'DEPOSITO')";

        double totalVentas = 0;
        double totalSalidas = 0;
        double totalEntradas = 0;

        try (Connection conexion = new Conexion().establecerConexion()) {
            // A. Calcular Ventas
            try (PreparedStatement ps1 = conexion.prepareStatement(sqlVentas)) {
                ps1.setInt(1, idSucursal);
                ResultSet rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    totalVentas = rs1.getDouble(1);
                }
            }

            // B. Calcular Salidas
            try (PreparedStatement ps2 = conexion.prepareStatement(sqlSalidas)) {
                ps2.setInt(1, idSucursal);
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    totalSalidas = rs2.getDouble(1);
                }
            }

            // C. Calcular Entradas (NUEVO)
            try (PreparedStatement ps3 = conexion.prepareStatement(sqlEntradas)) {
                ps3.setInt(1, idSucursal);
                ResultSet rs3 = ps3.executeQuery();
                if (rs3.next()) {
                    totalEntradas = rs3.getDouble(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error calculando saldo histórico: " + e.getMessage());
        }

        // FÓRMULA FINAL: (Lo que vendí + Lo que me transfirieron) - (Lo que saqué)
        return (totalVentas + totalEntradas) - totalSalidas;
    }

// En edu.UPAO.proyecto.DAO.CajaDAO
    public Caja obtenerCajaAbierta(int idSucursal) {
        // Agregamos: AND DATE(fecha_hora_apertura) = CURDATE()
        // Esto asegura que solo recupere la caja SI es del día actual.
        String sql = "SELECT * FROM caja WHERE id_sucursal = ? AND estado = 'ABIERTA' AND DATE(fecha_hora_apertura) = CURDATE() ORDER BY id_caja DESC LIMIT 1";

        try (Connection conexion = new Conexion().establecerConexion(); PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Caja c = new Caja();
                c.setIdCaja(rs.getInt("id_caja"));
                c.setFechaApertura(rs.getTimestamp("fecha_hora_apertura"));
                c.setSaldoInicial(rs.getDouble("saldo_inicial"));
                c.setEstado(rs.getString("estado"));
                return c;
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo caja abierta: " + e.getMessage());
        }
        return null;
    }
    // En CajaDAO

    public int obtenerIdSucursalPorCaja(int idCaja) {
        String sql = "SELECT id_sucursal FROM caja WHERE id_caja = ?";
        try (Connection con = new Conexion().establecerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCaja);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_sucursal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean abrirCaja(int idSucursal, double saldoInicial) {
        // Este método es una versión simplificada, podría asumir un empleado por defecto o ser obsoleto
        // Se recomienda usar la versión completa abajo
        return abrirCaja(idSucursal, saldoInicial, "DEFAULT", "MAÑANA");
    }

    public boolean cerrarCaja(int idCaja, double saldoFinal) {
        String sql = "UPDATE caja SET fecha_hora_cierre = NOW(), saldo_final = ?, estado = 'CERRADA' WHERE id_caja = ?";
        try (Connection conexion = new Conexion().establecerConexion(); PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, saldoFinal);
            stmt.setInt(2, idCaja);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cerrando caja: " + e.getMessage());
            return false;
        }
    }

    public double obtenerTotalVentasSesion(int idCaja) {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM venta WHERE id_caja = ?"; // Corregido: tabla venta, no movimiento_caja
        try (Connection conexion = new Conexion().establecerConexion(); PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idCaja);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo total ventas sesión: " + e.getMessage());
        }
        return 0.0;
    }

    public boolean abrirCaja(int idSucursal, double saldoInicial, String idEmpleado, String turno) {
        String sql = "INSERT INTO caja (fecha_hora_apertura, saldo_inicial, saldo_final, estado, id_sucursal, id_empleado, turno) "
                + "VALUES (NOW(), ?, 0, 'ABIERTA', ?, ?, ?)";

        try (Connection conexion = new Conexion().establecerConexion(); PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, saldoInicial);
            stmt.setInt(2, idSucursal);
            stmt.setString(3, idEmpleado);
            stmt.setString(4, turno);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error abriendo caja: " + e.getMessage());
            return false;
        }
    }

    // ✅ CORRECCIÓN: Buscar caja abierta ESPECÍFICA del empleado en esa sucursal
    public Caja obtenerCajaAbiertaPorUsuario(int idSucursal, String idEmpleado) {
        String sql = "SELECT * FROM caja WHERE id_sucursal = ? AND id_empleado = ? AND estado = 'ABIERTA' ORDER BY id_caja DESC LIMIT 1";

        try (Connection conexion = new Conexion().establecerConexion(); PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idSucursal);
            stmt.setString(2, idEmpleado);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Caja c = new Caja();
                c.setIdCaja(rs.getInt("id_caja"));
                c.setFechaApertura(rs.getTimestamp("fecha_hora_apertura"));
                c.setSaldoInicial(rs.getDouble("saldo_inicial"));
                c.setEstado(rs.getString("estado"));
                return c;
            }
        } catch (SQLException e) {
            System.err.println("Error buscando caja de usuario: " + e.getMessage());
        }
        return null;
    }

    // ✅ Método para el Arqueo: Calcula cuánto dinero debería haber según el sistema
    public double calcularSaldoTeorico(int idCaja) {
        double saldoInicial = 0;
        double totalVentas = 0; // Usaremos la tabla venta para mayor precisión
        double totalMovimientosExtras = 0; // Para otros ingresos/egresos si usas movimiento_caja

        try (Connection conexion = new Conexion().establecerConexion()) {
            // 1. Obtener saldo inicial
            String sqlIni = "SELECT saldo_inicial FROM caja WHERE id_caja = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sqlIni)) {
                ps.setInt(1, idCaja);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    saldoInicial = rs.getDouble("saldo_inicial");
                }
            }

            // 2. Sumar Ventas (Tabla Venta)
            String sqlVentas = "SELECT COALESCE(SUM(total), 0) FROM venta WHERE id_caja = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sqlVentas)) {
                ps.setInt(1, idCaja);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    totalVentas = rs.getDouble(1);
                }
            }

            // 3. Sumar Movimientos Extras (Opcional, si usas movimiento_caja para gastos/ingresos extras)
            /*
            String sqlMovs = "SELECT tipo, monto FROM movimiento_caja WHERE id_caja = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sqlMovs)) {
                ps.setInt(1, idCaja);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String tipo = rs.getString("tipo").toUpperCase();
                    double monto = rs.getDouble("monto");
                    if (tipo.contains("SALIDA") || tipo.contains("GASTO")) {
                        totalMovimientosExtras -= monto;
                    } else if (tipo.contains("INGRESO")) {
                        totalMovimientosExtras += monto;
                    }
                }
            }
             */
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return saldoInicial + totalVentas + totalMovimientosExtras;
    }

    // ✅ MÉTODO ACTUALIZADO: Cierra caja guardando la auditoría del descuadre
    public boolean cerrarCajaConArqueo(int idCaja, double saldoReal, double diferencia, String observaciones) {
        // Calculamos el saldo teórico (sistema)
        // saldoReal = saldoSistema + diferencia  =>  saldoSistema = saldoReal - diferencia
        double saldoSistema = saldoReal - diferencia;

        String sql = "UPDATE caja SET "
                + "fecha_hora_cierre = NOW(), "
                + "saldo_final = ?, " // Lo que contó el cajero
                + "saldo_sistema = ?, " // Lo que debía haber
                + "diferencia = ?, " // El descuadre
                + "observacion = ?, " // Justificación
                + "estado = 'CERRADA' "
                + "WHERE id_caja = ?";

        try (Connection conexion = new Conexion().establecerConexion(); PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, saldoReal);
            stmt.setDouble(2, saldoSistema);
            stmt.setDouble(3, diferencia);
            stmt.setString(4, observaciones);
            stmt.setInt(5, idCaja);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cerrando caja con arqueo: " + e.getMessage());
            return false;
        }
    }

    // ✅ REPORTE DE DESCUADRES (Para el Gerente)
    public List<Object[]> listarCierresConDescuadre(int idSucursal) {
        List<Object[]> lista = new ArrayList<>();

        String sql = "SELECT c.fecha_hora_cierre, CONCAT(p.nombres, ' ', p.apellidos) as cajero, "
                + "c.saldo_sistema, c.saldo_final, c.diferencia, c.observacion "
                + "FROM caja c "
                + "INNER JOIN empleado e ON c.id_empleado = e.id_empleado "
                + "INNER JOIN persona p ON e.dni = p.dni "
                + "WHERE c.id_sucursal = ? AND c.estado = 'CERRADA' "
                + "AND c.diferencia != 0 "
                + "ORDER BY c.fecha_hora_cierre DESC";

        try (Connection conexion = new Conexion().establecerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idSucursal);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getTimestamp("fecha_hora_cierre"),
                    rs.getString("cajero"),
                    rs.getDouble("saldo_sistema"),
                    rs.getDouble("saldo_final"),
                    rs.getDouble("diferencia"),
                    rs.getString("observacion")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}

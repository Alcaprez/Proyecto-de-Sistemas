package edu.UPAO.proyecto.DAO;

import BaseDatos.Conexion;
import edu.UPAO.proyecto.Modelo.Cupon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CuponDAO {

    private Connection getConexion() {
        try {
            return new Conexion().establecerConexion();
        } catch (Exception e) {
            System.err.println("❌ Error conectando CuponDAO: " + e.getMessage());
            return null;
        }
    }

    public static List<Cupon> listar() {
        List<Cupon> lista = new ArrayList<>();
        String sql = "SELECT * FROM cupon";
        CuponDAO dao = new CuponDAO();
        
        try (Connection con = dao.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapearCupon(rs));
        } catch (SQLException e) {
            System.err.println("❌ Error listando cupones: " + e.getMessage());
        }
        return lista;
    }

    public static void upsert(Cupon cupon) {
        if (buscarPorCodigo(cupon.getCodigo()).isPresent()) {
            actualizar(cupon);
        } else {
            insertar(cupon);
        }
    }

    public static Optional<Cupon> buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM cupon WHERE codigo = ?";
        CuponDAO dao = new CuponDAO();
        try (Connection con = dao.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapearCupon(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error buscando: " + e.getMessage());
        }
        return Optional.empty();
    }

    // ✅ INSERTAR (Usando 'descripcion' correctamente)
    private static void insertar(Cupon c) {
        String sql = "INSERT INTO cupon (codigo, tipo, valor, descripcion, min_compra, " +
                     "fecha_inicio, fecha_fin, estado, max_usos, usos_actuales) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        CuponDAO dao = new CuponDAO();
        
        try (Connection con = dao.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, c.getCodigo());
            ps.setString(2, c.getTipo() == Cupon.TipoDescuento.PERCENT ? "PORCENTAJE" : "MONTO_FIJO");
            ps.setDouble(3, c.getValor());
            ps.setString(4, c.getDescripcion()); // ✅ Ahora guarda en descripcion
            ps.setDouble(5, c.getMinimoCompra());
            ps.setDate(6, c.getInicio() != null ? java.sql.Date.valueOf(c.getInicio()) : null);
            ps.setDate(7, c.getFin() != null ? java.sql.Date.valueOf(c.getFin()) : null);
            ps.setString(8, c.isActivo() ? "ACTIVO" : "INACTIVO");
            ps.setInt(9, c.getMaxUsos());
            ps.setInt(10, c.getUsos());

            ps.executeUpdate();
            System.out.println("✅ Cupón insertado: " + c.getCodigo());

        } catch (SQLException e) {
            System.err.println("❌ Error insertando cupón: " + e.getMessage());
        }
    }

    private static void actualizar(Cupon c) {
        String sql = "UPDATE cupon SET tipo=?, valor=?, descripcion=?, min_compra=?, " +
                     "fecha_inicio=?, fecha_fin=?, estado=?, max_usos=?, usos_actuales=? " +
                     "WHERE codigo=?";
        CuponDAO dao = new CuponDAO();
        
        try (Connection con = dao.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, c.getTipo() == Cupon.TipoDescuento.PERCENT ? "PORCENTAJE" : "MONTO_FIJO");
            ps.setDouble(2, c.getValor());
            ps.setString(3, c.getDescripcion()); // ✅ Actualiza descripcion
            ps.setDouble(4, c.getMinimoCompra());
            ps.setDate(5, c.getInicio() != null ? java.sql.Date.valueOf(c.getInicio()) : null);
            ps.setDate(6, c.getFin() != null ? java.sql.Date.valueOf(c.getFin()) : null);
            ps.setString(7, c.isActivo() ? "ACTIVO" : "INACTIVO");
            ps.setInt(8, c.getMaxUsos());
            ps.setInt(9, c.getUsos());
            ps.setString(10, c.getCodigo());

            ps.executeUpdate();
            System.out.println("✅ Cupón actualizado: " + c.getCodigo());

        } catch (SQLException e) {
            System.err.println("❌ Error actualizando: " + e.getMessage());
        }
    }

    public static void eliminar(String codigo) {
        String sql = "DELETE FROM cupon WHERE codigo = ?";
        CuponDAO dao = new CuponDAO();
        try (Connection con = dao.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error eliminando: " + e.getMessage());
        }
    }

    private static Cupon mapearCupon(ResultSet rs) throws SQLException {
        String codigo = rs.getString("codigo");
        String tipoStr = rs.getString("tipo");
        Cupon.TipoDescuento tipo = "PORCENTAJE".equalsIgnoreCase(tipoStr) ? 
                Cupon.TipoDescuento.PERCENT : Cupon.TipoDescuento.FLAT;
        
        double valor = rs.getDouble("valor");
        String desc = rs.getString("descripcion"); // ✅ Lee de descripcion
        double min = rs.getDouble("min_compra");
        
        java.sql.Date ini = rs.getDate("fecha_inicio");
        java.sql.Date fin = rs.getDate("fecha_fin");
        boolean activo = "ACTIVO".equalsIgnoreCase(rs.getString("estado"));
        int max = rs.getInt("max_usos");
        int usos = rs.getInt("usos_actuales");

        return new Cupon(codigo, tipo, valor, desc, min,
            ini != null ? ini.toLocalDate() : null,
            fin != null ? fin.toLocalDate() : null,
            activo, max, usos);
    }
}
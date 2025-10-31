package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Proveedor;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO que gestiona proveedores persistiendo los datos en un archivo CSV.
 * Además, registra automáticamente los cambios en el HistorialProveedorDAO.
 */
public class ProveedorDAO {

    private static final String ARCHIVO_CSV = "proveedores.csv";
    private static final List<Proveedor> proveedores = new ArrayList<>();
    private static int contadorId = 1; // Generador de IDs únicos
    // DAO auxiliar para historial
    private final HistorialProveedorDAO historialDAO = new HistorialProveedorDAO();

    // Constructor: carga los datos al iniciar
    public ProveedorDAO() {
        cargarDesdeCSV();
        // Reinicia contadorId basado en el mayor ID existente
        proveedores.stream()
                .mapToInt(Proveedor::getIdProveedor)
                .max()
                .ifPresent(maxId -> contadorId = maxId + 1);
    }

    /**
     * Agrega un nuevo proveedor y guarda en CSV
     *
     * @param proveedor
     */
    public void agregar(Proveedor proveedor) {
        if (buscarPorRuc(proveedor.getRuc()) != null) {
            throw new IllegalArgumentException("Ya existe un proveedor con el RUC: " + proveedor.getRuc());
        }
        proveedor.setIdProveedor(contadorId++);
        if (proveedor.getFechaRegistro() == null) {
            proveedor.setFechaRegistro(LocalDate.now());
        }
        proveedores.add(proveedor);
        guardarEnCSV();
        historialDAO.registrarEvento(
                proveedor.getIdProveedor(),
                "REGISTRO",
                "Proveedor agregado: " + proveedor.getNombre()
        );
    }

    /**
     * Lista todos los proveedores (copiado para evitar modificaciones directas)
     *
     * @return
     */
    public List<Proveedor> listar() {
        return new ArrayList<>(proveedores);
    }

    public Proveedor buscarPorId(int id) {
        return proveedores.stream()
                .filter(p -> p.getIdProveedor() == id)
                .findFirst()
                .orElse(null);
    }

    public Proveedor buscarPorRuc(String ruc) {
        return proveedores.stream()
                .filter(p -> p.getRuc().equalsIgnoreCase(ruc))
                .findFirst()
                .orElse(null);
    }

    /**
     * Actualiza campos específicos del proveedor
     *
     * @param id
     * @param nombre
     * @param ruc
     * @param telefono
     * @param correo
     * @param direccion
     * @param contactoPrincipal
     * @return
     */
    public boolean actualizarCampos(int id, String nombre, String ruc, String telefono, String correo,
            String direccion, String contactoPrincipal) {
        Proveedor proveedor = buscarPorId(id);
        if (proveedor == null) {
            return false;
        }
        StringBuilder cambios = new StringBuilder();

        if (nombre != null && !nombre.isBlank()) {
            proveedor.setNombre(nombre);
            cambios.append("Nombre, ");
        }
        if (ruc != null && !ruc.isBlank()) {
            Proveedor existente = buscarPorRuc(ruc);
            if (existente != null && existente.getIdProveedor() != id) {
                throw new IllegalArgumentException("Ya existe otro proveedor con el RUC: " + ruc);
            }
            proveedor.setRuc(ruc);
            cambios.append("RUC, ");
        }
        if (telefono != null && !telefono.isBlank()) {
            proveedor.setTelefono(telefono);
            cambios.append("Teléfono, ");
        }
        if (correo != null && !correo.isBlank()) {
            proveedor.setCorreo(correo);
            cambios.append("Correo, ");
        }
        if (direccion != null && !direccion.isBlank()) {
            proveedor.setDireccion(direccion);
            cambios.append("Dirección, ");
        }
        if (contactoPrincipal != null && !contactoPrincipal.isBlank()) {
            proveedor.setContactoPrincipal(contactoPrincipal);
            cambios.append("Contacto principal, ");
        }

        guardarEnCSV();

        // Registrar en historial
        if (cambios.length() > 0) {
            historialDAO.registrarEvento(
                    id,
                    "ACTUALIZACION",
                    "Se actualizaron los campos: " + cambios.substring(0, cambios.length() - 2)
            );
        }

        return true;
    }

    /**
     * Cambia el estado activo/inactivo
     */
    public void cambiarEstado(int id, boolean activo) {
        Proveedor p = buscarPorId(id);
        if (p != null) {
            p.setActivo(activo);
            guardarEnCSV();

            // Registrar en historial
            historialDAO.registrarEvento(
                    id,
                    activo ? "ACTIVACION" : "DESACTIVACION",
                    "Proveedor " + (activo ? "activado" : "desactivado")
            );
        }
    }

    // ==============================
    //  MÉTODOS PRIVADOS DE ARCHIVO
    // ==============================
    /**
     * Carga los proveedores desde el archivo CSV
     */
    private void cargarDesdeCSV() {
        proveedores.clear();
        File archivo = new File(ARCHIVO_CSV);
        if (!archivo.exists()) {
            System.out.println("Archivo CSV no encontrado, se creará uno nuevo.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            br.readLine(); // Saltar encabezado
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] c = linea.split(";", -1);
                if (c.length < 9) {
                    continue;
                }
                // Leemos las columnas en el MISMO orden en que se guardaron
                int id = Integer.parseInt(c[0].trim());           // Columna 1
                String nombre = c[1].trim();                      // Columna 2
                String ruc = c[2].trim();                         // Columna 3
                String direccion = c[3].trim();                   // Columna 4
                String telefono = c[4].trim();                    // Columna 5
                String correo = c[5].trim();                      // Columna 6
                String contacto = c[6].trim();                    // Columna 7
                boolean activo = Boolean.parseBoolean(c[7].trim()); // Columna 8 (el booleano)
                String fechaStr = c[8].trim();                    // Columna 9 (la fecha)

                LocalDate fechaRegistro;
                if (fechaStr.contains("T")) {
                    fechaRegistro = LocalDateTime.parse(fechaStr).toLocalDate();
                } else {
                    fechaRegistro = LocalDate.parse(fechaStr);
                }

                proveedores.add(new Proveedor(id, nombre, ruc, telefono, correo, direccion, contacto, fechaRegistro, activo));
            }
        } catch (Exception e) {
            System.err.println("Error fatal al leer CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Guarda la lista actual de proveedores en el CSV
     */
    private void guardarEnCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_CSV))) {
            bw.write("idProveedor;nombre;ruc;direccion;telefono;correo;contactoPrincipal;activo;fechaRegistro\n");
            for (Proveedor p : proveedores) {
                bw.write(String.format("%d;%s;%s;%s;%s;%s;%s;%b;%s\n",
                        // 1. idProveedor
                        p.getIdProveedor(),
                        // 2. nombre
                        p.getNombre(),
                        // 3. ruc
                        p.getRuc(),
                        // 4. direccion
                        p.getDireccion(),
                        // 5. telefono
                        p.getTelefono(),
                        // 6. correo
                        p.getCorreo(),
                        // 7. contactoPrincipal
                        p.getContactoPrincipal(),
                        // 8. activo (el booleano)
                        p.isActivo(),
                        // 9. fechaRegistro (la fecha)
                        p.getFechaRegistro().toString()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar CSV: " + e.getMessage());
        }
    }
}

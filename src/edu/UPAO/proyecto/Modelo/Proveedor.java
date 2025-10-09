
package edu.UPAO.proyecto.Modelo;
import java.time.LocalDate;

public class Proveedor {
    private int idProveedor;
    private String nombre;
    private String ruc;
    private String telefono;
    private String correo;
    private String direccion;
    private String contactoPrincipal;
    private LocalDate fechaRegistro;
    private boolean activo;

    // Constructor completo (para casos especiales)
    public Proveedor(int idProveedor, String nombre, String ruc, String telefono,
                     String correo, String direccion, String contactoPrincipal,
                     LocalDate fechaRegistro, boolean activo) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.ruc = ruc;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.contactoPrincipal = contactoPrincipal;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
    }

    // Constructor sin ID ni fecha (el DAO los genera)
    public Proveedor(String nombre, String ruc, String direccion,
                     String telefono, String correo, String contactoPrincipal, boolean activo) {
        this.nombre = nombre;
        this.ruc = ruc;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.contactoPrincipal = contactoPrincipal;
        this.fechaRegistro = LocalDate.now();
        this.activo = activo;
    }

    // Getters y Setters
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getContactoPrincipal() { return contactoPrincipal; }
    public void setContactoPrincipal(String contactoPrincipal) { this.contactoPrincipal = contactoPrincipal; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + idProveedor +
                ", nombre='" + nombre + '\'' +
                ", ruc='" + ruc + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", direccion='" + direccion + '\'' +
                ", contacto='" + contactoPrincipal + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", activo=" + activo +
                '}';
    }
}


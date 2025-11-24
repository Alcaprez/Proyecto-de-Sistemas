package edu.UPAO.proyecto.Modelo;

public class Proveedor {
    // --- Campos directos de la tabla 'proveedor' ---
    private String idProveedor; // PK char(8)
    private String razonSocial;
    private String dniAsociado; // FK char(8)
    private String ruc;
    private String direccion;
    private int idSucursal; // int
    private String estado;

    // --- Campos auxiliares (vienen del JOIN con tabla 'persona') ---
    // Usaremos estos para mostrar el contacto en la tabla
    private String nombresContacto; 
    private String apellidosContacto;
    private String telefonoContacto;
    // private String correoContacto; // El formulario no pide correo, lo omitimos por ahora

    public Proveedor() {
    }

    // --- Getters y Setters ---
    public String getIdProveedor() { return idProveedor; }
    public void setIdProveedor(String idProveedor) { this.idProveedor = idProveedor; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getDniAsociado() { return dniAsociado; }
    public void setDniAsociado(String dniAsociado) { this.dniAsociado = dniAsociado; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getIdSucursal() { return idSucursal; }
    public void setIdSucursal(int idSucursal) { this.idSucursal = idSucursal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNombresContacto() { return nombresContacto; }
    public void setNombresContacto(String nombresContacto) { this.nombresContacto = nombresContacto; }

    public String getApellidosContacto() { return apellidosContacto; }
    public void setApellidosContacto(String apellidosContacto) { this.apellidosContacto = apellidosContacto; }

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }

    // Helper para mostrar nombre completo en la tabla si se desea
    public String getNombreCompletoContacto() {
        return (nombresContacto != null ? nombresContacto : "") + " " + (apellidosContacto != null ? apellidosContacto : "");
    }

    @Override
    public String toString() {
        return razonSocial; // Para que los combobox muestren el nombre
    }
}
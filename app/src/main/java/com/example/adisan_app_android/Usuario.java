package com.example.adisan_app_android;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName(value = "id", alternate = {"usuario_id", "id_usuario"})
    private int id;

    @SerializedName(value = "nombres", alternate = {"nombre", "name"})
    private String nombres;

    @SerializedName(value = "apellidos", alternate = {"apellido"})
    private String apellidos;

    @SerializedName("dni")
    private String dni;

    @SerializedName("ruc")
    private String ruc;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("correo")
    private String correo;

    @SerializedName("usuario")
    private String usuario;

    @SerializedName("password")
    private String password;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("rol")
    private String rol = "cliente";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres != null ? nombres : "";
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos != null ? apellidos : "";
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni != null ? dni : "";
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTelefono() {
        return telefono != null ? telefono : "";
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo != null ? correo : "";
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUsuario() {
        return usuario != null ? usuario : "";
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDireccion() {
        return direccion != null ? direccion : "";
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombreCompleto() {
        String n = getNombres();
        String a = getApellidos();
        if (!n.isEmpty() && !a.isEmpty()) {
            return n + " " + a;
        } else if (!n.isEmpty()) {
            return n;
        } else if (usuario != null && !usuario.isEmpty()) {
            return usuario;
        }
        return "Cliente #" + id;
    }

    @NonNull
    @Override
    public String toString() {
        String info = getNombreCompleto();
        if (!getDni().isEmpty()) {
            info += " (DNI: " + getDni() + ")";
        }
        return info;
    }
}
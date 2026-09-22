package com.example.adisan_app_android;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

public class Proveedor {

    @SerializedName("id")
    private int id;

    @SerializedName("nombreEmpresa")
    private String nombreEmpresa;

    @SerializedName("nombre_empresa")
    private String nombreEmpresaAlt;

    @SerializedName("ruc")
    private String ruc;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("correo")
    private String correo;

    @SerializedName("direccion")
    private String direccion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreEmpresa() {
        if (nombreEmpresa != null && !nombreEmpresa.isEmpty()) return nombreEmpresa;
        if (nombreEmpresaAlt != null && !nombreEmpresaAlt.isEmpty()) return nombreEmpresaAlt;
        return "Proveedor #" + id;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
        this.nombreEmpresaAlt = nombreEmpresa;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @NonNull
    @Override
    public String toString() {
        return getNombreEmpresa();
    }
}
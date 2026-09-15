package com.example.adisan_app_android;

import com.google.gson.annotations.SerializedName;

public class Producto {

    @SerializedName(value = "producto_id", alternate = {"_id", "id_producto"})
    private int productoId;

    @SerializedName("id")
    private int id;

    @SerializedName(value = "producto_nombre", alternate = {"name", "title", "producto"})
    private String productoNombre;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("activo")
    private int activo = 1;

    @SerializedName("presentacion_id")
    private Integer presentacionId = 1;

    @SerializedName(value = "proveedor_id", alternate = {"id_proveedor"})
    private Integer proveedorId = 1;

    @SerializedName("categoria_id")
    private Integer categoriaId = 1;

    @SerializedName("padre_id")
    private Integer padreId;

    @SerializedName("nivel")
    private Integer nivel = 1;

    @SerializedName("factor")
    private Integer factor = 1;

    @SerializedName(value = "stock", alternate = {"cantidad", "inventory"})
    private int stock;

    @SerializedName(value = "precio_compra", alternate = {"precio_costo"})
    private String precioCompra;

    @SerializedName("precioCompra")
    private String precioCompraAlt;

    @SerializedName(value = "precio_venta", alternate = {"precio"})
    private String precioVenta;

    @SerializedName("precioVenta")
    private String precioVentaAlt;

    public int getProductoId() {
        return productoId != 0 ? productoId : id;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
        this.id = productoId;
    }

    public String getProductoNombre() {
        if (productoNombre != null && !productoNombre.isEmpty()) return productoNombre;
        if (nombre != null && !nombre.isEmpty()) return nombre;
        return "";
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
        this.nombre = productoNombre;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public Integer getPresentacionId() {
        return presentacionId != null ? presentacionId : 1;
    }

    public void setPresentacionId(Integer presentacionId) {
        this.presentacionId = presentacionId;
    }

    public Integer getProveedorId() {
        return proveedorId != null ? proveedorId : 1;
    }

    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }

    public Integer getCategoriaId() {
        return categoriaId != null ? categoriaId : 1;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Integer getPadreId() {
        return padreId;
    }

    public void setPadreId(Integer padreId) {
        this.padreId = padreId;
    }

    public Integer getNivel() {
        return nivel != null ? nivel : 1;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Integer getFactor() {
        return factor != null ? factor : 1;
    }

    public void setFactor(Integer factor) {
        this.factor = factor;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getPrecioCompra() {
        if (precioCompra != null && !precioCompra.isEmpty()) return precioCompra;
        if (precioCompraAlt != null && !precioCompraAlt.isEmpty()) return precioCompraAlt;
        return "0.00";
    }

    public void setPrecioCompra(String precioCompra) {
        this.precioCompra = precioCompra;
        this.precioCompraAlt = precioCompra;
    }

    public String getPrecioVenta() {
        if (precioVenta != null && !precioVenta.isEmpty()) return precioVenta;
        if (precioVentaAlt != null && !precioVentaAlt.isEmpty()) return precioVentaAlt;
        return "0.00";
    }

    public void setPrecioVenta(String precioVenta) {
        this.precioVenta = precioVenta;
        this.precioVentaAlt = precioVenta;
    }

    public double getPrecioCompraDouble() {
        try {
            String val = getPrecioCompra();
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public double getPrecioVentaDouble() {
        try {
            String val = getPrecioVenta();
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
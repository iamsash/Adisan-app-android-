package com.example.adisan_app_android;

import com.google.gson.annotations.SerializedName;

public class DetallePedido {

    @SerializedName(value = "id", alternate = {"detalle_id"})
    private int id;

    @SerializedName(value = "pedido_id")
    private int pedidoId;

    @SerializedName(value = "producto_id")
    private int productoId;

    @SerializedName(value = "presentacion_id")
    private int presentacionId;

    @SerializedName(value = "producto_nombre", alternate = {"nombre", "producto"})
    private String productoNombre;

    @SerializedName(value = "cantidad")
    private int cantidad;

    @SerializedName(value = "precio")
    private String precio;

    @SerializedName(value = "subtotal")
    private String subtotal;

    @SerializedName(value = "nombre_oferta")
    private String nombreOferta;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public int getPresentacionId() {
        return presentacionId;
    }

    public void setPresentacionId(int presentacionId) {
        this.presentacionId = presentacionId;
    }

    public String getProductoNombre() {
        return productoNombre != null ? productoNombre : "Producto #" + productoId;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio != null ? precio : "0.00";
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getSubtotal() {
        return subtotal != null ? subtotal : "0.00";
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getNombreOferta() {
        return nombreOferta;
    }

    public void setNombreOferta(String nombreOferta) {
        this.nombreOferta = nombreOferta;
    }

    public double getPrecioDouble() {
        try {
            return precio != null ? Double.parseDouble(precio) : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public double getSubtotalDouble() {
        try {
            return subtotal != null ? Double.parseDouble(subtotal) : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
package com.example.adisan_app_android;

public class ItemCarrito {

    private int productoId;
    private int presentacionId;
    private String productoNombre;
    private int stock;
    private int cantidad;
    private String precio;
    private double precioDouble;

    public ItemCarrito(Producto producto, int cantidad) {
        this.productoId = producto.getProductoId();
        this.presentacionId = producto.getPresentacionId();
        this.productoNombre = producto.getProductoNombre();
        this.stock = producto.getStock();
        this.cantidad = cantidad;
        this.precio = producto.getPrecioVenta();
        this.precioDouble = producto.getPrecioVentaDouble();
    }

    public int getProductoId() {
        return productoId;
    }

    public int getPresentacionId() {
        return presentacionId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public int getStock() {
        return stock;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public double getPrecioDouble() {
        return precioDouble;
    }

    public double getSubtotal() {
        return cantidad * precioDouble;
    }
}
package com.example.adisan_app_android;

public class DashboardResponse {

    private boolean success;
    private int productos;
    private int usuarios;
    private int pedidos;
    private double ventas;
    private int stockBajo;
    private int enCamino;
    private double totalVendido;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getProductos() {
        return productos;
    }

    public void setProductos(int productos) {
        this.productos = productos;
    }

    public int getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(int usuarios) {
        this.usuarios = usuarios;
    }

    public int getPedidos() {
        return pedidos;
    }

    public void setPedidos(int pedidos) {
        this.pedidos = pedidos;
    }

    public double getVentas() {
        return ventas;
    }

    public void setVentas(double ventas) {
        this.ventas = ventas;
    }

    public int getStockBajo() {
        return stockBajo;
    }

    public void setStockBajo(int stockBajo) {
        this.stockBajo = stockBajo;
    }

    public int getEnCamino() {
        return enCamino;
    }

    public void setEnCamino(int enCamino) {
        this.enCamino = enCamino;
    }

    public double getTotalVendido() {
        return totalVendido;
    }

    public void setTotalVendido(double totalVendido) {
        this.totalVendido = totalVendido;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
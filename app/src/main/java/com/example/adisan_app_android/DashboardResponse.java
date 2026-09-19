package com.example.adisan_app_android;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DashboardResponse {

    @SerializedName(value = "success")
    private boolean success = true;

    @SerializedName(value = "totalProductos", alternate = {"productos"})
    private int totalProductos;

    @SerializedName(value = "totalUsuarios", alternate = {"usuarios"})
    private int totalUsuarios;

    @SerializedName(value = "totalPedidos", alternate = {"pedidos"})
    private int totalPedidos;

    @SerializedName(value = "totalVentas", alternate = {"ventas", "totalVendido"})
    private Object totalVentas;

    @SerializedName(value = "utilidad")
    private Object utilidad;

    @SerializedName(value = "stockBajo")
    private List<Object> stockBajoList;

    @SerializedName(value = "pedidosPorEstado")
    private List<EstadoCount> pedidosPorEstado;

    @SerializedName("message")
    private String message;

    public static class EstadoCount {
        @SerializedName("estado")
        private String estado;

        @SerializedName("cantidad")
        private int cantidad;

        public String getEstado() {
            return estado != null ? estado : "";
        }

        public int getCantidad() {
            return cantidad;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getProductos() {
        return totalProductos;
    }

    public void setProductos(int productos) {
        this.totalProductos = productos;
    }

    public int getUsuarios() {
        return totalUsuarios;
    }

    public void setUsuarios(int usuarios) {
        this.totalUsuarios = usuarios;
    }

    public int getPedidos() {
        return totalPedidos;
    }

    public void setPedidos(int pedidos) {
        this.totalPedidos = pedidos;
    }

    public double getVentas() {
        if (totalVentas != null) {
            try {
                return Double.parseDouble(totalVentas.toString());
            } catch (Exception e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    public void setVentas(double ventas) {
        this.totalVentas = ventas;
    }

    public int getStockBajo() {
        return stockBajoList != null ? stockBajoList.size() : 0;
    }

    public int getEnCamino() {
        int count = 0;
        if (pedidosPorEstado != null) {
            for (EstadoCount ec : pedidosPorEstado) {
                if ("en_camino".equalsIgnoreCase(ec.getEstado()) || "en camino".equalsIgnoreCase(ec.getEstado())) {
                    count += ec.getCantidad();
                }
            }
        }
        return count;
    }

    public double getTotalVendido() {
        return getVentas();
    }

    public void setTotalVendido(double totalVendido) {
        this.totalVentas = totalVendido;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
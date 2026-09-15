package com.example.adisan_app_android;

import com.google.gson.annotations.SerializedName;

public class Venta {

    @SerializedName(value = "venta_id", alternate = {"id", "_id", "id_venta"})
    private int ventaId;

    @SerializedName(value = "pedido_id", alternate = {"id_pedido"})
    private int pedidoId;

    @SerializedName(value = "cliente_id", alternate = {"usuario_id", "id_cliente"})
    private int clienteId;

    @SerializedName(value = "cliente", alternate = {"cliente_nombre", "nombres", "usuario", "nombre_cliente"})
    private String cliente;

    @SerializedName(value = "total", alternate = {"monto", "total_venta", "monto_total"})
    private String total;

    @SerializedName(value = "metodo_pago", alternate = {"metodoPago", "metodo"})
    private String metodoPago;

    @SerializedName(value = "estado")
    private String estado;

    @SerializedName(value = "fecha", alternate = {"created_at", "fecha_venta", "fecha_creacion"})
    private String fecha;

    public int getVentaId() {
        return ventaId;
    }

    public void setVentaId(int ventaId) {
        this.ventaId = ventaId;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getCliente() {
        return cliente != null ? cliente : "Cliente #" + clienteId;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTotal() {
        return total != null ? total : "0.00";
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public double getTotalDouble() {
        try {
            return total != null ? Double.parseDouble(total) : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public String getMetodoPago() {
        return metodoPago != null ? metodoPago : "Efectivo";
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado != null ? estado : "completado";
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha != null ? fecha : "";
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
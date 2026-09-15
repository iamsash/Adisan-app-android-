package com.example.adisan_app_android;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PedidoResponse {

    private boolean success;

    @SerializedName(value = "data", alternate = {"pedidos", "items", "results"})
    private List<Pedido> data;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Pedido> getData() {
        return data;
    }

    public void setData(List<Pedido> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
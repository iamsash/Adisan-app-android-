package com.example.adisan_app_android;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VentaResponse {

    private boolean success;

    @SerializedName(value = "data", alternate = {"ventas", "items", "results"})
    private List<Venta> data;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Venta> getData() {
        return data;
    }

    public void setData(List<Venta> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
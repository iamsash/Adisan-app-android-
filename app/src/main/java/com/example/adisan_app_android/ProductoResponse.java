package com.example.adisan_app_android;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductoResponse {

    private boolean success;

    @SerializedName(value = "data", alternate = {"productos", "items", "results"})
    private List<Producto> data;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Producto> getData() {
        return data;
    }

    public void setData(List<Producto> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
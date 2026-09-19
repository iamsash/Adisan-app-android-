package com.example.adisan_app_android;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UsuarioResponse {

    private boolean success = true;

    @SerializedName(value = "data", alternate = {"usuarios", "items", "results"})
    private List<Usuario> data;

    private int total;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Usuario> getData() {
        return data;
    }

    public void setData(List<Usuario> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
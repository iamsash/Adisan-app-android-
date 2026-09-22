package com.example.adisan_app_android;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CategoriaResponse {

    private boolean success = true;

    @SerializedName(value = "data", alternate = {"categorias", "items"})
    private List<Categoria> data;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Categoria> getData() {
        return data;
    }

    public void setData(List<Categoria> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
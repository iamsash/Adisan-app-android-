package com.example.adisan_app_android;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {

    @SerializedName("password")
    private String password;

    public ResetPasswordRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
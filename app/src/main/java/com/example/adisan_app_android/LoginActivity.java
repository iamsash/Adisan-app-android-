package com.example.adisan_app_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsuario;
    private TextInputEditText etPassword;
    private TextInputLayout tilUsuario;
    private TextInputLayout tilPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        initViews();

        // Configurar listener para el botón de inicio de sesión
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarYIniciarSesion();
            }
        });
    }

    private void initViews() {
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        tilUsuario = findViewById(R.id.tilUsuario);
        tilPassword = findViewById(R.id.tilPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void validarYIniciarSesion() {
        // Limpiar errores previos en las cajas de texto
        if (tilUsuario != null) tilUsuario.setError(null);
        if (tilPassword != null) tilPassword.setError(null);

        String usuario = etUsuario != null && etUsuario.getText() != null
                ? etUsuario.getText().toString().trim()
                : "";
        String password = etPassword != null && etPassword.getText() != null
                ? etPassword.getText().toString().trim()
                : "";

        boolean esValido = true;

        if (TextUtils.isEmpty(usuario)) {
            if (tilUsuario != null) {
                tilUsuario.setError(getString(R.string.error_empty_usuario));
            } else if (etUsuario != null) {
                etUsuario.setError(getString(R.string.error_empty_usuario));
            }
            esValido = false;
        }

        if (TextUtils.isEmpty(password)) {
            if (tilPassword != null) {
                tilPassword.setError(getString(R.string.error_empty_password));
            } else if (etPassword != null) {
                etPassword.setError(getString(R.string.error_empty_password));
            }
            esValido = false;
        }

        if (esValido) {
            ejecutarLogin(usuario, password);
        }
    }

    private void ejecutarLogin(String usuario, String password) {
        // Deshabilitar botón durante la petición
        btnLogin.setEnabled(false);

        LoginRequest request = new LoginRequest(usuario, password);

        ApiClient.getApiService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();

                    if (res.isSuccess()) {
                        // 1. Mostrar mensaje de bienvenida
                        String nombreUsuario = res.getUsuario() != null ? res.getUsuario() : usuario;
                        Toast.makeText(LoginActivity.this, "¡Bienvenido, " + nombreUsuario + "!", Toast.LENGTH_SHORT).show();

                        // 2. Guardar token, usuario y rol en SharedPreferences
                        guardarSesion(res);

                        // 3. Abrir DashboardActivity
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(intent);

                        // 4. Cerrar LoginActivity
                        finish();
                    } else {
                        String msg = res.getMessage() != null ? res.getMessage() : "Credenciales incorrectas";
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Procesar respuesta con código de error HTTP (400, 401, etc.)
                    String mensajeError = "Credenciales incorrectas o error en el servidor";
                    if (response.errorBody() != null) {
                        try {
                            String errorString = response.errorBody().string();
                            LoginResponse errorRes = new Gson().fromJson(errorString, LoginResponse.class);
                            if (errorRes != null && errorRes.getMessage() != null) {
                                mensajeError = errorRes.getMessage();
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    Toast.makeText(LoginActivity.this, mensajeError, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this,
                        "Error de conexión con el servidor (10.0.2.2:3000). Verifica que el backend esté ejecutándose.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void guardarSesion(LoginResponse response) {
        SharedPreferences preferences = getSharedPreferences("AdisanPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", response.getToken());
        editor.putString("usuario", response.getUsuario());
        editor.putString("rol", response.getRol());
        editor.putInt("id", response.getId());
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }
}
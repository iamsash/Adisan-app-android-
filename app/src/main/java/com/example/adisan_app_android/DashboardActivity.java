package com.example.adisan_app_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private TextView tvSubGreeting;
    private TextView tvAvatarInitial;

    // Tarjetas principales
    private TextView tvValProductos;
    private TextView tvValUsuarios;
    private TextView tvValPedidos;
    private TextView tvValVentas;
    private TextView tvValStockBajo;
    private TextView tvValEnCamino;
    private TextView tvTotalVendido;

    // Alertas
    private TextView tvAlertStockBajoText;
    private TextView tvAlertEnCaminoText;

    private MaterialCardView cardProductos;
    private MaterialCardView cardUsuarios;
    private MaterialCardView cardPedidos;
    private MaterialCardView cardVentas;
    private MaterialCardView cardStockBajo;
    private MaterialCardView cardEnCamino;

    // Accesos rápidos
    private MaterialCardView cardQuickProductos;
    private MaterialCardView cardQuickPedidos;
    private MaterialCardView cardQuickOfertas;
    private MaterialCardView cardQuickStock;

    // Navegación e iconos superiores
    private BottomNavigationView bottomNavigation;
    private ImageView btnNotifications;
    private ImageView btnAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 1. Inicializar vistas
        initViews();

        // 2. Cargar datos de usuario guardados en SharedPreferences
        cargarDatosUsuario();

        // 3. Consumir backend GET /api/dashboard
        obtenerDatosDashboard();

        // 4. Configurar listeners de interacción
        setupListeners();
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvSubGreeting = findViewById(R.id.tvSubGreeting);
        tvAvatarInitial = findViewById(R.id.tvAvatarInitial);

        tvValProductos = findViewById(R.id.tvValProductos);
        tvValUsuarios = findViewById(R.id.tvValUsuarios);
        tvValPedidos = findViewById(R.id.tvValPedidos);
        tvValVentas = findViewById(R.id.tvValVentas);
        tvValStockBajo = findViewById(R.id.tvValStockBajo);
        tvValEnCamino = findViewById(R.id.tvValEnCamino);
        tvTotalVendido = findViewById(R.id.tvTotalVendido);

        tvAlertStockBajoText = findViewById(R.id.tvAlertStockBajoText);
        tvAlertEnCaminoText = findViewById(R.id.tvAlertEnCaminoText);

        cardProductos = findViewById(R.id.cardProductos);
        cardUsuarios = findViewById(R.id.cardUsuarios);
        cardPedidos = findViewById(R.id.cardPedidos);
        cardVentas = findViewById(R.id.cardVentas);
        cardStockBajo = findViewById(R.id.cardStockBajo);
        cardEnCamino = findViewById(R.id.cardEnCamino);

        cardQuickProductos = findViewById(R.id.cardQuickProductos);
        cardQuickPedidos = findViewById(R.id.cardQuickPedidos);
        cardQuickOfertas = findViewById(R.id.cardQuickOfertas);
        cardQuickStock = findViewById(R.id.cardQuickStock);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnAvatar = findViewById(R.id.btnAvatar);
    }

    private void cargarDatosUsuario() {
        SharedPreferences preferences = getSharedPreferences("AdisanPrefs", MODE_PRIVATE);
        String usuario = preferences.getString("usuario", "admin");

        // Formatear saludo
        if (tvGreeting != null) {
            tvGreeting.setText(getString(R.string.greeting_format, usuario));
        }

        // Configurar inicial en el avatar si existe la vista
        if (tvAvatarInitial != null && !TextUtils.isEmpty(usuario)) {
            tvAvatarInitial.setText(usuario.substring(0, 1).toUpperCase());
            tvAvatarInitial.setVisibility(View.VISIBLE);
            if (btnAvatar != null) {
                btnAvatar.setVisibility(View.GONE);
            }
        }

        // Formatear fecha actual
        if (tvSubGreeting != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es", "ES"));
            String fechaActual = sdf.format(new Date());
            if (fechaActual.length() > 0) {
                fechaActual = fechaActual.substring(0, 1).toUpperCase() + fechaActual.substring(1);
            }
            tvSubGreeting.setText("Todo bajo control hoy • " + fechaActual);
        }
    }

    private void obtenerDatosDashboard() {
        SharedPreferences preferences = getSharedPreferences("AdisanPrefs", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        String authHeader = !TextUtils.isEmpty(token) ? "Bearer " + token : "";

        ApiClient.getApiService().getDashboardData(authHeader).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardResponse data = response.body();
                    if (data.isSuccess()) {
                        actualizarMetricas(data);
                    } else {
                        String msg = data.getMessage() != null ? data.getMessage() : "Error al obtener datos del Dashboard";
                        Toast.makeText(DashboardActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, "Respuesta no exitosa (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                Toast.makeText(DashboardActivity.this,
                        "Error de conexión con el backend (/api/dashboard)",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarMetricas(DashboardResponse data) {
        if (tvValProductos != null) {
            tvValProductos.setText(String.format(Locale.getDefault(), "%,d", data.getProductos()));
        }
        if (tvValUsuarios != null) {
            tvValUsuarios.setText(String.format(Locale.getDefault(), "%,d", data.getUsuarios()));
        }
        if (tvValPedidos != null) {
            tvValPedidos.setText(String.format(Locale.getDefault(), "%,d", data.getPedidos()));
        }
        if (tvValVentas != null) {
            tvValVentas.setText(String.format(Locale.US, "$%,.2f", data.getVentas()));
        }
        if (tvValStockBajo != null) {
            tvValStockBajo.setText(String.format(Locale.getDefault(), "%,d", data.getStockBajo()));
        }
        if (tvValEnCamino != null) {
            tvValEnCamino.setText(String.format(Locale.getDefault(), "%,d", data.getEnCamino()));
        }
        if (tvTotalVendido != null) {
            tvTotalVendido.setText(String.format(Locale.US, "$%,.2f", data.getTotalVendido()));
        }

        // Actualizar alertas
        if (tvAlertStockBajoText != null) {
            if (data.getStockBajo() == 0) {
                tvAlertStockBajoText.setText("Sin productos con stock bajo");
            } else {
                tvAlertStockBajoText.setText(data.getStockBajo() + " productos con stock bajo que requieren atención");
            }
        }

        if (tvAlertEnCaminoText != null) {
            tvAlertEnCaminoText.setText(data.getEnCamino() + " pedidos actualmente en ruta de despacho");
        }
    }

    private void setupListeners() {
        // Listener de la barra de navegación inferior
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
            bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_dashboard) {
                        return true;
                    } else if (itemId == R.id.nav_productos) {
                        Intent intent = new Intent(DashboardActivity.this, ProductosActivity.class);
                        startActivity(intent);
                        return true;
                    } else if (itemId == R.id.nav_pedidos) {
                        Intent intent = new Intent(DashboardActivity.this, PedidosActivity.class);
                        startActivity(intent);
                        return true;
                    } else if (itemId == R.id.nav_ventas) {
                        Intent intent = new Intent(DashboardActivity.this, VentasActivity.class);
                        startActivity(intent);
                        return true;
                    } else if (itemId == R.id.nav_mas) {
                        Toast.makeText(DashboardActivity.this, "Próximamente", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
        }

        // Click listeners en Accesos Rápidos y Tarjetas
        View.OnClickListener quickAccessListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.cardQuickProductos || id == R.id.cardProductos) {
                    Intent intent = new Intent(DashboardActivity.this, ProductosActivity.class);
                    startActivity(intent);
                } else if (id == R.id.cardQuickPedidos || id == R.id.cardPedidos) {
                    Intent intent = new Intent(DashboardActivity.this, PedidosActivity.class);
                    startActivity(intent);
                } else if (id == R.id.cardQuickOfertas || id == R.id.cardVentas) {
                    Intent intent = new Intent(DashboardActivity.this, VentasActivity.class);
                    startActivity(intent);
                } else if (id == R.id.cardQuickStock || id == R.id.cardUsuarios) {
                    Toast.makeText(DashboardActivity.this, "Próximamente: Módulo Usuarios", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.cardStockBajo) {
                    Toast.makeText(DashboardActivity.this, "Próximamente: Alertas de Stock", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.cardEnCamino) {
                    Toast.makeText(DashboardActivity.this, "Próximamente: Despachos en camino", Toast.LENGTH_SHORT).show();
                }
            }
        };

        if (cardProductos != null) cardProductos.setOnClickListener(quickAccessListener);
        if (cardUsuarios != null) cardUsuarios.setOnClickListener(quickAccessListener);
        if (cardPedidos != null) cardPedidos.setOnClickListener(quickAccessListener);
        if (cardVentas != null) cardVentas.setOnClickListener(quickAccessListener);
        if (cardStockBajo != null) cardStockBajo.setOnClickListener(quickAccessListener);
        if (cardEnCamino != null) cardEnCamino.setOnClickListener(quickAccessListener);

        if (cardQuickProductos != null) cardQuickProductos.setOnClickListener(quickAccessListener);
        if (cardQuickPedidos != null) cardQuickPedidos.setOnClickListener(quickAccessListener);
        if (cardQuickOfertas != null) cardQuickOfertas.setOnClickListener(quickAccessListener);
        if (cardQuickStock != null) cardQuickStock.setOnClickListener(quickAccessListener);

        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v ->
                Toast.makeText(DashboardActivity.this, "Sin notificaciones pendientes", Toast.LENGTH_SHORT).show()
            );
        }

        View.OnClickListener avatarClickListener = v -> {
            SharedPreferences preferences = getSharedPreferences("AdisanPrefs", MODE_PRIVATE);
            String rol = preferences.getString("rol", "Usuario");
            Toast.makeText(DashboardActivity.this, "Perfil | Rol: " + rol, Toast.LENGTH_SHORT).show();
        };

        if (btnAvatar != null) btnAvatar.setOnClickListener(avatarClickListener);
        if (tvAvatarInitial != null) tvAvatarInitial.setOnClickListener(avatarClickListener);
    }
}
package com.example.adisan_app_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentasActivity extends AppCompatActivity {

    private ProgressBar pbLoading;
    private LinearLayout layoutVacio;
    private LinearLayout layoutError;
    private TextView tvMensajeError;
    private MaterialButton btnReintentar;
    private MaterialButton btnCrearPrimeraVenta;
    private MaterialButton btnNuevaVentaHeader;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvVentas;
    private VentaAdapter adapter;

    private ImageView btnVolver;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);

        // 1. Inicializar vistas
        initViews();

        // 2. Manejar insets para la barra de estado superior
        setupWindowInsets();

        // 3. Configurar RecyclerView y Adapter
        setupRecyclerView();

        // 4. Configurar listeners
        setupListeners();

        // 5. Cargar registro de ventas (obtenidas desde GET /api/pedidos)
        cargarVentas();
    }

    private void initViews() {
        pbLoading = findViewById(R.id.pbLoading);
        layoutVacio = findViewById(R.id.layoutVacio);
        layoutError = findViewById(R.id.layoutError);
        tvMensajeError = findViewById(R.id.tvMensajeError);
        btnReintentar = findViewById(R.id.btnReintentar);
        btnCrearPrimeraVenta = findViewById(R.id.btnCrearPrimeraVenta);
        btnNuevaVentaHeader = findViewById(R.id.btnNuevaVentaHeader);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        rvVentas = findViewById(R.id.rvVentas);
        btnVolver = findViewById(R.id.btnVolver);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupWindowInsets() {
        View headerBar = findViewById(R.id.headerBar);
        if (headerBar != null) {
            ViewCompat.setOnApplyWindowInsetsListener(headerBar, (v, insets) -> {
                Insets statusInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                int topPadding = statusInsets.top > 0 ? statusInsets.top + 16 : 36;
                v.setPadding(v.getPaddingLeft(), topPadding, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }
    }

    private void setupRecyclerView() {
        rvVentas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VentaAdapter(this, new ArrayList<>());
        rvVentas.setAdapter(adapter);
    }

    private void setupListeners() {
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        // El flujo real de crear una venta/operación es navegar a Productos para seleccionar items
        View.OnClickListener irAProductosListener = v -> {
            Intent intent = new Intent(VentasActivity.this, ProductosActivity.class);
            startActivity(intent);
        };

        if (btnNuevaVentaHeader != null) {
            btnNuevaVentaHeader.setOnClickListener(irAProductosListener);
        }

        if (btnCrearPrimeraVenta != null) {
            btnCrearPrimeraVenta.setOnClickListener(irAProductosListener);
        }

        if (btnReintentar != null) {
            btnReintentar.setOnClickListener(v -> cargarVentas());
        }

        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(R.color.blue_primary);
            swipeRefresh.setOnRefreshListener(this::cargarVentas);
        }

        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_ventas);
            bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_ventas) {
                        return true;
                    } else if (itemId == R.id.nav_dashboard) {
                        Intent intent = new Intent(VentasActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    } else if (itemId == R.id.nav_productos) {
                        Intent intent = new Intent(VentasActivity.this, ProductosActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    } else if (itemId == R.id.nav_pedidos) {
                        Intent intent = new Intent(VentasActivity.this, PedidosActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    } else if (itemId == R.id.nav_mas) {
                        Toast.makeText(VentasActivity.this, "Sección Más (Próximamente)", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void cargarVentas() {
        if (swipeRefresh != null && !swipeRefresh.isRefreshing()) {
            mostrarEstadoCarga();
        }

        // Obtener historial de ventas/operaciones consumiendo GET /api/pedidos
        ApiClient.getApiService().getPedidos().enqueue(new Callback<PedidoResponse>() {
            @Override
            public void onResponse(@NonNull Call<PedidoResponse> call, @NonNull Response<PedidoResponse> response) {
                ocultarSwipeRefresh();

                if (response.code() == 401) {
                    manejarSesionExpirada();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    PedidoResponse body = response.body();
                    List<Pedido> lista = body.getData();

                    if (lista != null && !lista.isEmpty()) {
                        mostrarListaVentas(lista);
                    } else {
                        mostrarEstadoVacio();
                    }
                } else {
                    // Fallback si el backend devuelve un arreglo directo JSON [ {...} ]
                    cargarVentasListaDirecta();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PedidoResponse> call, @NonNull Throwable t) {
                cargarVentasListaDirecta();
            }
        });
    }

    private void cargarVentasListaDirecta() {
        ApiClient.getApiService().getPedidosDirectList().enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(@NonNull Call<List<Pedido>> call, @NonNull Response<List<Pedido>> response) {
                ocultarSwipeRefresh();

                if (response.code() == 401) {
                    manejarSesionExpirada();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Pedido> lista = response.body();
                    if (!lista.isEmpty()) {
                        mostrarListaVentas(lista);
                    } else {
                        mostrarEstadoVacio();
                    }
                } else {
                    mostrarEstadoError("Error en el servidor (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Pedido>> call, @NonNull Throwable t) {
                ocultarSwipeRefresh();
                mostrarEstadoError("Error de conexión al servidor (10.0.2.2:3000). Verifica que el backend esté ejecutándose.");
            }
        });
    }

    // =======================================================
    // MANEJO DE ESTADOS DE LA UI
    // =======================================================

    private void mostrarEstadoCarga() {
        if (pbLoading != null) pbLoading.setVisibility(View.VISIBLE);
        if (rvVentas != null) rvVentas.setVisibility(View.GONE);
        if (layoutVacio != null) layoutVacio.setVisibility(View.GONE);
        if (layoutError != null) layoutError.setVisibility(View.GONE);
    }

    private void ocultarSwipeRefresh() {
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    private void mostrarListaVentas(List<Pedido> pedidos) {
        if (pbLoading != null) pbLoading.setVisibility(View.GONE);
        if (layoutVacio != null) layoutVacio.setVisibility(View.GONE);
        if (layoutError != null) layoutError.setVisibility(View.GONE);
        if (rvVentas != null) {
            rvVentas.setVisibility(View.VISIBLE);
            adapter.setPedidos(pedidos);
        }
    }

    private void mostrarEstadoVacio() {
        if (pbLoading != null) pbLoading.setVisibility(View.GONE);
        if (rvVentas != null) rvVentas.setVisibility(View.GONE);
        if (layoutError != null) layoutError.setVisibility(View.GONE);
        if (layoutVacio != null) layoutVacio.setVisibility(View.VISIBLE);
    }

    private void mostrarEstadoError(String mensaje) {
        if (pbLoading != null) pbLoading.setVisibility(View.GONE);
        if (rvVentas != null) rvVentas.setVisibility(View.GONE);
        if (layoutVacio != null) layoutVacio.setVisibility(View.GONE);
        if (layoutError != null) {
            layoutError.setVisibility(View.VISIBLE);
            if (tvMensajeError != null) {
                tvMensajeError.setText(mensaje);
            }
        }
    }

    private void manejarSesionExpirada() {
        Toast.makeText(this, "Tu sesión ha expirado. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();

        SharedPreferences preferences = getSharedPreferences("AdisanPrefs", MODE_PRIVATE);
        preferences.edit().clear().apply();

        Intent intent = new Intent(VentasActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
package com.example.adisan_app_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidosActivity extends AppCompatActivity {

    private ProgressBar pbLoading;
    private LinearLayout layoutVacio;
    private LinearLayout layoutError;
    private TextView tvMensajeError;
    private MaterialButton btnReintentar;
    private MaterialButton btnCrearPrimerPedido;
    private MaterialButton btnNuevoPedidoHeader;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvPedidos;
    private PedidoAdapter adapter;

    private ImageView btnVolver;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        // 1. Inicializar vistas
        initViews();

        // 2. Manejar insets para que el header respete la barra de estado superior
        setupWindowInsets();

        // 3. Configurar RecyclerView y Adapter
        setupRecyclerView();

        // 4. Configurar listeners
        setupListeners();

        // 5. Cargar pedidos desde el backend
        cargarPedidos();
    }

    private void initViews() {
        pbLoading = findViewById(R.id.pbLoading);
        layoutVacio = findViewById(R.id.layoutVacio);
        layoutError = findViewById(R.id.layoutError);
        tvMensajeError = findViewById(R.id.tvMensajeError);
        btnReintentar = findViewById(R.id.btnReintentar);
        btnCrearPrimerPedido = findViewById(R.id.btnCrearPrimerPedido);
        btnNuevoPedidoHeader = findViewById(R.id.btnNuevoPedidoHeader);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        rvPedidos = findViewById(R.id.rvPedidos);
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
        rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PedidoAdapter(this, new ArrayList<>());
        rvPedidos.setAdapter(adapter);

        // Listener para acciones de Editar y Cancelar
        adapter.setOnPedidoActionListener(new PedidoAdapter.OnPedidoActionListener() {
            @Override
            public void onEditarEstado(Pedido pedido) {
                mostrarDialogoFormularioPedido(pedido);
            }

            @Override
            public void onCancelar(Pedido pedido) {
                mostrarDialogoConfirmarCancelar(pedido);
            }
        });
    }

    private void setupListeners() {
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        if (btnNuevoPedidoHeader != null) {
            btnNuevoPedidoHeader.setOnClickListener(v -> mostrarDialogoFormularioPedido(null));
        }

        if (btnCrearPrimerPedido != null) {
            btnCrearPrimerPedido.setOnClickListener(v -> mostrarDialogoFormularioPedido(null));
        }

        if (btnReintentar != null) {
            btnReintentar.setOnClickListener(v -> cargarPedidos());
        }

        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(R.color.blue_primary);
            swipeRefresh.setOnRefreshListener(this::cargarPedidos);
        }

        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_pedidos);
            bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_pedidos) {
                        return true;
                    } else if (itemId == R.id.nav_dashboard) {
                        Intent intent = new Intent(PedidosActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    } else if (itemId == R.id.nav_productos) {
                        Intent intent = new Intent(PedidosActivity.this, ProductosActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    } else if (itemId == R.id.nav_ventas) {
                        Intent intent = new Intent(PedidosActivity.this, VentasActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    } else if (itemId == R.id.nav_mas) {
                        Toast.makeText(PedidosActivity.this, "Sección Más (Próximamente)", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void cargarPedidos() {
        if (swipeRefresh != null && !swipeRefresh.isRefreshing()) {
            mostrarEstadoCarga();
        }

        // 1. Probar endpoint envuelto { "data": [ ... ] }
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
                        mostrarListaPedidos(lista);
                    } else {
                        mostrarEstadoVacio();
                    }
                } else {
                    // Fallback a lista directa si el JSON es un arreglo [ {...} ]
                    cargarPedidosListaDirecta();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PedidoResponse> call, @NonNull Throwable t) {
                cargarPedidosListaDirecta();
            }
        });
    }

    private void cargarPedidosListaDirecta() {
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
                        mostrarListaPedidos(lista);
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
    // FORMULARIO CREAR Y EDITAR PEDIDO
    // =======================================================

    private void mostrarDialogoFormularioPedido(Pedido pedidoExistente) {
        boolean esEdicion = pedidoExistente != null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pedido, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitlePedido);
        TextView tvSubtitle = dialogView.findViewById(R.id.tvDialogSubtitlePedido);

        TextInputLayout tilCliente = dialogView.findViewById(R.id.tilCliente);
        TextInputLayout tilTotal = dialogView.findViewById(R.id.tilTotal);
        TextInputLayout tilMetodoPago = dialogView.findViewById(R.id.tilMetodoPago);
        TextInputLayout tilEstadoPedido = dialogView.findViewById(R.id.tilEstadoPedido);

        TextInputEditText etClienteId = dialogView.findViewById(R.id.etClienteId);
        TextInputEditText etTotalPedido = dialogView.findViewById(R.id.etTotalPedido);
        TextInputEditText etMetodoPago = dialogView.findViewById(R.id.etMetodoPago);
        TextInputEditText etEstadoPedido = dialogView.findViewById(R.id.etEstadoPedido);

        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelarDialogPedido);
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btnGuardarDialogPedido);

        if (esEdicion) {
            if (tvTitle != null) tvTitle.setText("Editar Pedido #" + pedidoExistente.getId());
            if (tvSubtitle != null) tvSubtitle.setText("Modifica los datos o estado de la orden");

            if (etClienteId != null) etClienteId.setText(String.valueOf(pedidoExistente.getClienteId()));
            if (etTotalPedido != null) etTotalPedido.setText(pedidoExistente.getTotal());
            if (etMetodoPago != null) etMetodoPago.setText(pedidoExistente.getMetodoPago());
            if (etEstadoPedido != null) etEstadoPedido.setText(pedidoExistente.getEstado());
        } else {
            if (tvTitle != null) tvTitle.setText("Nuevo Pedido");
            if (tvSubtitle != null) tvSubtitle.setText("Ingresa los datos para registrar un nuevo pedido");
        }

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(v -> {
                if (tilCliente != null) tilCliente.setError(null);
                if (tilTotal != null) tilTotal.setError(null);
                if (tilMetodoPago != null) tilMetodoPago.setError(null);
                if (tilEstadoPedido != null) tilEstadoPedido.setError(null);

                String clienteIdStr = etClienteId != null && etClienteId.getText() != null ? etClienteId.getText().toString().trim() : "";
                String totalStr = etTotalPedido != null && etTotalPedido.getText() != null ? etTotalPedido.getText().toString().trim() : "";
                String metodoPago = etMetodoPago != null && etMetodoPago.getText() != null ? etMetodoPago.getText().toString().trim() : "Efectivo";
                String estado = etEstadoPedido != null && etEstadoPedido.getText() != null ? etEstadoPedido.getText().toString().trim() : "pendiente";

                boolean esValido = true;

                if (TextUtils.isEmpty(clienteIdStr)) {
                    if (tilCliente != null) tilCliente.setError("Ingresa el ID del cliente");
                    esValido = false;
                }

                if (TextUtils.isEmpty(totalStr)) {
                    if (tilTotal != null) tilTotal.setError("Ingresa el monto total");
                    esValido = false;
                }

                if (esValido) {
                    int clienteId = Integer.parseInt(clienteIdStr);

                    Pedido pedidoRequest = esEdicion ? pedidoExistente : new Pedido();
                    pedidoRequest.setClienteId(clienteId);
                    pedidoRequest.setTotal(totalStr);
                    pedidoRequest.setMetodoPago(metodoPago);
                    pedidoRequest.setEstado(estado);

                    if (esEdicion) {
                        ejecutarEditarPedido(pedidoExistente.getId(), pedidoRequest, dialog, btnGuardar);
                    } else {
                        ejecutarCrearPedido(pedidoRequest, dialog, btnGuardar);
                    }
                }
            });
        }

        dialog.show();
    }

    private void ejecutarCrearPedido(Pedido pedido, AlertDialog dialog, MaterialButton btnGuardar) {
        if (btnGuardar != null) btnGuardar.setEnabled(false);

        ApiClient.getApiService().crearPedido(pedido).enqueue(new Callback<PedidoResponse>() {
            @Override
            public void onResponse(@NonNull Call<PedidoResponse> call, @NonNull Response<PedidoResponse> response) {
                if (btnGuardar != null) btnGuardar.setEnabled(true);

                if (response.code() == 401) {
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    manejarSesionExpirada();
                    return;
                }

                if (response.isSuccessful()) {
                    Toast.makeText(PedidosActivity.this, "¡Pedido registrado correctamente!", Toast.LENGTH_SHORT).show();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    cargarPedidos();
                } else {
                    String msg = "Error al crear pedido (" + response.code() + ")";
                    if (response.errorBody() != null) {
                        try {
                            String errStr = response.errorBody().string();
                            PedidoResponse errRes = new Gson().fromJson(errStr, PedidoResponse.class);
                            if (errRes != null && errRes.getMessage() != null) {
                                msg = errRes.getMessage();
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    Toast.makeText(PedidosActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PedidoResponse> call, @NonNull Throwable t) {
                if (btnGuardar != null) btnGuardar.setEnabled(true);
                Toast.makeText(PedidosActivity.this, "Error de conexión al crear pedido", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ejecutarEditarPedido(int id, Pedido pedido, AlertDialog dialog, MaterialButton btnGuardar) {
        if (btnGuardar != null) btnGuardar.setEnabled(false);

        ApiClient.getApiService().editarPedido(id, pedido).enqueue(new Callback<PedidoResponse>() {
            @Override
            public void onResponse(@NonNull Call<PedidoResponse> call, @NonNull Response<PedidoResponse> response) {
                if (btnGuardar != null) btnGuardar.setEnabled(true);

                if (response.code() == 401) {
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    manejarSesionExpirada();
                    return;
                }

                if (response.isSuccessful()) {
                    Toast.makeText(PedidosActivity.this, "¡Pedido actualizado correctamente!", Toast.LENGTH_SHORT).show();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    cargarPedidos();
                } else {
                    String msg = "Error al actualizar pedido (" + response.code() + ")";
                    if (response.errorBody() != null) {
                        try {
                            String errStr = response.errorBody().string();
                            PedidoResponse errRes = new Gson().fromJson(errStr, PedidoResponse.class);
                            if (errRes != null && errRes.getMessage() != null) {
                                msg = errRes.getMessage();
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    Toast.makeText(PedidosActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PedidoResponse> call, @NonNull Throwable t) {
                if (btnGuardar != null) btnGuardar.setEnabled(true);
                Toast.makeText(PedidosActivity.this, "Error de conexión al actualizar pedido", Toast.LENGTH_LONG).show();
            }
        });
    }

    // =======================================================
    // CANCELAR / ELIMINAR PEDIDO
    // =======================================================

    private void mostrarDialogoConfirmarCancelar(Pedido pedido) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar Pedido")
                .setMessage("¿Estás seguro de cancelar o eliminar el pedido #" + pedido.getId() + "?")
                .setPositiveButton("Confirmar", (dialog, which) -> ejecutarEliminarPedido(pedido.getId()))
                .setNegativeButton("Volver", null)
                .show();
    }

    private void ejecutarEliminarPedido(int id) {
        mostrarEstadoCarga();

        ApiClient.getApiService().eliminarPedido(id).enqueue(new Callback<PedidoResponse>() {
            @Override
            public void onResponse(@NonNull Call<PedidoResponse> call, @NonNull Response<PedidoResponse> response) {
                if (response.code() == 401) {
                    manejarSesionExpirada();
                    return;
                }

                if (response.isSuccessful()) {
                    Toast.makeText(PedidosActivity.this, "¡Pedido procesado/cancelado correctamente!", Toast.LENGTH_SHORT).show();
                    cargarPedidos();
                } else {
                    Toast.makeText(PedidosActivity.this, "Error al procesar pedido (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    cargarPedidos();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PedidoResponse> call, @NonNull Throwable t) {
                Toast.makeText(PedidosActivity.this, "Error de conexión al procesar pedido", Toast.LENGTH_SHORT).show();
                cargarPedidos();
            }
        });
    }

    // =======================================================
    // MANEJO DE ESTADOS DE LA UI
    // =======================================================

    private void mostrarEstadoCarga() {
        if (pbLoading != null) pbLoading.setVisibility(View.VISIBLE);
        if (rvPedidos != null) rvPedidos.setVisibility(View.GONE);
        if (layoutVacio != null) layoutVacio.setVisibility(View.GONE);
        if (layoutError != null) layoutError.setVisibility(View.GONE);
    }

    private void ocultarSwipeRefresh() {
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    private void mostrarListaPedidos(List<Pedido> pedidos) {
        if (pbLoading != null) pbLoading.setVisibility(View.GONE);
        if (layoutVacio != null) layoutVacio.setVisibility(View.GONE);
        if (layoutError != null) layoutError.setVisibility(View.GONE);
        if (rvPedidos != null) {
            rvPedidos.setVisibility(View.VISIBLE);
            adapter.setPedidos(pedidos);
        }
    }

    private void mostrarEstadoVacio() {
        if (pbLoading != null) pbLoading.setVisibility(View.GONE);
        if (rvPedidos != null) rvPedidos.setVisibility(View.GONE);
        if (layoutError != null) layoutError.setVisibility(View.GONE);
        if (layoutVacio != null) layoutVacio.setVisibility(View.VISIBLE);
    }

    private void mostrarEstadoError(String mensaje) {
        if (pbLoading != null) pbLoading.setVisibility(View.GONE);
        if (rvPedidos != null) rvPedidos.setVisibility(View.GONE);
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

        Intent intent = new Intent(PedidosActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
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
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosActivity extends AppCompatActivity {

    private ProgressBar pbLoading;
    private LinearLayout layoutVacio;
    private LinearLayout layoutError;
    private TextView tvMensajeError;
    private MaterialButton btnReintentar;
    private MaterialButton btnCrearPrimerProducto;
    private MaterialButton btnNuevoProductoHeader;
    private MaterialButton btnVerCarrito;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvProductos;
    private ProductoAdapter adapter;

    private ImageView btnVolver;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        // 1. Inicializar vistas
        initViews();

        // 2. Manejar insets para que el header respete la barra de estado superior
        setupWindowInsets();

        // 3. Configurar RecyclerView y Adapter
        setupRecyclerView();

        // 4. Configurar listeners
        setupListeners();

        // 5. Configurar títulos según el Rol del usuario
        configurarTituloPorRol();

        // 6. Cargar productos desde el backend
        cargarProductos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refrescar automáticamente la lista al retomar foco
        cargarProductos();
    }

    private void configurarTituloPorRol() {
        SharedPreferences preferences = getSharedPreferences("AdisanPrefs", MODE_PRIVATE);
        String rol = preferences.getString("rol", "admin");

        TextView tvTitle = findViewById(R.id.tvHeaderTitle);
        if (tvTitle != null) {
            if ("cliente".equalsIgnoreCase(rol)) {
                tvTitle.setText("Comprar productos");
            } else {
                tvTitle.setText("Vender productos");
            }
        }
    }

    private void initViews() {
        pbLoading = findViewById(R.id.pbLoading);
        layoutVacio = findViewById(R.id.layoutVacio);
        layoutError = findViewById(R.id.layoutError);
        tvMensajeError = findViewById(R.id.tvMensajeError);
        btnReintentar = findViewById(R.id.btnReintentar);
        btnCrearPrimerProducto = findViewById(R.id.btnCrearPrimerProducto);
        btnNuevoProductoHeader = findViewById(R.id.btnNuevoProductoHeader);
        btnVerCarrito = findViewById(R.id.btnVerCarrito);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        rvProductos = findViewById(R.id.rvProductos);
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
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductoAdapter(this, new ArrayList<>());
        rvProductos.setAdapter(adapter);

        // Listener para acciones de Carrito, Editar y Eliminar
        adapter.setOnProductoActionListener(new ProductoAdapter.OnProductoActionListener() {
            @Override
            public void onEditar(Producto producto) {
                mostrarDialogoFormularioProducto(producto);
            }

            @Override
            public void onEliminar(Producto producto) {
                mostrarDialogoConfirmarEliminar(producto);
            }

            @Override
            public void onAgregarCarrito(Producto producto) {
                if (producto.getStock() <= 0) {
                    Toast.makeText(ProductosActivity.this, "Producto sin stock disponible", Toast.LENGTH_SHORT).show();
                    return;
                }
                CarritoManager.getInstance().agregarProducto(producto, 1);
                Toast.makeText(ProductosActivity.this, "¡" + producto.getProductoNombre() + " agregado al carrito!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        if (btnNuevoProductoHeader != null) {
            btnNuevoProductoHeader.setOnClickListener(v -> mostrarDialogoFormularioProducto(null));
        }

        if (btnVerCarrito != null) {
            btnVerCarrito.setOnClickListener(v -> {
                Intent intent = new Intent(ProductosActivity.this, CarritoActivity.class);
                startActivity(intent);
            });
        }

        if (btnCrearPrimerProducto != null) {
            btnCrearPrimerProducto.setOnClickListener(v -> mostrarDialogoFormularioProducto(null));
        }

        if (btnReintentar != null) {
            btnReintentar.setOnClickListener(v -> cargarProductos());
        }

        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(R.color.blue_primary);
            swipeRefresh.setOnRefreshListener(this::cargarProductos);
        }

        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_productos);
            bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_productos) {
                        return true;
                    } else if (itemId == R.id.nav_dashboard) {
                        Intent intent = new Intent(ProductosActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    } else if (itemId == R.id.nav_pedidos) {
                        Intent intent = new Intent(ProductosActivity.this, PedidosActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    } else if (itemId == R.id.nav_mas) {
                        Toast.makeText(ProductosActivity.this, "Sección Más (Próximamente)", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void cargarProductos() {
        if (swipeRefresh != null && !swipeRefresh.isRefreshing()) {
            mostrarEstadoCarga();
        }

        // 1. Probar endpoint envuelto { "data": [ ... ] }
        ApiClient.getApiService().getProductos().enqueue(new Callback<ProductoResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductoResponse> call, @NonNull Response<ProductoResponse> response) {
                ocultarSwipeRefresh();

                if (response.code() == 401) {
                    manejarSesionExpirada();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    ProductoResponse body = response.body();
                    List<Producto> lista = body.getData();

                    if (lista != null && !lista.isEmpty()) {
                        mostrarListaProductos(lista);
                    } else {
                        mostrarEstadoVacio();
                    }
                } else {
                    // Fallback a lista directa si el JSON es un arreglo [ {...} ]
                    cargarProductosListaDirecta();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductoResponse> call, @NonNull Throwable t) {
                cargarProductosListaDirecta();
            }
        });
    }

    private void cargarProductosListaDirecta() {
        ApiClient.getApiService().getProductosDirectList().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(@NonNull Call<List<Producto>> call, @NonNull Response<List<Producto>> response) {
                ocultarSwipeRefresh();

                if (response.code() == 401) {
                    manejarSesionExpirada();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> lista = response.body();
                    if (!lista.isEmpty()) {
                        mostrarListaProductos(lista);
                    } else {
                        mostrarEstadoVacio();
                    }
                } else {
                    mostrarEstadoError("Error en el servidor (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Producto>> call, @NonNull Throwable t) {
                ocultarSwipeRefresh();
                mostrarEstadoError("Error de conexión al servidor (10.0.2.2:3000). Verifica que el backend esté ejecutándose.");
            }
        });
    }

    // =======================================================
    // FORMULARIO CREAR Y EDITAR PRODUCTO
    // =======================================================

    private void mostrarDialogoFormularioProducto(Producto productoExistente) {
        boolean esEdicion = productoExistente != null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_producto, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvSubtitle = dialogView.findViewById(R.id.tvDialogSubtitle);

        TextInputLayout tilNombre = dialogView.findViewById(R.id.tilNombre);
        TextInputLayout tilStock = dialogView.findViewById(R.id.tilStock);
        TextInputLayout tilPrecioCompra = dialogView.findViewById(R.id.tilPrecioCompra);
        TextInputLayout tilPrecioVenta = dialogView.findViewById(R.id.tilPrecioVenta);

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombre);
        TextInputEditText etStock = dialogView.findViewById(R.id.etStock);
        TextInputEditText etFactor = dialogView.findViewById(R.id.etFactor);
        TextInputEditText etPrecioCompra = dialogView.findViewById(R.id.etPrecioCompra);
        TextInputEditText etPrecioVenta = dialogView.findViewById(R.id.etPrecioVenta);

        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelarDialog);
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btnGuardarDialog);

        if (esEdicion) {
            if (tvTitle != null) tvTitle.setText("Editar Producto");
            if (tvSubtitle != null) tvSubtitle.setText("Modifica los datos del producto #" + productoExistente.getProductoId());

            if (etNombre != null) etNombre.setText(productoExistente.getProductoNombre());
            if (etStock != null) etStock.setText(String.valueOf(productoExistente.getStock()));
            if (etFactor != null && productoExistente.getFactor() != null) {
                etFactor.setText(String.valueOf(productoExistente.getFactor()));
            }
            if (etPrecioCompra != null) etPrecioCompra.setText(productoExistente.getPrecioCompra());
            if (etPrecioVenta != null) etPrecioVenta.setText(productoExistente.getPrecioVenta());
        } else {
            if (tvTitle != null) tvTitle.setText("Nuevo Producto");
            if (tvSubtitle != null) tvSubtitle.setText("Ingresa los datos para registrar un nuevo producto");
        }

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(v -> {
                if (tilNombre != null) tilNombre.setError(null);
                if (tilStock != null) tilStock.setError(null);
                if (tilPrecioCompra != null) tilPrecioCompra.setError(null);
                if (tilPrecioVenta != null) tilPrecioVenta.setError(null);

                String nombre = etNombre != null && etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
                String stockStr = etStock != null && etStock.getText() != null ? etStock.getText().toString().trim() : "";
                String factorStr = etFactor != null && etFactor.getText() != null ? etFactor.getText().toString().trim() : "";
                String precioCompraStr = etPrecioCompra != null && etPrecioCompra.getText() != null ? etPrecioCompra.getText().toString().trim() : "";
                String precioVentaStr = etPrecioVenta != null && etPrecioVenta.getText() != null ? etPrecioVenta.getText().toString().trim() : "";

                boolean esValido = true;

                if (TextUtils.isEmpty(nombre)) {
                    if (tilNombre != null) tilNombre.setError("Ingresa el nombre del producto");
                    esValido = false;
                }

                if (TextUtils.isEmpty(stockStr)) {
                    if (tilStock != null) tilStock.setError("Ingresa el stock");
                    esValido = false;
                }

                if (TextUtils.isEmpty(precioCompraStr)) {
                    if (tilPrecioCompra != null) tilPrecioCompra.setError("Ingresa precio de compra");
                    esValido = false;
                }

                if (TextUtils.isEmpty(precioVentaStr)) {
                    if (tilPrecioVenta != null) tilPrecioVenta.setError("Ingresa precio de venta");
                    esValido = false;
                }

                if (esValido) {
                    int stock = Integer.parseInt(stockStr);
                    int factor = !TextUtils.isEmpty(factorStr) ? Integer.parseInt(factorStr) : 1;

                    Producto productoRequest = esEdicion ? productoExistente : new Producto();
                    productoRequest.setProductoNombre(nombre);
                    productoRequest.setStock(stock);
                    productoRequest.setFactor(factor);
                    productoRequest.setPrecioCompra(precioCompraStr);
                    productoRequest.setPrecioVenta(precioVentaStr);
                    productoRequest.setActivo(1);
                    productoRequest.setProveedorId(1);
                    productoRequest.setCategoriaId(1);
                    if (!esEdicion) {
                        productoRequest.setPresentacionId(1);
                        productoRequest.setNivel(1);
                    }

                    if (esEdicion) {
                        ejecutarEditarProducto(productoExistente.getProductoId(), productoRequest, dialog, btnGuardar);
                    } else {
                        ejecutarCrearProducto(productoRequest, dialog, btnGuardar);
                    }
                }
            });
        }

        dialog.show();
    }

    private void ejecutarCrearProducto(Producto producto, AlertDialog dialog, MaterialButton btnGuardar) {
        if (btnGuardar != null) btnGuardar.setEnabled(false);

        ApiClient.getApiService().crearProducto(producto).enqueue(new Callback<ProductoResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductoResponse> call, @NonNull Response<ProductoResponse> response) {
                if (btnGuardar != null) btnGuardar.setEnabled(true);

                if (response.code() == 401) {
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    manejarSesionExpirada();
                    return;
                }

                if (response.isSuccessful()) {
                    Toast.makeText(ProductosActivity.this, "¡Producto creado correctamente!", Toast.LENGTH_SHORT).show();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    cargarProductos();
                } else {
                    String msg = "Error al crear producto (" + response.code() + ")";
                    if (response.errorBody() != null) {
                        try {
                            String errStr = response.errorBody().string();
                            ProductoResponse errRes = new Gson().fromJson(errStr, ProductoResponse.class);
                            if (errRes != null && errRes.getMessage() != null) {
                                msg = errRes.getMessage();
                            } else {
                                msg = "Error: " + errStr;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    Toast.makeText(ProductosActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductoResponse> call, @NonNull Throwable t) {
                if (btnGuardar != null) btnGuardar.setEnabled(true);
                Toast.makeText(ProductosActivity.this, "Error de conexión al crear producto: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ejecutarEditarProducto(int id, Producto producto, AlertDialog dialog, MaterialButton btnGuardar) {
        if (btnGuardar != null) btnGuardar.setEnabled(false);

        ApiClient.getApiService().editarProducto(id, producto).enqueue(new Callback<ProductoResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductoResponse> call, @NonNull Response<ProductoResponse> response) {
                if (btnGuardar != null) btnGuardar.setEnabled(true);

                if (response.code() == 401) {
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    manejarSesionExpirada();
                    return;
                }

                if (response.isSuccessful()) {
                    Toast.makeText(ProductosActivity.this, "¡Producto actualizado correctamente!", Toast.LENGTH_SHORT).show();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    cargarProductos();
                } else {
                    String msg = "Error al actualizar producto (" + response.code() + ")";
                    if (response.errorBody() != null) {
                        try {
                            String errStr = response.errorBody().string();
                            ProductoResponse errRes = new Gson().fromJson(errStr, ProductoResponse.class);
                            if (errRes != null && errRes.getMessage() != null) {
                                msg = errRes.getMessage();
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    Toast.makeText(ProductosActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductoResponse> call, @NonNull Throwable t) {
                if (btnGuardar != null) btnGuardar.setEnabled(true);
                Toast.makeText(ProductosActivity.this, "Error de conexión al actualizar producto", Toast.LENGTH_LONG).show();
            }
        });
    }

    // =======================================================
    // ELIMINAR PRODUCTO
    // =======================================================

    private void mostrarDialogoConfirmarEliminar(Producto producto) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Producto")
                .setMessage("¿Estás seguro de eliminar el producto \"" + producto.getProductoNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> ejecutarEliminarProducto(producto.getProductoId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void ejecutarEliminarProducto(int id) {
        mostrarEstadoCarga();

        ApiClient.getApiService().eliminarProducto(id).enqueue(new Callback<ProductoResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductoResponse> call, @NonNull Response<ProductoResponse> response) {
                if (response.code() == 401) {
                    manejarSesionExpirada();
                    return;
                }

                if (response.isSuccessful()) {
                    Toast.makeText(ProductosActivity.this, "¡Producto eliminado correctamente!", Toast.LENGTH_SHORT).show();
                    cargarProductos();
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al eliminar producto (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    cargarProductos();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductoResponse> call, @NonNull Throwable t) {
                Toast.makeText(ProductosActivity.this, "Error de conexión al eliminar producto", Toast.LENGTH_SHORT).show();
                cargarProductos();
            }
        });
    }

    // =======================================================
    // MANEJO DE ESTADOS DE LA UI
    // =======================================================

    private void mostrarEstadoCarga() {
        if (pbLoading != null) pbLoading.setVisibility(View.VISIBLE);
        if (rvProductos != null) rvProductos.setVisibility(View.GONE);
        if (layoutVacio != null) layoutVacio.setVisibility(View.GONE);
        if (layoutError != null) layoutError.setVisibility(View.GONE);
    }

    private void ocultarSwipeRefresh() {
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    private void mostrarListaProductos(List<Producto> productos) {
        if (pbLoading != null) pbLoading.setVisibility(View.GONE);
        if (layoutVacio != null) layoutVacio.setVisibility(View.GONE);
        if (layoutError != null) layoutError.setVisibility(View.GONE);

        if (rvProductos != null && productos != null) {
            rvProductos.setVisibility(View.VISIBLE);

            // Ordenar por ID descendente para que el nuevo producto creado aparezca primero en la parte superior
            Collections.sort(productos, (p1, p2) -> Integer.compare(p2.getProductoId(), p1.getProductoId()));

            if (adapter == null) {
                adapter = new ProductoAdapter(this, productos);
                rvProductos.setAdapter(adapter);
            } else {
                adapter.setProductos(productos);
            }
            rvProductos.scrollToPosition(0);
        }
    }

    private void mostrarEstadoVacio() {
        if (pbLoading != null) pbLoading.setVisibility(View.GONE);
        if (rvProductos != null) rvProductos.setVisibility(View.GONE);
        if (layoutError != null) layoutError.setVisibility(View.GONE);
        if (layoutVacio != null) layoutVacio.setVisibility(View.VISIBLE);
    }

    private void mostrarEstadoError(String mensaje) {
        if (pbLoading != null) pbLoading.setVisibility(View.GONE);
        if (rvProductos != null) rvProductos.setVisibility(View.GONE);
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

        Intent intent = new Intent(ProductosActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
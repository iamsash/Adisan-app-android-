package com.example.adisan_app_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarritoActivity extends AppCompatActivity {

    private ImageView btnVolver;
    private RecyclerView rvCarrito;
    private LinearLayout layoutVacio;
    private MaterialButton btnIrACatalogo;
    private MaterialButton btnConfirmarPedido;
    private TextInputLayout tilMetodoPago;
    private TextInputEditText etMetodoPago;
    private TextView tvTotalCalculado;

    private CarritoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        initViews();
        setupRecyclerView();
        setupListeners();
        actualizarVista();
    }

    private void initViews() {
        btnVolver = findViewById(R.id.btnVolverCarrito);
        rvCarrito = findViewById(R.id.rvCarrito);
        layoutVacio = findViewById(R.id.layoutCarritoVacio);
        btnIrACatalogo = findViewById(R.id.btnIrACatalogo);
        btnConfirmarPedido = findViewById(R.id.btnConfirmarPedidoCarrito);
        tilMetodoPago = findViewById(R.id.tilMetodoPagoCarrito);
        etMetodoPago = findViewById(R.id.etMetodoPagoCarrito);
        tvTotalCalculado = findViewById(R.id.tvTotalCarritoCalculado);
    }

    private void setupRecyclerView() {
        rvCarrito.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CarritoAdapter(this, CarritoManager.getInstance().getItems(), new CarritoAdapter.OnCarritoChangeListener() {
            @Override
            public void onChange() {
                actualizarVista();
            }
        });
        rvCarrito.setAdapter(adapter);
    }

    private void setupListeners() {
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        if (btnIrACatalogo != null) {
            btnIrACatalogo.setOnClickListener(v -> finish());
        }

        if (btnConfirmarPedido != null) {
            btnConfirmarPedido.setOnClickListener(v -> ejecutarConfirmarPedido());
        }
    }

    private void actualizarVista() {
        List<ItemCarrito> items = CarritoManager.getInstance().getItems();
        if (items.isEmpty()) {
            if (layoutVacio != null) layoutVacio.setVisibility(View.VISIBLE);
            if (rvCarrito != null) rvCarrito.setVisibility(View.GONE);
            if (btnConfirmarPedido != null) btnConfirmarPedido.setEnabled(false);
            if (tvTotalCalculado != null) tvTotalCalculado.setText("S/ 0.00");
        } else {
            if (layoutVacio != null) layoutVacio.setVisibility(View.GONE);
            if (rvCarrito != null) rvCarrito.setVisibility(View.VISIBLE);
            if (btnConfirmarPedido != null) btnConfirmarPedido.setEnabled(true);

            double total = CarritoManager.getInstance().obtenerTotal();
            if (tvTotalCalculado != null) {
                tvTotalCalculado.setText(String.format(Locale.US, "S/ %.2f", total));
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void ejecutarConfirmarPedido() {
        List<ItemCarrito> items = CarritoManager.getInstance().getItems();
        if (items.isEmpty()) {
            Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        String metodoPago = etMetodoPago != null && etMetodoPago.getText() != null
                ? etMetodoPago.getText().toString().trim()
                : "Yape";

        if (TextUtils.isEmpty(metodoPago)) {
            if (tilMetodoPago != null) tilMetodoPago.setError("Ingresa método de pago");
            return;
        }

        SharedPreferences preferences = getSharedPreferences("AdisanPrefs", MODE_PRIVATE);
        int clienteId = preferences.getInt("id", 1);

        double total = CarritoManager.getInstance().obtenerTotal();

        Pedido pedidoRequest = new Pedido();
        pedidoRequest.setClienteId(clienteId);
        pedidoRequest.setTotal(String.format(Locale.US, "%.2f", total));
        pedidoRequest.setMetodoPago(metodoPago);
        pedidoRequest.setEstado("pendiente");

        List<DetallePedido> detalles = new ArrayList<>();
        for (ItemCarrito item : items) {
            DetallePedido dp = new DetallePedido();
            dp.setProductoId(item.getProductoId());
            dp.setPresentacionId(item.getPresentacionId());
            dp.setCantidad(item.getCantidad());
            dp.setPrecio(item.getPrecio());
            dp.setSubtotal(String.format(Locale.US, "%.2f", item.getSubtotal()));
            detalles.add(dp);
        }
        pedidoRequest.setDetalles(detalles);

        if (btnConfirmarPedido != null) btnConfirmarPedido.setEnabled(false);

        ApiClient.getApiService().crearPedido(pedidoRequest).enqueue(new Callback<PedidoResponse>() {
            @Override
            public void onResponse(@NonNull Call<PedidoResponse> call, @NonNull Response<PedidoResponse> response) {
                if (btnConfirmarPedido != null) btnConfirmarPedido.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(CarritoActivity.this, "¡Pedido registrado correctamente!", Toast.LENGTH_SHORT).show();
                    CarritoManager.getInstance().limpiar();

                    Intent intent = new Intent(CarritoActivity.this, PedidosActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String msg = "Error al crear pedido";
                    if (response.body() != null && response.body().getMessage() != null) {
                        msg = response.body().getMessage();
                    }
                    Toast.makeText(CarritoActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PedidoResponse> call, @NonNull Throwable t) {
                if (btnConfirmarPedido != null) btnConfirmarPedido.setEnabled(true);
                Toast.makeText(CarritoActivity.this, "Error de conexión al enviar el pedido", Toast.LENGTH_LONG).show();
            }
        });
    }

    // =======================================================
    // ADAPTER RECYCLERVIEW DEL CARRITO
    // =======================================================

    private static class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

        interface OnCarritoChangeListener {
            void onChange();
        }

        private final Context context;
        private final List<ItemCarrito> items;
        private final OnCarritoChangeListener listener;

        public CarritoAdapter(Context context, List<ItemCarrito> items, OnCarritoChangeListener listener) {
            this.context = context;
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_carrito, parent, false);
            return new CarritoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
            ItemCarrito item = items.get(position);

            holder.tvNombre.setText(item.getProductoNombre());
            holder.tvPrecio.setText(String.format(Locale.US, "S/ %.2f c/u", item.getPrecioDouble()));
            holder.tvCantidad.setText(String.valueOf(item.getCantidad()));
            holder.tvSubtotal.setText(String.format(Locale.US, "Subtotal: S/ %.2f", item.getSubtotal()));

            holder.btnMas.setOnClickListener(v -> {
                if (item.getCantidad() < item.getStock()) {
                    item.setCantidad(item.getCantidad() + 1);
                    notifyItemChanged(position);
                    if (listener != null) listener.onChange();
                } else {
                    Toast.makeText(context, "Stock máximo alcanzado (" + item.getStock() + ")", Toast.LENGTH_SHORT).show();
                }
            });

            holder.btnMenos.setOnClickListener(v -> {
                if (item.getCantidad() > 1) {
                    item.setCantidad(item.getCantidad() - 1);
                    notifyItemChanged(position);
                    if (listener != null) listener.onChange();
                } else {
                    CarritoManager.getInstance().eliminarItem(position);
                    notifyDataSetChanged();
                    if (listener != null) listener.onChange();
                }
            });

            holder.btnEliminar.setOnClickListener(v -> {
                CarritoManager.getInstance().eliminarItem(position);
                notifyDataSetChanged();
                if (listener != null) listener.onChange();
            });
        }

        @Override
        public int getItemCount() {
            return items != null ? items.size() : 0;
        }

        static class SeleccionViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombre;
            TextView tvPrecio;
            TextView tvSubtotal;
            TextView tvCantidad;
            MaterialButton btnMenos;
            MaterialButton btnMas;
            ImageView btnEliminar;

            public SeleccionViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tvNombreItemCarrito);
                tvPrecio = itemView.findViewById(R.id.tvPrecioItemCarrito);
                tvSubtotal = itemView.findViewById(R.id.tvSubtotalItemCarrito);
                tvCantidad = itemView.findViewById(R.id.tvCantidadCarrito);
                btnMenos = itemView.findViewById(R.id.btnMenosCarrito);
                btnMas = itemView.findViewById(R.id.btnMasCarrito);
                btnEliminar = itemView.findViewById(R.id.btnEliminarCarrito);
            }
        }

        static class CarritoViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombre;
            TextView tvPrecio;
            TextView tvSubtotal;
            TextView tvCantidad;
            MaterialButton btnMenos;
            MaterialButton btnMas;
            ImageView btnEliminar;

            public CarritoViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tvNombreItemCarrito);
                tvPrecio = itemView.findViewById(R.id.tvPrecioItemCarrito);
                tvSubtotal = itemView.findViewById(R.id.tvSubtotalItemCarrito);
                tvCantidad = itemView.findViewById(R.id.tvCantidadCarrito);
                btnMenos = itemView.findViewById(R.id.btnMenosCarrito);
                btnMas = itemView.findViewById(R.id.btnMasCarrito);
                btnEliminar = itemView.findViewById(R.id.btnEliminarCarrito);
            }
        }
    }
}
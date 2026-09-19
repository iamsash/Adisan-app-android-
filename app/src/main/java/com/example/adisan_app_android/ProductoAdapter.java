package com.example.adisan_app_android;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    public interface OnProductoActionListener {
        void onEditar(Producto producto);
        void onEliminar(Producto producto);
        void onAgregarCarrito(Producto producto);
    }

    private final Context context;
    private List<Producto> productoList;
    private OnProductoActionListener listener;

    public ProductoAdapter(Context context, List<Producto> productoList) {
        this.context = context;
        this.productoList = productoList != null ? new ArrayList<>(productoList) : new ArrayList<>();
    }

    public void setOnProductoActionListener(OnProductoActionListener listener) {
        this.listener = listener;
    }

    public void setProductos(List<Producto> productos) {
        this.productoList = productos != null ? new ArrayList<>(productos) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productoList.get(position);

        holder.tvIdProducto.setText("ID: #" + producto.getProductoId());
        holder.tvNombre.setText(producto.getProductoNombre());

        if (producto.getActivo() == 1) {
            holder.tvEstado.setText("Activo");
            holder.tvEstado.setTextColor(Color.parseColor("#059669"));
        } else {
            holder.tvEstado.setText("Inactivo");
            holder.tvEstado.setTextColor(Color.parseColor("#DC2626"));
        }

        int stock = producto.getStock();
        holder.tvStock.setText(stock + " un.");

        if (stock <= 5) {
            holder.tvStock.setTextColor(Color.parseColor("#DC2626"));
        } else if (stock <= 15) {
            holder.tvStock.setTextColor(Color.parseColor("#D97706"));
        } else {
            holder.tvStock.setTextColor(Color.parseColor("#1E293B"));
        }

        holder.tvPrecioCompra.setText(String.format(Locale.US, "S/ %.2f", producto.getPrecioCompraDouble()));
        holder.tvPrecioVenta.setText(String.format(Locale.US, "S/ %.2f", producto.getPrecioVentaDouble()));

        if (holder.btnAgregarCarrito != null) {
            holder.btnAgregarCarrito.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAgregarCarrito(producto);
                }
            });
        }

        if (holder.btnEditar != null) {
            holder.btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditar(producto);
                }
            });
        }

        if (holder.btnEliminar != null) {
            holder.btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEliminar(producto);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productoList != null ? productoList.size() : 0;
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdProducto;
        TextView tvNombre;
        TextView tvEstado;
        TextView tvStock;
        TextView tvPrecioCompra;
        TextView tvPrecioVenta;
        MaterialButton btnAgregarCarrito;
        MaterialButton btnEditar;
        MaterialButton btnEliminar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdProducto = itemView.findViewById(R.id.tvIdProducto);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvEstado = itemView.findViewById(R.id.tvEstadoProducto);
            tvStock = itemView.findViewById(R.id.tvStockProducto);
            tvPrecioCompra = itemView.findViewById(R.id.tvPrecioCompra);
            tvPrecioVenta = itemView.findViewById(R.id.tvPrecioVenta);
            btnAgregarCarrito = itemView.findViewById(R.id.btnAgregarCarrito);
            btnEditar = itemView.findViewById(R.id.btnEditarProducto);
            btnEliminar = itemView.findViewById(R.id.btnEliminarProducto);
        }
    }
}
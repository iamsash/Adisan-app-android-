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

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    public interface OnPedidoActionListener {
        void onEditarEstado(Pedido pedido);
        void onCancelar(Pedido pedido);
    }

    private final Context context;
    private List<Pedido> pedidoList;
    private OnPedidoActionListener listener;

    public PedidoAdapter(Context context, List<Pedido> pedidoList) {
        this.context = context;
        this.pedidoList = pedidoList != null ? pedidoList : new ArrayList<>();
    }

    public void setOnPedidoActionListener(OnPedidoActionListener listener) {
        this.listener = listener;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidoList = pedidos != null ? pedidos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);

        holder.tvIdPedido.setText("Pedido #" + pedido.getId());
        holder.tvClientePedido.setText(pedido.getCliente());
        holder.tvFechaPedido.setText(!pedido.getFecha().isEmpty() ? pedido.getFecha() : "Fecha pendiente");
        holder.tvMetodoPagoPedido.setText(pedido.getMetodoPago());
        holder.tvTotalPedido.setText(String.format(Locale.US, "$%,.2f", pedido.getTotalDouble()));

        String estado = pedido.getEstado().toLowerCase();
        holder.tvEstadoPedido.setText(estado);

        switch (estado) {
            case "aceptado":
            case "aprobado":
                holder.tvEstadoPedido.setTextColor(Color.parseColor("#2563EB")); // Azul
                break;
            case "en_camino":
            case "en camino":
                holder.tvEstadoPedido.setTextColor(Color.parseColor("#0D9488")); // Teal
                break;
            case "entregado":
            case "completado":
                holder.tvEstadoPedido.setTextColor(Color.parseColor("#059669")); // Verde
                break;
            case "cancelado":
            case "rechazado":
                holder.tvEstadoPedido.setTextColor(Color.parseColor("#DC2626")); // Rojo
                break;
            default: // pendiente
                holder.tvEstadoPedido.setTextColor(Color.parseColor("#D97706")); // Naranja
                break;
        }

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditarEstado(pedido);
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelar(pedido);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedidoList != null ? pedidoList.size() : 0;
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdPedido;
        TextView tvClientePedido;
        TextView tvFechaPedido;
        TextView tvMetodoPagoPedido;
        TextView tvTotalPedido;
        TextView tvEstadoPedido;
        MaterialButton btnEditar;
        MaterialButton btnEliminar;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdPedido = itemView.findViewById(R.id.tvIdPedido);
            tvClientePedido = itemView.findViewById(R.id.tvClientePedido);
            tvFechaPedido = itemView.findViewById(R.id.tvFechaPedido);
            tvMetodoPagoPedido = itemView.findViewById(R.id.tvMetodoPagoPedido);
            tvTotalPedido = itemView.findViewById(R.id.tvTotalPedido);
            tvEstadoPedido = itemView.findViewById(R.id.tvEstadoPedido);
            btnEditar = itemView.findViewById(R.id.btnEditarPedido);
            btnEliminar = itemView.findViewById(R.id.btnEliminarPedido);
        }
    }
}
package com.example.adisan_app_android;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VentaAdapter extends RecyclerView.Adapter<VentaAdapter.VentaViewHolder> {

    private final Context context;
    private List<Pedido> pedidoList;

    public VentaAdapter(Context context, List<Pedido> pedidoList) {
        this.context = context;
        this.pedidoList = pedidoList != null ? pedidoList : new ArrayList<>();
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidoList = pedidos != null ? pedidos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_venta, parent, false);
        return new VentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VentaViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);

        holder.tvIdVenta.setText("Venta #" + pedido.getId());
        holder.tvClienteVenta.setText(pedido.getCliente());
        holder.tvFechaVenta.setText(!pedido.getFecha().isEmpty() ? pedido.getFecha() : "Fecha pendiente");
        holder.tvMetodoPagoVenta.setText(pedido.getMetodoPago());
        holder.tvTotalVenta.setText(String.format(Locale.US, "$%,.2f", pedido.getTotalDouble()));

        String estado = pedido.getEstado().toLowerCase();
        holder.tvEstadoVenta.setText(estado);

        switch (estado) {
            case "aceptado":
            case "entregado":
            case "completado":
                holder.tvEstadoVenta.setTextColor(Color.parseColor("#059669")); // Verde
                break;
            case "en_camino":
            case "en camino":
                holder.tvEstadoVenta.setTextColor(Color.parseColor("#0D9488")); // Teal
                break;
            case "cancelado":
                holder.tvEstadoVenta.setTextColor(Color.parseColor("#DC2626")); // Rojo
                break;
            default: // pendiente
                holder.tvEstadoVenta.setTextColor(Color.parseColor("#D97706")); // Naranja
                break;
        }
    }

    @Override
    public int getItemCount() {
        return pedidoList != null ? pedidoList.size() : 0;
    }

    public static class VentaViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdVenta;
        TextView tvClienteVenta;
        TextView tvFechaVenta;
        TextView tvMetodoPagoVenta;
        TextView tvTotalVenta;
        TextView tvEstadoVenta;

        public VentaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdVenta = itemView.findViewById(R.id.tvIdVenta);
            tvClienteVenta = itemView.findViewById(R.id.tvClienteVenta);
            tvFechaVenta = itemView.findViewById(R.id.tvFechaVenta);
            tvMetodoPagoVenta = itemView.findViewById(R.id.tvMetodoPagoVenta);
            tvTotalVenta = itemView.findViewById(R.id.tvTotalVenta);
            tvEstadoVenta = itemView.findViewById(R.id.tvEstadoVenta);
        }
    }
}
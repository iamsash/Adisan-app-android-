package com.example.adisan_app_android;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProveedorAdapter extends RecyclerView.Adapter<ProveedorAdapter.ProveedorViewHolder> {

    private final Context context;
    private List<Proveedor> proveedorList;

    public ProveedorAdapter(Context context, List<Proveedor> proveedorList) {
        this.context = context;
        this.proveedorList = proveedorList != null ? new ArrayList<>(proveedorList) : new ArrayList<>();
    }

    public void setProveedores(List<Proveedor> proveedores) {
        this.proveedorList = proveedores != null ? new ArrayList<>(proveedores) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProveedorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_proveedor, parent, false);
        return new ProveedorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProveedorViewHolder holder, int position) {
        Proveedor proveedor = proveedorList.get(position);

        holder.tvNombre.setText(proveedor.getNombreEmpresa());
        holder.tvRuc.setText("RUC: " + (!TextUtils.isEmpty(proveedor.getRuc()) ? proveedor.getRuc() : "-"));

        String tel = !TextUtils.isEmpty(proveedor.getTelefono()) ? proveedor.getTelefono() : "-";
        String correo = !TextUtils.isEmpty(proveedor.getCorreo()) ? proveedor.getCorreo() : "-";
        holder.tvContacto.setText("Tel: " + tel + " • " + correo);

        String dir = !TextUtils.isEmpty(proveedor.getDireccion()) ? proveedor.getDireccion() : "Sin dirección registrada";
        holder.tvDireccion.setText("Dirección: " + dir);
    }

    @Override
    public int getItemCount() {
        return proveedorList != null ? proveedorList.size() : 0;
    }

    static class ProveedorViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvRuc;
        TextView tvContacto;
        TextView tvDireccion;

        public ProveedorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEmpresaProveedor);
            tvRuc = itemView.findViewById(R.id.tvRucProveedor);
            tvContacto = itemView.findViewById(R.id.tvContactoProveedor);
            tvDireccion = itemView.findViewById(R.id.tvDireccionProveedor);
        }
    }
}
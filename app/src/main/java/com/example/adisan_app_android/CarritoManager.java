package com.example.adisan_app_android;

import java.util.ArrayList;
import java.util.List;

public class CarritoManager {

    private static CarritoManager instance;
    private final List<ItemCarrito> items = new ArrayList<>();

    public static synchronized CarritoManager getInstance() {
        if (instance == null) {
            instance = new CarritoManager();
        }
        return instance;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void agregarProducto(Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) return;

        for (ItemCarrito item : items) {
            if (item.getProductoId() == producto.getProductoId()) {
                int nuevaCantidad = item.getCantidad() + cantidad;
                if (nuevaCantidad > producto.getStock()) {
                    nuevaCantidad = producto.getStock();
                }
                item.setCantidad(nuevaCantidad);
                return;
            }
        }

        int cantidadInicial = Math.min(cantidad, producto.getStock());
        if (cantidadInicial > 0) {
            items.add(new ItemCarrito(producto, cantidadInicial));
        }
    }

    public void cambiarCantidad(int position, int nuevaCantidad) {
        if (position >= 0 && position < items.size()) {
            if (nuevaCantidad <= 0) {
                items.remove(position);
            } else {
                ItemCarrito item = items.get(position);
                if (nuevaCantidad > item.getStock()) {
                    nuevaCantidad = item.getStock();
                }
                item.setCantidad(nuevaCantidad);
            }
        }
    }

    public void eliminarItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
        }
    }

    public double obtenerTotal() {
        double total = 0;
        for (ItemCarrito item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int obtenerCantidadTotalItems() {
        int count = 0;
        for (ItemCarrito item : items) {
            count += item.getCantidad();
        }
        return count;
    }

    public void limpiar() {
        items.clear();
    }
}
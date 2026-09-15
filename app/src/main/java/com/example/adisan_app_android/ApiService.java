package com.example.adisan_app_android;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("api/dashboard")
    Call<DashboardResponse> getDashboardData(@Header("Authorization") String token);

    // ==========================================
    // CRUD PRODUCTOS
    // ==========================================

    @GET("api/productos")
    Call<ProductoResponse> getProductos();

    @GET("api/productos")
    Call<List<Producto>> getProductosDirectList();

    @GET("api/productos/{id}")
    Call<Producto> getProductoById(@Path("id") int id);

    @POST("api/productos")
    Call<ProductoResponse> crearProducto(@Body Producto producto);

    @PUT("api/productos/{id}")
    Call<ProductoResponse> editarProducto(@Path("id") int id, @Body Producto producto);

    @DELETE("api/productos/{id}")
    Call<ProductoResponse> eliminarProducto(@Path("id") int id);

    // ==========================================
    // CRUD PEDIDOS / OPERACIONES DE VENTA
    // ==========================================

    @GET("api/pedidos")
    Call<PedidoResponse> getPedidos();

    @GET("api/pedidos")
    Call<List<Pedido>> getPedidosDirectList();

    @GET("api/pedidos/{id}")
    Call<Pedido> getPedidoById(@Path("id") int id);

    @POST("api/pedidos")
    Call<PedidoResponse> crearPedido(@Body Pedido pedido);

    @PUT("api/pedidos/{id}")
    Call<PedidoResponse> editarPedido(@Path("id") int id, @Body Pedido pedido);

    @DELETE("api/pedidos/{id}")
    Call<PedidoResponse> eliminarPedido(@Path("id") int id);
}
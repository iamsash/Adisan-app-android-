# 🤖 AGENTS.md — Developer & AI Agent Guidelines for ADISAN Android

This document serves as the authoritative technical reference and operational guide for AI agents and human developers working on the **ADISAN** native Android codebase.

---

## 📌 1. Project Overview & Technical Stack

| Domain | Technology / Specification |
| :--- | :--- |
| **App Name** | ADISAN (Logistics & Commercial Distribution App) |
| **Language** | **Pure Java** (STRICT RULE: NO Kotlin allowed) |
| **UI Stack** | **Android XML Layouts + Material Design 3** (STRICT RULE: NO Jetpack Compose) |
| **Min SDK / Target SDK** | Min SDK 24 / Target SDK 34+ |
| **HTTP & Networking** | Retrofit 2 + Gson Converter + OkHttp 3 |
| **Authentication** | JWT (JSON Web Token) saved in `SharedPreferences("AdisanPrefs")` under key `"token"` |
| **Automatic Headers** | `ApiClient.java` uses an OkHttp Interceptor that automatically attaches `Authorization: Bearer <token>` |
| **Backend Stack** | Node.js + Express + MySQL 8.0 |
| **Backend Base URL** | `http://10.0.2.2:3000/` (Android Emulator loopback to local Node.js server) |
| **Build System** | Gradle (`./gradlew app:assembleDebug`) |

---

## 🏛️ 2. Core Architectural Principles & Business Rules

### 1. No Independent `ventas` Table
- **Crucial Rule**: There is NO separate `ventas` or `detalle_venta` table in MySQL or backend route `/api/ventas`.
- **Unified Order Model**: All commercial operations (orders and direct sales) are represented via `pedidos` and `detalle_pedido`.
- **Completed Sale Definition**: An order with `estado = 'entregado'` (or `'completado'`) constitutes a completed sale for financial metrics and Dashboard calculations.

### 2. Direct Sales (Venta Directa de Mostrador)
- Direct over-the-counter sales do **not** require a client.
- When creating a direct sale:
  - `pedidos.cliente_id` = `NULL`
  - `pedidos.estado` = `'entregado'`
- In queries (`GET /api/pedidos`), `IFNULL(u.nombres, 'Venta Mostrador')` displays the seller/client as `"Venta Mostrador"`.

### 3. Role-Based Dynamic UI Text
Read `rol` from `SharedPreferences` (`"AdisanPrefs"` -> `"rol"`):
- **`CLIENTE`**:
  - Catalog Header Title: `"Comprar productos"`
  - Orders Header Title: `"Mis pedidos"`
- **`VENDEDOR` / `ADMIN`**:
  - Catalog Header Title: `"Vender productos"`
  - Orders Header Title: `"Pedidos"`

### 4. In-Memory Shopping Cart
- `CarritoManager.java` is a thread-safe Singleton managing `ItemCarrito` objects in memory.
- The cart persists across activity transitions (`ProductosActivity` ➔ `CarritoActivity` ➔ `PedidosActivity`).

---

## 🗄️ 3. MySQL Database Schema Reference (`adisan`)

```sql
-- Table: usuarios (User Accounts & Roles)
CREATE TABLE usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombres VARCHAR(100) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  dni VARCHAR(8) NOT NULL UNIQUE,
  ruc VARCHAR(11) NULL,
  telefono VARCHAR(20) NOT NULL,
  correo VARCHAR(150) NOT NULL,
  usuario VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  direccion TEXT NULL,
  rol ENUM('admin', 'vendedor', 'chofer', 'cliente') DEFAULT 'cliente',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: productos (Catalog Items)
CREATE TABLE productos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(200) NOT NULL,
  proveedor_id INT NOT NULL,
  activo TINYINT(1) DEFAULT 1
);

-- Table: presentaciones (Pricing & Stock Levels)
CREATE TABLE presentaciones (
  id INT AUTO_INCREMENT PRIMARY KEY,
  producto_id INT NOT NULL,
  categoria_id INT NOT NULL,
  padre_id INT NULL,
  nivel INT DEFAULT 1,
  factor INT DEFAULT 1,
  stock INT DEFAULT 0,
  precio_compra DECIMAL(10,2) NOT NULL,
  precio_venta DECIMAL(10,2) NOT NULL
);

-- Table: pedidos (Order & Sale Headers)
CREATE TABLE pedidos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  cliente_id INT NULL, -- NULL for Direct Sales
  total DECIMAL(10,2) NOT NULL,
  metodo_pago VARCHAR(50) NOT NULL, -- 'Yape', 'Tarjeta', 'Efectivo'
  estado ENUM('pendiente', 'aceptado', 'en_camino', 'entregado', 'cancelado') DEFAULT 'pendiente',
  fecha DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table: detalle_pedido (Order Line Items)
CREATE TABLE detalle_pedido (
  id INT AUTO_INCREMENT PRIMARY KEY,
  pedido_id INT NOT NULL,
  producto_id INT NOT NULL,
  presentacion_id INT NOT NULL,
  cantidad INT NOT NULL,
  precio DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL
);
```

---

## 🔌 4. API Endpoints & Contracts

| HTTP Method | Endpoint | Auth Required | Description |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/login` | No | Authenticates user credentials. Returns JWT `token`, `usuario`, `rol`, `id`. |
| **GET** | `/api/dashboard` | Admin / Vendedor | Returns real-time metrics (`totalProductos`, `totalUsuarios`, `totalPedidos`, `totalVentas`, `pedidosPorEstado`, `stockBajo`). |
| **GET** | `/api/productos` | Yes | Retrieves product catalog and stock levels. |
| **POST** | `/api/productos` | Admin | Registers a new product with presentation details (`nombre`, `proveedor_id`, `presentaciones`). |
| **PUT** | `/api/productos/:id` | Admin | Updates an existing product. |
| **DELETE** | `/api/productos/:id` | Admin | Deletes/Deactivates a product. |
| **GET** | `/api/pedidos` | Yes | Retrieves order and sales transaction history. |
| **POST** | `/api/pedidos` | Yes | Registers an order or direct sale, inserts `pedidos` and `detalle_pedido`, and deducts stock in `presentaciones`. |
| **PUT** | `/api/pedidos/:id` | Yes | Updates order status (`'pendiente'` ➔ `'aceptado'` ➔ `'en_camino'` ➔ `'entregado'` / `'cancelado'`). |
| **GET** | `/api/usuarios` | Admin / Vendedor | Queries users/clients with search filter (`?search=query`). |
| **POST** | `/api/usuarios` | Admin / Vendedor | Registers a new client in `usuarios`. |

---

## 📁 5. Directory Structure & Key Classes

```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/adisan_app_android/
│   ├── ApiClient.java               # Retrofit instance + OkHttp JWT Interceptor
│   ├── ApiService.java              # Retrofit API interface definitions
│   ├── LoginActivity.java           # Authentication screen & JWT storage
│   ├── LoginRequest.java / Response # Auth models
│   ├── DashboardActivity.java       # Real-time metrics dashboard
│   ├── DashboardResponse.java       # Dashboard JSON deserialization model
│   ├── ProductosActivity.java       # Product catalog & CRUD management
│   ├── Producto.java / Response     # Product domain models
│   ├── ProductoAdapter.java         # RecyclerView adapter for catalog items
│   ├── ItemCarrito.java             # Shopping cart item model
│   ├── CarritoManager.java          # In-memory Singleton cart manager
│   ├── CarritoActivity.java         # Checkout & Cart management screen
│   ├── PedidosActivity.java         # Order tracking & client order creation
│   ├── Pedido.java / Response       # Order domain models
│   ├── DetallePedido.java           # Order detail line item model
│   ├── PedidoAdapter.java           # RecyclerView adapter for order history
│   ├── Usuario.java / Response      # User / Client domain models
│   └── MainActivity.java            # App entry point
└── res/
    ├── drawable/                    # Icons & Spinner outlined backgrounds
    ├── layout/                      # Activity and Dialog XML layouts
    └── menu/bottom_nav_menu.xml     # Clean 4-item bottom navigation bar
```

---

## 🧭 6. Bottom Navigation Hierarchy

The main bottom navigation bar (`bottom_nav_menu.xml`) contains 4 items:

1. **Dashboard** (`DashboardActivity`)
2. **Productos** (`ProductosActivity`)
3. **Pedidos** (`PedidosActivity`)
4. **Más** (Settings & future features)

---

## 🛠️ 7. Development Guidelines for AI Agents

When implementing new features or fixing bugs in this repository:

1. **Maintain Pure Java + XML Stack**:
   - Do NOT introduce Kotlin files (`.kt`) or Jetpack Compose UI code.
   - Keep all layout definitions in standard Android XML under `res/layout/`.

2. **Strict Concurrency Control**:
   - Always read files using `read_file` before editing.
   - Use surgical `replace_file_content` or `multi_replace_file_content` for file edits.
   - Never use shell commands (`sed`, `echo >`, `cat << EOF`) to modify source code files.

3. **GSON Deserialization Safety**:
   - When mapping fields with `@SerializedName`, ensure that multiple Java fields DO NOT claim the same primary JSON property name to prevent `IllegalArgumentException` conflicts in Retrofit.
   - When handling fields that can be JSON Arrays or Numbers/Strings, use `List<Type>` or flexible getters (`Double.parseDouble(field.toString())`).

4. **Build Verification**:
   - Always run `./gradlew app:assembleDebug` using `gradle_build` after completing code modifications.
   - Verify that the build finishes with **`BUILD SUCCESSFUL`** and **0 compilation errors**.

---

*Last Updated: September 2026*

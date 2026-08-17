# Supermarket ERP → Warehouse Management System

Spring Boot MVC application covering **Product**, **Supplier**, **Purchase Orders (PO)**,
**Goods Received Notes (GRN)**, **Inventory** (per-location stock), **Sales**, **Payments**,
and **User/Role login** — restructured to match the Warehouse Management System ER diagram.

## Tech Stack

- Java 17
- Spring Boot 3.2 (Spring MVC, Spring Data JPA, Validation, Security, Thymeleaf)
- MySQL 8
- Maven
- Bootstrap 5 (CDN, for styling only)

## Architecture

```
Controller  ->  Service (interface + impl)  ->  Repository (Spring Data JPA)  ->  MySQL
     |                                                     |
     +----------------------- Entity (JPA) ----------------+
     |
Thymeleaf Views (templates/)
```

- **entity** — `Product`, `Supplier`, `PurchaseOrder`, `PurchaseOrderItem`,
  `GoodsReceivedNote`, `GrnItem`, `Role`, `User`, `Location`, `Inventory`,
  `SalesReceipt`, `SalesReceiptItem`, `Payment` (JPA entities with
  `@ManyToOne`/`@OneToMany` relationships)
- **repository** — one Spring Data JPA repository per entity above
- **service** — one interface + `impl` per module (`ProductService`, `SupplierService`,
  `PurchaseOrderService`, `GoodsReceivedNoteService`, `InventoryService`, `UserService`,
  `RoleService`, `LocationService`, `SalesReceiptService`, `PaymentService`)
- **controller** — `ProductController`, `SupplierController`, `PurchaseOrderController`,
  `GoodsReceivedNoteController`, `InventoryController`, `UserController`,
  `LocationController`, `SalesController`, `PaymentController`, `LoginController`,
  `HomeController`
- **config** — `SecurityConfig` + `CustomUserDetailsService` (login), `DataInitializer`
  (seeds default roles/admin/location), `WebConfig` (Supplier ↔ id `Formatter` used by
  the product form's supplier dropdown)
- **templates** — Thymeleaf views: `products/`, `suppliers/`, `purchase-orders/`, `grn/`,
  `inventory/`, `users/`, `locations/`, `sales/`, `payments/`, `login.html`,
  shared `fragments/navbar`

## Default login

The app seeds this on first run — **change the password after logging in for real use**:

```
username: admin
password: admin123
```

Roles `ADMIN`, `MANAGER`, `CASHIER` and a default "Main Warehouse" location are also seeded.

## What changed from the original Supplier/Product assignment

| Diagram entity | Implementation |
|---|---|
| Product | Restructured: no more direct Supplier link; `quantity` removed (stock now lives in Inventory); added `brand`, `size`, `color`, `purchasePrice`, `sellingPrice`, `status` |
| Supplier | No longer linked to Product directly; now linked to Purchase Orders; added `bankDetails`, `status` |
| User / Role | New — Spring Security login, BCrypt-hashed passwords, role-based user accounts |
| Location (Branch/Store) | New — every stock and transaction record ties back to a location |
| Inventory/Stock | New — per-product, per-location quantity, replaces `Product.quantity` |
| Purchase_Order | Added `createdBy` (auto-set to the logged-in user) and `approvedBy` (set via an Approve action) |
| Purchase_Order_Item | Unchanged structurally (product, quantity, unit price, computed subtotal) |
| Goods_Receipt / Item | `receivedBy` is now a `User` (not free text); added `location`; GRN items now link directly to `Product` + `barcode` |
| Sales_Payment_Receipt | New — a POS-style sale: cashier, location, items, discount, payment method, total |
| Sales_Receipt_Item | New — line items on a sale |
| Payment | New — auto-created alongside each completed sale |

## Purchase Order (PO) and GRN modules

**Purchase Order** — a header (`PurchaseOrder`: PO number, supplier, order date, status,
created by, approved by) with one or more line items (`PurchaseOrderItem`: product,
quantity ordered, unit price). PO numbers auto-generate as `PO-<year>-<sequence>`. A PO
can only be edited or deleted while it is still `PENDING` and nothing has been received
against it yet.

**Goods Received Note** — records what was actually delivered against a PO, and where it
went. Creating a GRN (`GoodsReceivedNoteService.receiveGoods`) does this in one transaction:
1. Creates the `GoodsReceivedNote` and its `GrnItem` lines
2. Increases `Inventory` stock for each item **at the chosen location**
3. Updates the PO's `receivedQuantity` per line and recalculates its overall status
   (`PENDING` → `PARTIALLY_RECEIVED` → `RECEIVED`)

## Integration between modules

- Every `Product` now has purchase/selling prices but **no stock field** — stock lives in
  `Inventory`, tracked per product **and** per location.
- `GoodsReceivedNoteServiceImpl.receiveGoods()` — receiving a GRN increases `Inventory`
  stock for the chosen location and updates the PO's status.
- `SalesReceiptServiceImpl.completeSale()` — completing a sale decreases `Inventory` stock
  at the chosen location (throws if there isn't enough on hand) and creates a matching
  `Payment` automatically.
- `InventoryService` is the single source of truth for stock levels — both GRN receiving
  and Sales checkout route through its `increaseStock()` / `decreaseStock()` methods
  rather than touching `Product` directly, so stock numbers can't drift out of sync.
- A `Supplier` can't be deleted while it still has purchase orders attached — the
  controller checks `hasLinkedPurchaseOrders()` and shows a friendly error instead.

## Setup

1. **Install prerequisites:** JDK 17+, Maven 3.9+, MySQL 8 running locally (WAMP works fine).

2. **Create the database (optional — the app can also auto-create it):**
   ```sql
   CREATE DATABASE supermarket_erp;
   ```

3. **Configure DB credentials** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/supermarket_erp?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```
   **Using WAMP:** WAMP's default MySQL root user has no password — leave
   `spring.datasource.password=` blank, and make sure WAMP's tray icon is fully green
   before starting the app.

4. **Run the app:**
   ```bash
   mvn spring-boot:run
   ```
   Tables are created/updated automatically on startup (`spring.jpa.hibernate.ddl-auto=update`).

5. **Open the app:** http://localhost:8080 — you'll be redirected to `/login`.
   Log in with `admin` / `admin123`.

## Suggested first steps after logging in

1. Add a **Location** (or use the seeded "Main Warehouse").
2. Add a **Supplier**.
3. Add a **Product** (purchase price, selling price, category, etc — no stock yet).
4. Create a **Purchase Order** for that product from the supplier.
5. Go to **GRN → Receive Goods**, pick the PO, choose a location, receive the items —
   check **Inventory** to see stock appear.
6. Go to **Sales → New Sale**, pick the location and product, complete the sale — check
   **Inventory** again to see stock decrease, and **Payments** to see the payment logged.

## Feature checklist

- [x] Supplier entity, full CRUD, input validation
- [x] Product entity with barcode support, full CRUD
- [x] Purchase Order module — Add/View/Edit/Delete, multi-line items, supplier link,
      created-by/approved-by tracking
- [x] GRN module — receive goods against a PO, updates Inventory stock and PO status
- [x] Inventory module — per-product, per-location stock levels
- [x] User/Role module — Spring Security login, BCrypt password hashing
- [x] Location (Branch/Store) module — full CRUD
- [x] Sales module — POS-style checkout, decrements Inventory, auto-creates Payment
- [x] Payment module — records how each sale was settled
- [x] Spring Boot MVC architecture — Controller / Service / Repository / Entity layers
- [x] MySQL storage — Spring Data JPA + `mysql-connector-j`


## Support

dinithmadushan38@gmail.com
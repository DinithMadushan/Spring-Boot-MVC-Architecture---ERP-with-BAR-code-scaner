# Supermarket Chain ERP System

Spring Boot MVC application for a supermarket chain, covering **Product Management**
(with barcode support), **Supplier Management**, **Purchase Orders (PO)**, and
**Goods Received Notes (GRN)** - all integrated with each other.

## Tech Stack

- Java 17
- Spring Boot 3.2 (Spring MVC, Spring Data JPA, Validation, Thymeleaf)
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
  `GoodsReceivedNote`, `GrnItem` (JPA entities with `@ManyToOne`/`@OneToMany` relationships)
- **repository** — `ProductRepository`, `SupplierRepository`, `PurchaseOrderRepository`,
  `GoodsReceivedNoteRepository` (Spring Data JPA)
- **service** — `ProductService`, `SupplierService`, `PurchaseOrderService`,
  `GoodsReceivedNoteService` — each an interface + `impl` implementation
- **controller** — `ProductController`, `SupplierController`, `PurchaseOrderController`,
  `GoodsReceivedNoteController`, `HomeController`
- **config** — `WebConfig` registers the `Supplier` <-> id `Formatter` used by the product
  form's supplier dropdown
- **templates** — Thymeleaf views: `products/`, `suppliers/`, `purchase-orders/`, `grn/`,
  shared `fragments/navbar`

## Purchase Order (PO) and GRN modules

**Purchase Order** — a header (`PurchaseOrder`: PO number, supplier, order date, status)
with one or more line items (`PurchaseOrderItem`: product, quantity ordered, unit price).
PO numbers auto-generate as `PO-<year>-<sequence>`. A PO can only be edited or deleted
while it is still `PENDING` and nothing has been received against it yet.

**Goods Received Note** — records what was actually delivered against a PO.
Creating a GRN (`GoodsReceivedNoteService.receiveGoods`) does three things in one
transaction:
1. Creates the `GoodsReceivedNote` and its `GrnItem` lines
2. Increases each received product's stock via `ProductService.increaseStock(...)`
3. Updates the PO's `receivedQuantity` per line and recalculates its overall status
   (`PENDING` → `PARTIALLY_RECEIVED` → `RECEIVED`)

This chains all four modules together: **Supplier → Purchase Order → GRN → Product stock**.

## Integration between modules

Every `Product` has a required `supplier` (`@ManyToOne`). The product Add/Edit form has a
supplier dropdown populated from `SupplierService`, so each product is always linked to a
real supplier row in MySQL. A supplier can't be deleted while it still has products
attached — the controller checks `hasLinkedProducts()` and shows a friendly error instead.

## Setup

1. **Install prerequisites:** JDK 17+, Maven 3.9+, MySQL 8 running locally.

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

4. **Run the app:**
   ```bash
   mvn spring-boot:run
   ```
   Tables are created/updated automatically on startup (`spring.jpa.hibernate.ddl-auto=update`).

5. **Open the app:** http://localhost:8080

## Feature checklist

- [x] Supplier entity (`entity/Supplier.java`)
- [x] Add Supplier — `GET /suppliers/new`, `POST /suppliers/save`
- [x] View Supplier List — `GET /suppliers`
- [x] Edit Supplier — `GET /suppliers/edit/{id}`, `POST /suppliers/save`
- [x] Delete Supplier — `GET /suppliers/delete/{id}` (blocked if products are linked)
- [x] Input validation — Jakarta Bean Validation annotations + Thymeleaf error display
- [x] Integration with Product module — `Product.supplier` (`@ManyToOne`), dropdown on
      the product form, products shown per supplier count in the supplier list
- [x] Purchase Order module — Add/View/Edit/Delete, multi-line items, supplier link
- [x] GRN module — receive goods against a PO, updates Product stock and PO status
- [x] Spring Boot MVC architecture — Controller / Service / Repository / Entity layers
- [x] MySQL storage — Spring Data JPA + `mysql-connector-j`

## Support
      dinithmadushan38@gmail.com

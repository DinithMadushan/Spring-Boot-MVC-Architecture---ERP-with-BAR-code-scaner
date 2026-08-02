# Supermarket Chain ERP System

Spring Boot MVC application for a supermarket chain, covering **Product Management**
(with barcode support) and **Supplier Management**, fully integrated with each other.

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

- **entity** — `Product`, `Supplier` (JPA entities, `@ManyToOne`/`@OneToMany` relationship)
- **repository** — `ProductRepository`, `SupplierRepository` (Spring Data JPA)
- **service** — `ProductService`, `SupplierService` interfaces + `impl` implementations
- **controller** — `ProductController`, `SupplierController`, `HomeController`
- **config** — `WebConfig` registers the `Supplier` <-> id `Formatter` used by the product
  form's supplier dropdown
- **templates** — Thymeleaf views: `products/`, `suppliers/`, shared `fragments/navbar`

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

## Feature checklist (maps to assignment requirements)

- [x] Supplier entity (`entity/Supplier.java`)
- [x] Add Supplier — `GET /suppliers/new`, `POST /suppliers/save`
- [x] View Supplier List — `GET /suppliers`
- [x] Edit Supplier — `GET /suppliers/edit/{id}`, `POST /suppliers/save`
- [x] Delete Supplier — `GET /suppliers/delete/{id}` (blocked if products are linked)
- [x] Input validation — Jakarta Bean Validation annotations + Thymeleaf error display
- [x] Integration with Product module — `Product.supplier` (`@ManyToOne`), dropdown on
      the product form, products shown per supplier count in the supplier list
- [x] Spring Boot MVC architecture — Controller / Service / Repository / Entity layers
- [x] MySQL storage — Spring Data JPA + `mysql-connector-j`

## Suggested next steps before submission

1. Test all CRUD flows locally (add/edit/delete for both Products and Suppliers).
2. `git init`, commit, push to your own GitHub repository.
3. Submit the repository link via the LMS.

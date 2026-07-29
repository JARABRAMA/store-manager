# Store Manager

Inventory management REST API built with Spring Boot 4.0.6 and Java 21, following Hexagonal Architecture (Ports & Adapters).

## Tech Stack

- **Java 21** with **Spring Boot 4.0.6**
- **PostgreSQL** with Spring Data JPA / Hibernate
- **SpringDoc OpenAPI 3.0.2** (Swagger UI)
- **Lombok**, **JaCoCo**, **java-dotenv**
- **JUnit 5** + **Mockito** for testing

## Architecture

The project follows a **Hexagonal Architecture** with three layers:

```
domain/         — Pure business logic and domain model (no framework annotations)
application/    — Use cases (ProductService) and inbound ports (ProductUseCase)
infraestructure/— Adapters: JPA persistence, REST controllers, exception handlers
```

## API Endpoints

All endpoints are under `/api/products`:

| Method | Path                    | Description            |
|--------|-------------------------|------------------------|
| POST   | `/api/products`         | Create a product       |
| GET    | `/api/products`         | List products (paginated, filterable by `text`, `category`, `page`) |
| GET    | `/api/products/{id}`    | Get product by ID      |
| PUT    | `/api/products/{id}`    | Update a product       |
| GET    | `/api/products/categories` | List all categories |

## Domain Model

A **Product** has: `id` (UUID), `name`, `description`, `price`, `stock`, `expiresAt`, `imageUrl`, and `categories` (ManyToMany).

Business rules enforced on creation/update:
- Name is required (max 50 characters)
- Price >= 0
- Stock >= 0
- Description max 100 characters
- Product names must be unique

## Getting Started

### Prerequisites

- Java 21+
- PostgreSQL

### Setup

1. Clone the repository.
2. Create a PostgreSQL database named `store_manager`.
3. Copy `.env` (or create one) with:

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/store_manager
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password
FRONT_SOURCE=http://localhost:5173
```

4. Run the application:

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### Swagger UI

Once running, visit: `http://localhost:8080/swagger-ui.html`

## Running Tests

```bash
./mvnw test
```

## Build

```bash
./mvnw clean package
```

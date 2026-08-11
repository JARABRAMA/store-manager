# Store Manager

Inventory management and authentication REST API built with Spring Boot 4.0.6 and Java 21, following Hexagonal Architecture (Ports & Adapters).

## Tech Stack

- **Java 21** with **Spring Boot 4.0.6**
- **PostgreSQL** with Spring Data JPA / Hibernate
- **Spring Security Crypto** (BCrypt password hashing)
- **JJWT** for JWT token generation/validation
- **SpringDoc OpenAPI 3.0.2** (Swagger UI)
- **Lombok**, **JaCoCo**, **java-dotenv**
- **JUnit 5** + **Mockito** for testing

## Architecture

The project follows a **Hexagonal Architecture**. Each module (`inventory`, `authentication`) is organized in layers:

```
domain/          — Pure business logic, domain models and outbound ports (repositories)
usecases/        — Use cases implementing the business workflows
persistence/     — JPA adapters implementing the outbound ports
presentation/    — REST controllers, DTOs and exception handlers
service/         — Cross-cutting services (e.g. JWT generation)
```

## API Endpoints

### Products

| Method | Path                    | Description            |
|--------|-------------------------|------------------------|
| POST   | `/api/products`         | Create a product       |
| GET    | `/api/products`         | List products (paginated, filterable by `text`, `category`, `page`) |
| GET    | `/api/products/{id}`    | Get product by ID      |
| PUT    | `/api/products/{id}`    | Update a product       |
| GET    | `/api/products/categories` | List all categories |

### Users & Authentication

| Method | Path              | Description                                    |
|--------|-------------------|------------------------------------------------|
| POST   | `/api/users`       | Register a new user (role `EMPLOYEE` by default) |
| POST   | `/api/auth/login`  | Login and obtain access + refresh tokens        |

#### Login flow

`POST /api/auth/login` validates credentials, revokes previous sessions and, when the client sends a device fingerprint with `trustedDevice: true`, registers/restores a trusted device. It then returns:

- an **access token** (valid 5 minutes)
- a **refresh token** (valid 15 minutes on untrusted devices, 5 days on trusted devices)

A session linked to the refresh token is persisted so it can later be validated when refreshing a session.

## Domain Model

- **Product**: `id` (UUID), `name`, `description`, `price`, `stock`, `expiresAt`, `imageUrl`, `categories` (ManyToMany).
- **SystemUser**: `id` (UUID), `username`, `passwordHash`, `role`.
- **AuthToken**: `id`, `tokenHash`, `userId`, `tokenType` (`ACCESS`/`REFRESH`), `createdAt`, `expiresAt`, `revoked`.
- **Session**: `id`, `userId`, `trustedDeviceId`, `lastActivityAt`, `expiresAt`, `revoked`, `refreshToken`.
- **TrustedDevice**: `id`, `userId`, `deviceTokenHash`, `lastUsedAt`, `revoked`.

### Business rules

Products, enforced on creation/update:
- Name is required (max 50 characters)
- Price >= 0
- Stock >= 0
- Description max 100 characters
- Product names must be unique

Users, enforced on registration:
- Username between 3 and 20 characters and unique
- Password between 6 and 20 characters, stored as a BCrypt hash

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
JWT_SECRET_KEY=your_base64_secret_key
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

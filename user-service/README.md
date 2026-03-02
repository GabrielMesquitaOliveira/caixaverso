# User Service

A Quarkus-based microservice for managing users in the Caixaverso project, built with Clean Architecture principles and Java 25.

## Quick Start

### Prerequisites
- Java 25
- Maven
- Docker & Docker Compose (for local database)

### Running Locally
1. Start the PostgreSQL database:
   ```bash
   cd ../infra
   docker-compose -f docker-compose.dev.yml up -d
   ```
2. Run the application in dev mode:
   ```bash
   ./mvnw compile quarkus:dev
   ```

The application will be available at `http://localhost:8080`.
The OpenAPI/Swagger UI is accessible at `http://localhost:8080/q/swagger-ui/`.

## Features

- **Clean Architecture:** Strict separation of concerns (Domain, Application, Presentation, Infrastructure).
- **User Management:** Create and retrieve users.
- **Data Persistence:** Hibernate ORM with Panache and PostgreSQL.
- **DTO Mapping:** MapStruct integration for efficient data transformation.
- **Health Checks:** SmallRye Health endpoints included out of the box.

## API Reference

Base URL: `http://localhost:8080/api/users`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `POST` | `/` | Create a new user | `{ "username": "...", "email": "...", "fullName": "..."}` | `201 Created` / User object |
| `GET` | `/{id}` | Get user by ID | - | `200 OK` / User object |
| `GET` | `/` | Get all users | - | `200 OK` / List of User objects |

## Configuration

Key environment properties configured in `src/main/resources/application.properties`:

| Key | Description | Default / Example |
|-----|-------------|-------------------|
| `quarkus.http.port` | The port the HTTP server listens on | `8080` |
| `quarkus.datasource.jdbc.url` | Database connection URL | `jdbc:postgresql://localhost:5432/user_db` |
| `quarkus.datasource.username` | Database username | `sa` |
| `quarkus.datasource.password` | Database password | `sa` |
| `quarkus.swagger-ui.always-include` | Expose Swagger UI at `/q/swagger-ui` | `true` |
| `quarkus.log.level` | Application log level | `INFO` |

## Architecture

This service follows **Clean Architecture**:
- **Domain:** Contains core business rules and entities (`User`).
- **Application:** Contains use cases (`CreateUserUseCase`, `GetUserUseCase`) and DTOs mapping.
- **Presentation:** Contains REST resources (`UserResource`) and exception handlers.
- **Infrastructure:** Contains database repositories (`UserRepository`, `UserJpaEntity`) and adapters.

## Developing & Testing
Tests can be run using Maven, utilizing the dedicated test profile defined in the configuration, which also targets PostgreSQL:
```bash
./mvnw test
```

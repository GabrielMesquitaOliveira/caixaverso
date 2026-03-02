# Infrastructure - Caixaverso

This directory contains the core infrastructure scripts and configurations necessary to run the Caixaverso microservices (`user-service` and `reservation-service`) locally and in production-like environments.

## 📂 Directory Structure

- `docker-compose.yml`: Main compose file for a production-like environment (uses pre-built JVM Docker images).
- `docker-compose.dev.yml`: Developer compose file running Quarkus in Dev mode (live coding) mapped to local source directories.
- `Dockerfile.dev`: Dockerfile specifically for running services in development mode.
- `nginx.conf`: Nginx reverse proxy block with specific rate limiting, logging, and security headers.
- `postgres-init.sh`: Custom PostgreSQL initialization script responsible for creating multiple databases at container startup.
- `html/`: Nginx static files (e.g., custom error pages `404.html`, `500.html`).

---

## 🏗️ Architecture Components

### 1. Nginx (API Gateway / Reverse Proxy)
Acts as the main entry point to the system, listening on port `80`.
- **Routing**: Routes `/api/users` traffic to `user-service` and `/api/reservations` traffic to `reservation-service`.
- **Security**: Denies `X-Frame-Options`, adds `nosniff`, and establishes basic XSS Protection.
- **Traffic Control**: Rate limiting set to `5 req/sec` with a burst buffer of 10. Maximum request payload is capped at `1M`.
- **Custom Error Handling**: Returns structured fallback pages for `404` and `5xx` errors.

### 2. PostgreSQL (Database Layer)
A single `postgres:15-alpine` container acting as the datastore.
- Exposes port `5432` locally (in dev mode).
- Uses `postgres-init.sh` to automatically provision two separate logical databases: `user_db` and `reservation_db`.

### 3. Microservices
- `user-service`: Exposed dynamically to the network via Nginx. Connects to `user_db`.
- `reservation-service`: Exposed dynamically to the network via Nginx. Connects to `reservation_db` and communicates internally with `user-service`.

---

## 🚀 How to Run

### Development Mode (Live Coding)

To spin up the network with Quarkus live-coding enabled (auto-reloads changes when you save files):

```bash
docker-compose -f docker-compose.dev.yml up --build
```

**Exposed Ports:**
- **Nginx API Gateway:** `http://localhost:80`
- **User Service:** `8080` (App) / `5005` (Debug)
- **Reservation Service:** `8081` (App) / `5006` (Debug)
- **Postgres:** `5432`

*Note: In Dev mode, local folders (`../user-service` and `../reservation-service`) are mounted inside the containers to allow hot-reloading.*

### Production-like Mode

To run the complete network using the JVM-optimized versions of your services (Make sure you have built your Maven projects first!):

```bash
# In your service folders, run: ./mvnw package
docker-compose up --build -d
```

**Exposed Ports:**
- **Nginx API Gateway:** `http://localhost:80`

*Note: Services themselves do not bind to host ports in this mode to ensure all traffic routes exclusively through Nginx.*

---

## 🛠️ Maintenance & Reset

If you need to completely recreate the database or clear the states:

```bash
docker-compose -f docker-compose.dev.yml down -v
```
*The `-v` flag removes the named volumes, completely wiping out the PostgreSQL data directory.*

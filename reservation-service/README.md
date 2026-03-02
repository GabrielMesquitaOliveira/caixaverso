# Reservation Service

The **Reservation Service** is a robust, Quarkus-based microservice responsible for managing resource reservations. It forms the core of the scheduling system, handling complex business rules and validations while integrating seamlessly with the `user-service` to verify user identities. 

Built using modern **Java 25**, this project leverages Records, Record Builders, and advanced pattern matching. It strictly adheres to **Clean Architecture** principles, guaranteeing a high degree of maintainability, testability, and framework independence.

---

## 🏗️ Architecture (Clean Architecture)

The service is strictly divided into layers following **Clean Architecture** and **Ports and Adapters (Hexagonal Architecture)** patterns. This ensures that the core business logic remains framework-agnostic and decoupled from external systems like databases, message brokers, or REST APIs.

### Layers Breakdown

- **`domain`**: The heart of the application. It contains the central `Reservation` entity and all complex domain exceptions (e.g., `InvalidDurationException`, `InvalidTimeSlotException`, `BusinessHoursViolationException`, `SlotAlreadyBookedException`). **Crucially, this layer has zero dependencies on any frameworks or other layers.**
- **`application`**: Contains the Use Cases (such as `CreateReservationUseCase` and `GetAvailableSlotsUseCase`). It orchestrates the flow of data, delegates business rules to the domain entities, and defines interfaces (Ports) for external communication (both input and output).
- **`infrastructure`**: Implements the output ports defined in the application layer. It contains:
  - **Repositories**: Database access implemented cleanly using Hibernate ORM with Panache.
  - **Adapters**: REST Clients for outbound communication with the `user-service` via the Quarkus REST Client.
- **`presentation`**: Implements the input ports. It contains the REST API controllers (Resources, e.g., `ReservationResource`) mapped to Quarkus REST, as well as DTOs (Data Transfer Objects) to encapsulate HTTP requests/responses. MapStruct is used here to map seamlessly between DTOs and Domain Entities.

---

## 💼 Business Rules & Domain Logic

The core business logic of the Reservation Service is rigorous and defined entirely within the Domain layer. These rules must all pass before a reservation is successfully created:

1. **Duration Rule (`SLOT_DURATION_MINUTES = 30`)**
   - Every reservation must be exactly 30 minutes long.
2. **Time Slot Rule**
   - The start minute must end cleanly in `:00` or `:30` (e.g., 10:00 or 10:30).
3. **Business Hours Rule**
   - Reservations are permitted strictly within business hours: **08:00 to 18:00**.
   - A reservation cannot begin before 08:00 or end past 18:00.
4. **Overlap / Double Booking Rule**
   - For any single resource name, two reservations cannot occupy the same time slot simultaneously. The system calculates intersections mathematically (`startDate1 < endDate2` and `endDate1 > startDate2`).
5. **Cross-Service User Validation**
   - Prior to finalizing a reservation, the system validates the `userId` synchronously via the `user-service` to ensure the requester exists.

---

## 🛠️ Technology Stack

- **Framework:** Quarkus (Quarkus REST, Hibernate ORM with Panache, REST Client, DevServices)
- **Language:** Java 25 (Native Records, Patterns)
- **Database:** PostgreSQL
- **Object Mapping:** MapStruct
- **Testing:** JUnit 5, REST Assured (E2E API Tests), Mockito, AssertJ, Instancio (Data Generation)
- **Containerization:** Docker & Docker Compose

---

## 🚀 REST Endpoints

The API base path is `/api/reservations`. OpenAPI documentation and Swagger UI are provided out-of-the-box in development mode.

### 1. Create a Reservation
- **POST** `/api/reservations`
- **Request Body Example:**
  ```json
  {
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "resourceName": "Conference Room A",
    "startDate": "2026-10-15T10:00:00",
    "endDate": "2026-10-15T10:30:00"
  }
  ```
- **Responses:**
  - `201 Created` - Successfully created, returns the reservation details and assigned ID.
  - `400 Bad Request` - Form validation errors (null values, invalid format).
  - `404 Not Found` - The `userId` was not found in the `user-service`.
  - `409 Conflict` - Domain rule violations (overlapping slot, business hours boundaries, invalid duration/time slot).

### 2. Get Available Slots
- **GET** `/api/reservations/available?date={YYYY-MM-DD}&resource={ResourceName}`
- **Description**: Returns a full list of all 30-minute intervals between 08:00 and 18:00 for the specified date and resource. It indicates precisely which slots are already booked (`isAvailable: false`) and which are free (`isAvailable: true`).

---

## ⚙️ Local Development & Setup

### Prerequisites
- JDK 25 installed
- Docker (for Testcontainers and DevServices)
- Maven wrapper included (`mvnw`)

### Development Mode

Quarkus Dev Mode enables incredibly fast live coding, automatic recompilation, and the automatic provisioning of a PostgreSQL database container:

```bash
./mvnw quarkus:dev
```
- **Swagger UI:** `http://localhost:8080/q/swagger-ui/`
- **Quarkus Dev UI:** `http://localhost:8080/q/dev/`

### Configuration
Key settings in `src/main/resources/application.properties` to connect to `user-service`:
```properties
org.acme.infrastructure.client.UserServiceClient/mp-rest/url=http://localhost:8081
```

### Testing
Run the comprehensive test suite, encompassing localized unit tests and Testcontainer-backed integration tests:

```bash
./mvnw verify
```

### Packaging for Production
Compile the project to a runnable JAR:

```bash
./mvnw package
```
Launch the compiled application:
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

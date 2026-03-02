package org.acme.presentation;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.application.ReservationDto;
import org.acme.domain.entity.ReservationStatus;
import org.acme.infrastructure.UserRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class ReservationResourceIT {

        @InjectMock
        @RestClient
        UserRestClient userRestClient;

        @BeforeEach
        public void setup() {
                Mockito.when(userRestClient.getUserById("user-123"))
                                .thenReturn(new UserRestClient.UserDtoResponse("user-123", "testuser", "test@test.com",
                                                "Test User"));
        }

        @Test
        public void testCreateReservationSuccess() {
                LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0)
                                .withNano(0);
                LocalDateTime end = start.plusMinutes(30);

                ReservationDto dto = new ReservationDto(
                                null,
                                "user-123",
                                null,
                                "Room A",
                                start,
                                end,
                                null);

                given()
                                .contentType(ContentType.JSON)
                                .body(dto)
                                .when()
                                .post("/api/reservations")
                                .then()
                                .statusCode(201)
                                .body("id", notNullValue())
                                .body("userId", equalTo("user-123"))
                                .body("resourceName", equalTo("Room A"))
                                .body("status", equalTo(ReservationStatus.CONFIRMED.name()));
        }

        @Test
        public void testCreateReservationUserNotFound() {
                LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0)
                                .withNano(0);
                LocalDateTime end = start.plusMinutes(30);

                ReservationDto dto = new ReservationDto(
                                null,
                                "user-999", // Not stubbed
                                null,
                                "Room B",
                                start,
                                end,
                                null);

                given()
                                .contentType(ContentType.JSON)
                                .body(dto)
                                .when()
                                .post("/api/reservations")
                                .then()
                                .statusCode(400); // Mapped IllegalArgumentException to 400
        }
}

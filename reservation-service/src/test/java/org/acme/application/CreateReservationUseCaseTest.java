package org.acme.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import org.acme.domain.Reservation;
import org.acme.domain.SlotAlreadyBookedException;
import org.acme.domain.port.ReservationRepositoryPort;
import org.acme.domain.port.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateReservationUseCaseTest {

    @Mock
    ReservationRepositoryPort reservationRepositoryPort;

    @Mock
    UserGateway userGateway;

    CreateReservationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateReservationUseCase(reservationRepositoryPort, userGateway);
    }

    @Test
    void shouldCreateReservationSuccessfully() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 10, 0);
        LocalDateTime end = start.plusMinutes(30);

        ReservationDto incomingDto = new ReservationDto(
                null, "user-1", null, "room-a", start, end, null);

        when(userGateway.findUsernameById("user-1")).thenReturn(Optional.of("John Doe"));
        when(reservationRepositoryPort.findByResourceAndDate("room-a", start.toLocalDate())).thenReturn(List.of());

        when(reservationRepositoryPort.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation saved = invocation.getArgument(0);
            return saved; // return the same instance as if it was saved
        });

        // Act
        ReservationDto result = useCase.execute(incomingDto);

        // Assert
        assertNotNull(result.id());
        assertEquals("user-1", result.userId());
        assertEquals("John Doe", result.username());
        assertEquals("room-a", result.resourceName());

        verify(reservationRepositoryPort).save(any(Reservation.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 10, 0);
        LocalDateTime end = start.plusMinutes(30);
        ReservationDto incomingDto = new ReservationDto(null, "user-unknown", null, "room-a", start, end, null);

        when(userGateway.findUsernameById("user-unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(incomingDto));
        verify(reservationRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowWhenSlotAlreadyBooked() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 10, 0);
        LocalDateTime end = start.plusMinutes(30);
        ReservationDto incomingDto = new ReservationDto(null, "user-1", null, "room-a", start, end, null);

        Reservation existing = new Reservation("id-old", "user-2", "room-a", start, end);

        when(userGateway.findUsernameById("user-1")).thenReturn(Optional.of("John Doe"));
        when(reservationRepositoryPort.findByResourceAndDate("room-a", start.toLocalDate()))
                .thenReturn(List.of(existing));

        // Act & Assert
        assertThrows(SlotAlreadyBookedException.class, () -> useCase.execute(incomingDto));
        verify(reservationRepositoryPort, never()).save(any());
    }
}

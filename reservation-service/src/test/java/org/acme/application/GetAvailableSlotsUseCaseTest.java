package org.acme.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.acme.domain.Reservation;
import org.acme.domain.port.ReservationRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAvailableSlotsUseCaseTest {

    @Mock
    ReservationRepositoryPort reservationRepositoryPort;

    GetAvailableSlotsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetAvailableSlotsUseCase(reservationRepositoryPort);
    }

    @Test
    void shouldReturnAllSlotsAvailableWhenNoReservations() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 3, 2);
        String resource = "room-a";

        when(reservationRepositoryPort.findByResourceAndDate(resource, date)).thenReturn(List.of());

        // Act
        List<AvailableSlotDto> slots = useCase.execute(date, resource);

        // Assert
        assertEquals(20, slots.size()); // 8am to 6pm, 30 min intervals = 10 hours * 2 = 20 slots
        assertTrue(slots.stream().allMatch(AvailableSlotDto::isAvailable));
    }

    @Test
    void shouldMarkSlotsAsNotAvailableWhenReserved() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 3, 2);
        String resource = "room-a";

        // Slot at 09:00 - 09:30
        LocalDateTime start1 = LocalDateTime.of(2026, 3, 2, 9, 0);
        Reservation res1 = new Reservation("1", "user-1", resource, start1, start1.plusMinutes(30));

        // Slot at 14:30 - 15:00
        LocalDateTime start2 = LocalDateTime.of(2026, 3, 2, 14, 30);
        Reservation res2 = new Reservation("2", "user-2", resource, start2, start2.plusMinutes(30));

        when(reservationRepositoryPort.findByResourceAndDate(resource, date)).thenReturn(List.of(res1, res2));

        // Act
        List<AvailableSlotDto> slots = useCase.execute(date, resource);

        // Assert
        assertEquals(20, slots.size());

        Optional<AvailableSlotDto> slot9am = slots.stream().filter(s -> s.startDate().equals(start1)).findFirst();
        assertTrue(slot9am.isPresent());
        assertFalse(slot9am.get().isAvailable());

        Optional<AvailableSlotDto> slot230pm = slots.stream().filter(s -> s.startDate().equals(start2)).findFirst();
        assertTrue(slot230pm.isPresent());
        assertFalse(slot230pm.get().isAvailable());

        // A random other slot should be available
        Optional<AvailableSlotDto> slot10am = slots.stream()
                .filter(s -> s.startDate().equals(LocalDateTime.of(2026, 3, 2, 10, 0))).findFirst();
        assertTrue(slot10am.isPresent());
        assertTrue(slot10am.get().isAvailable());
    }
}

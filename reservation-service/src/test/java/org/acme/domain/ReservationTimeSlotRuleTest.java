package org.acme.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ReservationTimeSlotRuleTest {

    @Test
    void shouldAcceptStartAtMinuteZero() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 10, 0);
        assertDoesNotThrow(() -> new Reservation("id-1", "user-1", "room-a", start, start.plusMinutes(30)));
    }

    @Test
    void shouldAcceptStartAtMinuteThirty() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 10, 30);
        assertDoesNotThrow(() -> new Reservation("id-1", "user-1", "room-a", start, start.plusMinutes(30)));
    }

    @Test
    void shouldRejectStartAtOtherMinutes() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 10, 15);
        assertThrows(InvalidTimeSlotException.class,
                () -> new Reservation("id-1", "user-1", "room-a", start, start.plusMinutes(30)));
    }
}

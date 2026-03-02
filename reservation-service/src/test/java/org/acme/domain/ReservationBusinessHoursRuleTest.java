package org.acme.domain;

import org.acme.domain.entity.Reservation;
import org.acme.domain.exceptions.BusinessHoursViolationException;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ReservationBusinessHoursRuleTest {

    @Test
    void shouldAcceptWithinBusinessHours() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 8, 0);
        assertDoesNotThrow(() -> new Reservation("id-1", "user-1", "room-a", start, start.plusMinutes(30)));
    }

    @Test
    void shouldRejectBeforeBusinessHours() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 7, 30);
        assertThrows(BusinessHoursViolationException.class,
                () -> new Reservation("id-1", "user-1", "room-a", start, start.plusMinutes(30)));
    }

    @Test
    void shouldRejectAfterBusinessHours() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 18, 0);
        assertThrows(BusinessHoursViolationException.class,
                () -> new Reservation("id-1", "user-1", "room-a", start, start.plusMinutes(30)));
    }
}

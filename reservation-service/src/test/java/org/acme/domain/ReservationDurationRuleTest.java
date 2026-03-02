package org.acme.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ReservationDurationRuleTest {

    @Test
    void shouldAcceptExactly30MinutesDuration() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 10, 0);
        LocalDateTime end = start.plusMinutes(30);

        assertDoesNotThrow(() -> new Reservation("id-1", "user-1", "room-a", start, end));
    }

    @Test
    void shouldRejectMoreThan30MinutesDuration() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 10, 0);
        LocalDateTime end = start.plusMinutes(31);

        assertThrows(InvalidDurationException.class, () -> new Reservation("id-1", "user-1", "room-a", start, end));
    }

    @Test
    void shouldRejectLessThan30MinutesDuration() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 2, 10, 0);
        LocalDateTime end = start.plusMinutes(29);

        assertThrows(InvalidDurationException.class, () -> new Reservation("id-1", "user-1", "room-a", start, end));
    }
}

package org.acme.domain.port;

import org.acme.domain.Reservation;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepositoryPort {
    Reservation save(Reservation reservation);

    List<Reservation> findByResourceAndDate(String resourceName, LocalDate date);
}

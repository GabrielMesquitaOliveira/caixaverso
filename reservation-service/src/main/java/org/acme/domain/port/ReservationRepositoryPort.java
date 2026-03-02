package org.acme.domain.port;

import java.time.LocalDate;
import java.util.List;

import org.acme.domain.entity.Reservation;

public interface ReservationRepositoryPort {
    Reservation save(Reservation reservation);

    List<Reservation> findByResourceAndDate(String resourceName, LocalDate date);
}

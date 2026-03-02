package org.acme.application;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.acme.domain.entity.Reservation;
import org.acme.domain.port.ReservationRepositoryPort;

@ApplicationScoped
public class GetAvailableSlotsUseCase {

    private final ReservationRepositoryPort reservationRepositoryPort;

    public GetAvailableSlotsUseCase(ReservationRepositoryPort reservationRepositoryPort) {
        this.reservationRepositoryPort = reservationRepositoryPort;
    }

    public List<AvailableSlotDto> execute(LocalDate date, String resourceName) {
        List<Reservation> existingReservations = reservationRepositoryPort.findByResourceAndDate(resourceName, date);
        List<AvailableSlotDto> slots = new ArrayList<>();

        LocalDateTime currentSlotStart = date.atTime(8, 0);
        LocalDateTime endOfDay = date.atTime(18, 0);

        while (currentSlotStart.isBefore(endOfDay)) {
            LocalDateTime currentSlotEnd = currentSlotStart.plusMinutes(30);

            // Reassign for lambda capture
            final LocalDateTime finalStart = currentSlotStart;
            final LocalDateTime finalEnd = currentSlotEnd;

            boolean isBooked = existingReservations.stream()
                    .anyMatch(res -> finalStart.isBefore(res.getEndDate()) && finalEnd.isAfter(res.getStartDate()));

            slots.add(new AvailableSlotDto(finalStart, finalEnd, !isBooked));

            currentSlotStart = currentSlotEnd;
        }

        return slots;
    }
}

package org.acme.application;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import java.util.List;

import org.acme.domain.entity.Reservation;
import org.acme.domain.exceptions.SlotAlreadyBookedException;
import org.acme.domain.port.ReservationRepositoryPort;
import org.acme.domain.port.UserGateway;

@ApplicationScoped
public class CreateReservationUseCase {

        private final ReservationRepositoryPort reservationRepositoryPort;
        private final UserGateway userGateway;

        public CreateReservationUseCase(ReservationRepositoryPort reservationRepositoryPort, UserGateway userGateway) {
                this.reservationRepositoryPort = reservationRepositoryPort;
                this.userGateway = userGateway;
        }

        public ReservationDto execute(ReservationDto reservationDto) {
                // 1. Cross-service validation: verify user exists
                String username = userGateway.findUsernameById(reservationDto.userId())
                                .orElseThrow(() -> new IllegalArgumentException("User ID not found via User Service"));

                String id = UUID.randomUUID().toString();

                // 2. Execute core domain validations via Entity Constructor
                Reservation reservation = new Reservation(
                                id,
                                reservationDto.userId(),
                                reservationDto.resourceName(),
                                reservationDto.startDate(),
                                reservationDto.endDate());

                // 3. Overlap / Double Booking Rule check
                List<Reservation> existingReservations = reservationRepositoryPort.findByResourceAndDate(
                                reservation.getResourceName(),
                                reservation.getStartDate().toLocalDate());

                boolean hasOverlap = existingReservations.stream()
                                // Active reservations only (assuming we filter cancelled ones, but let's check
                                // all returned for now)
                                .anyMatch(existing -> reservation.getStartDate().isBefore(existing.getEndDate()) &&
                                                reservation.getEndDate().isAfter(existing.getStartDate()));

                if (hasOverlap) {
                        throw new SlotAlreadyBookedException(
                                        "Slot is already booked for resource " + reservation.getResourceName());
                }

                // 4. Save to Repository
                Reservation savedReservation = reservationRepositoryPort.save(reservation);

                // 5. Return mapped DTO enriched with username
                return new ReservationDto(
                                savedReservation.getId(),
                                savedReservation.getUserId(),
                                username,
                                savedReservation.getResourceName(),
                                savedReservation.getStartDate(),
                                savedReservation.getEndDate(),
                                savedReservation.getStatus());
        }
}

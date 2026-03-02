package org.acme.domain;

import java.time.LocalDateTime;
import java.time.Duration;

public class Reservation {

    private static final int SLOT_DURATION_MINUTES = 30;
    private static final int BUSINESS_HOUR_START = 8;
    private static final int BUSINESS_HOUR_END = 18;

    private final String id;
    private final String userId;
    private final String resourceName;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private ReservationStatus status;

    public Reservation(String id, String userId, String resourceName, LocalDateTime startDate, LocalDateTime endDate) {
        validateTimeSlot(startDate);
        validateDuration(startDate, endDate);
        validateBusinessHours(startDate, endDate);

        this.id = id;
        this.userId = userId;
        this.resourceName = resourceName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = ReservationStatus.CONFIRMED;
    }

    private void validateTimeSlot(LocalDateTime start) {
        if (start.getMinute() != 0 && start.getMinute() != 30) {
            throw new InvalidTimeSlotException("Reservation must start at :00 or :30 minutes.");
        }
        if (start.getSecond() != 0 || start.getNano() != 0) {
            throw new InvalidTimeSlotException("Reservation start time must have zero seconds and nanos.");
        }
    }

    private void validateDuration(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new InvalidDurationException("Reservation start date must be before end date.");
        }
        Duration duration = Duration.between(start, end);
        if (duration.toMinutes() != SLOT_DURATION_MINUTES) {
            throw new InvalidDurationException(
                    "Reservation must be exactly " + SLOT_DURATION_MINUTES + " minutes long.");
        }
    }

    private void validateBusinessHours(LocalDateTime start, LocalDateTime end) {
        if (start.getHour() < BUSINESS_HOUR_START) {
            throw new BusinessHoursViolationException("Reservations cannot start before " + BUSINESS_HOUR_START + "h.");
        }
        // If end hour is > 18, or if it's 18 but minutes > 0
        if (end.getHour() > BUSINESS_HOUR_END || (end.getHour() == BUSINESS_HOUR_END
                && (end.getMinute() > 0 || end.getSecond() > 0 || end.getNano() > 0))) {
            throw new BusinessHoursViolationException("Reservations cannot end past " + BUSINESS_HOUR_END + "h.");
        }
        if (start.toLocalDate().isAfter(end.toLocalDate())) {
            throw new BusinessHoursViolationException("Cross-day reservations are not allowed.");
        }
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}

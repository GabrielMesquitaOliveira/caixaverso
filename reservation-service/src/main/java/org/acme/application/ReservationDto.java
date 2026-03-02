package org.acme.application;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.LocalDateTime;

import org.acme.domain.entity.ReservationStatus;

@RecordBuilder
public record ReservationDto(
                String id,
                String userId,
                String username,
                String resourceName,
                LocalDateTime startDate,
                LocalDateTime endDate,
                ReservationStatus status) {
}

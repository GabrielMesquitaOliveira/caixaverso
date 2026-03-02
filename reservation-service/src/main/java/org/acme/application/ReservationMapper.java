package org.acme.application;

import org.acme.domain.entity.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ReservationMapper {

    ReservationDto toDto(Reservation reservation);

    Reservation toEntity(ReservationDto dto);
}

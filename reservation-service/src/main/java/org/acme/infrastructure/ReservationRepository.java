package org.acme.infrastructure;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.acme.domain.entity.Reservation;
import org.acme.domain.port.ReservationRepositoryPort;

@ApplicationScoped
public class ReservationRepository
        implements PanacheRepositoryBase<ReservationJpaEntity, String>, ReservationRepositoryPort {

    @Override
    public Reservation save(Reservation reservation) {
        ReservationJpaEntity entity = toJpaEntity(reservation);
        persist(entity);
        return toDomainEntity(entity);
    }

    @Override
    public List<Reservation> findByResourceAndDate(String resourceName, LocalDate date) {
        return find("resourceName = ?1 AND cast(startDate as date) = ?2", resourceName, date)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    private ReservationJpaEntity toJpaEntity(Reservation reservation) {
        ReservationJpaEntity entity = new ReservationJpaEntity();
        entity.setId(reservation.getId());
        entity.setUserId(reservation.getUserId());
        entity.setResourceName(reservation.getResourceName());
        entity.setStartDate(reservation.getStartDate());
        entity.setEndDate(reservation.getEndDate());
        entity.setStatus(reservation.getStatus());
        return entity;
    }

    private Reservation toDomainEntity(ReservationJpaEntity entity) {
        Reservation res = new Reservation(
                entity.getId(),
                entity.getUserId(),
                entity.getResourceName(),
                entity.getStartDate(),
                entity.getEndDate());
        if (entity.getStatus() == org.acme.domain.entity.ReservationStatus.CANCELLED) {
            res.cancel();
        }
        return res;
    }
}

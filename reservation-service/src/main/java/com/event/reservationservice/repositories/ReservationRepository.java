package com.event.reservationservice.repositories;

import com.event.reservationservice.entitites.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByIdEvent(Long idEvent);
}

package com.event.reservationservice.repositories;

import com.event.reservationservice.entitites.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}

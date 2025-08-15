package com.event.reservationservice.entitites;

import com.event.reservationservice.models.Client;
import com.event.reservationservice.models.Event;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;
    private LocalDateTime reservationDateTime;
    private String statu;

    private Long idEvent;
    @Transient
    private Event event;

    private Long idClient;
    @Transient
    private Client client;

}

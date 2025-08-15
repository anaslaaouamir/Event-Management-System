package com.event.reservationservice.entitites;

import com.event.reservationservice.models.Client;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSeat;
    private int rowNumber;
    private int columnNumber;
    private String statu;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="idReservation")
    private Reservation reservation;
}

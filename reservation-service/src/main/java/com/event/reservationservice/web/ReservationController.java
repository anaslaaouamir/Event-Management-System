package com.event.reservationservice.web;

import com.event.reservationservice.entitites.Reservation;
import com.event.reservationservice.models.Client;
import com.event.reservationservice.models.Event;
import com.event.reservationservice.repositories.ReservationRepository;
import jakarta.ws.rs.DELETE;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReservationController {

    private ReservationRepository reservationRepository;
    private EventOpenFeign eventOpenFeign;
    private ClientOpenFeign clientOpenFeign;

    public ReservationController(ReservationRepository reservationRepository, EventOpenFeign eventOpenFeign,ClientOpenFeign clientOpenFeign) {
        this.reservationRepository = reservationRepository;
        this.eventOpenFeign = eventOpenFeign;
        this.clientOpenFeign = clientOpenFeign;
    }

    @PostMapping("/reservations")
    public void addReservation(@RequestBody Reservation reservation) {
        reservationRepository.save(reservation);
        eventOpenFeign.decrementCapacity(reservation.getIdEvent());
    }

    @GetMapping("/reservations_client/{id_client}")
    public List<Reservation> reservationsByClient(@PathVariable Long id_client) {
        List<Reservation> reservations = new ArrayList<>();
        List<Reservation> reservations1 = reservationRepository.findAll();
        Client client = clientOpenFeign.getClient(id_client);
        for (Reservation reservation : reservations1) {
            if(reservation.getIdClient() == id_client) {
                Event event = eventOpenFeign.getEvent(reservation.getIdEvent());
                reservation.setEvent(event);
                reservation.setClient(client);
                reservations.add(reservation);}
        }
        return reservations;
    }

    @GetMapping("/reservations_event/{id_event}")
    public List<Reservation> reservationsByEvent(@PathVariable Long id_event) {
        List<Reservation> reservations = new ArrayList<>();
        List<Reservation> reservations1 = reservationRepository.findAll();
        Event event = eventOpenFeign.getEvent(id_event);
        for (Reservation reservation : reservations1) {
            if(reservation.getIdEvent() == id_event) {
                Client client = clientOpenFeign.getClient(reservation.getIdClient());
                reservation.setEvent(event);
                reservation.setClient(client);
                reservations.add(reservation);}
        }
        return reservations;
    }

    @DeleteMapping("/reservations/{id}")
    public void cancelReservation(@PathVariable Long id){
        Reservation reservation = reservationRepository.findById(id).get();
        eventOpenFeign.incrementCapacity(reservation.getIdEvent());
        reservationRepository.deleteById(id);
    }
}

package com.event.reservationservice.web;

import com.event.reservationservice.entitites.Reservation;
import com.event.reservationservice.models.Client;
import com.event.reservationservice.models.Event;
import com.event.reservationservice.repositories.ReservationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.event.reservationservice.models.Class;


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

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/reservations")
    public Reservation addReservation(@RequestBody Reservation reservation) {
        reservationRepository.save(reservation);
        eventOpenFeign.decrementCapacity(reservation.getIdEvent());
        eventOpenFeign.decrementClasse(reservation.getIdClasse());
        return reservation;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/reservations_client/{id_client}")
    public List<Reservation> reservationsByClient(@PathVariable Long id_client) {
        List<Reservation> reservations = new ArrayList<>();
        List<Reservation> reservations1 = reservationRepository.findAll();
        Client client = clientOpenFeign.getClient(id_client);
        for (Reservation reservation : reservations1) {
            if(reservation.getIdClient() == id_client) {
                Event event = eventOpenFeign.getEvent(reservation.getIdEvent());
                Class classe = eventOpenFeign.getClasse(reservation.getIdClasse());
                reservation.setEvent(event);
                reservation.setClient(client);
                reservation.setClasse(classe);
                reservations.add(reservation);}
        }
        return reservations;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
     adMapping ("/reservations")
    public List<Reservation> allReservations(){
        return reservationRepository.findAll();
    }



    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/reservations_event/{id_event}")
    public List<Reservation> reservationsByEvent(@PathVariable Long id_event) {
        List<Reservation> reservations = new ArrayList<>();
        List<Reservation> reservations1 = reservationRepository.findAll();
        Event event = eventOpenFeign.getEvent(id_event);
        for (Reservation reservation : reservations1) {
            if(reservation.getIdEvent() == id_event) {
                Client client = clientOpenFeign.getClient(reservation.getIdClient());
                Class classe = eventOpenFeign.getClasse(reservation.getIdClasse());
                reservation.setClasse(classe);
                reservation.setEvent(event);
                reservation.setClient(client);
                reservations.add(reservation);}
        }
        return reservations;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/reservations/{id}")
    public void cancelReservation(@PathVariable Long id){
        Reservation reservation = reservationRepository.findById(id).get();
        eventOpenFeign.incrementCapacity(reservation.getIdEvent());
        eventOpenFeign.incrementClasse(reservation.getIdClasse());
        reservationRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete_by_event/{id_event}")
    public void deleteReservationByEvent(@PathVariable Long id_event){
        List<Reservation> reservations = reservationRepository.findByIdEvent(id_event);
        List<Reservation> reservations1 = reservationRepository.findAll();
        for(Reservation reservation1 : reservations1) {
            for(Reservation reservation : reservations) {
                if(reservation.getIdReservation() == reservation1.getIdReservation()) {
                    reservationRepository.delete(reservation);
                }
            }
        }
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }
}

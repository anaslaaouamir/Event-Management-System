package com.event.eventservice.web;

import com.event.eventservice.web.ClasseController;
import com.event.eventservice.entities.Class;
import com.event.eventservice.entities.Event;
import com.event.eventservice.repositories.ClasseRepository;
import com.event.eventservice.repositories.EventRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@RestController
public class EventController {

    private EventRepository eventRepository;
    private ClasseRepository classRepository;
    private ReservationOpenFeign reservationOpenFeign;

    public EventController(EventRepository eventRepository, ClasseRepository classRepository, ReservationOpenFeign reservationOpenFeign) {
        this.eventRepository = eventRepository;
        this.classRepository = classRepository;
        this.reservationOpenFeign = reservationOpenFeign;
    }


    @GetMapping("/events")
    public List<Event> getEvents() {
        System.out.println(eventRepository.findAll());
        return eventRepository.findAll();
    }

    @GetMapping("/events/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventRepository.findById(id).get();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/events")
    public Event addEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return event;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/events/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event event) {
        Event existingEvent = eventRepository.findById(id).orElseThrow();

        // Update basic Event fields
        existingEvent.setTitle(event.getTitle());
        existingEvent.setLocation(event.getLocation());
        existingEvent.setEventDateTime(event.getEventDateTime());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setImagePath(event.getImagePath());
        existingEvent.setSalleImagePath(event.getSalleImagePath());

        int newFullCapacity = 0;
        int newCapacity = 0;

        System.out.println("Classes in request:");
        for (Class c : event.getClasses()) {
            System.out.println("idClass=" + c.getIdClass() + ", name=" + c.getClassName() + ", fullCapacity=" + c.getFullCapacity());
        }

        if (event.getClasses() != null) {
            for (Class updatedClass : event.getClasses()) {
                Class existingClass = classRepository.findById(updatedClass.getIdClass())
                        .orElseThrow(() -> new RuntimeException("Class not found"));

                int difference;
                System.out.println("**************************** from request"+updatedClass.getFullCapacity()+" old  "+ existingClass.getFullCapacity());
                difference=existingClass.getFullCapacity() - updatedClass.getFullCapacity() ;
                System.out.println("**************************** difference"+difference);
                existingClass.setCapacity(existingClass.getCapacity() - difference);
                System.out.println("**************************** class"+existingClass.getClassName()+"   "+ existingClass.getCapacity());

                existingClass.setClassName(updatedClass.getClassName());
                //existingClass.setDescription(updatedClass.getDescription());
                //existingClass.setPrice(updatedClass.getPrice());
                existingClass.setFullCapacity(updatedClass.getFullCapacity());

                classRepository.save(existingClass);

                newFullCapacity += existingClass.getFullCapacity();
                newCapacity += existingClass.getCapacity();
            }
        }
        existingEvent.setCapacity(newCapacity);
        existingEvent.setFullCapacity(newFullCapacity);
        eventRepository.save(existingEvent);
        return existingEvent;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/events/{id}")
    public void deleteEvent(@PathVariable Long id) {
        reservationOpenFeign.deleteReservationByEvent(id);
        eventRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/dcrement/{id}")
    public void decrementCapacity(@PathVariable Long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        event.setCapacity(event.getCapacity() - 1);
        eventRepository.save(event);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/increment/{id}")
    public void incrementCapacity(@PathVariable Long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        event.setCapacity(event.getCapacity() + 1);
        eventRepository.save(event);
    }
}

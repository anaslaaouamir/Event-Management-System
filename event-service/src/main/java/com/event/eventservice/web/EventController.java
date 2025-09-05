package com.event.eventservice.web;

import com.event.eventservice.entities.Event;
import com.event.eventservice.repositories.EventRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class EventController {

    private EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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
    public void addEvent(@RequestBody Event event) {
        eventRepository.save(event);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/events/{id}")
    public void updateEvent(@PathVariable Long id, @RequestBody Event event) {
        Event event1 = eventRepository.findById(id).orElseThrow();
        BeanUtils.copyProperties(event, event1);
        eventRepository.save(event1);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/events/{id}")
    public void deleteEvent(@PathVariable Long id) {
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

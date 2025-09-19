package com.event.reservationservice.web;
import com.event.reservationservice.models.Class;
import com.event.reservationservice.models.Client;
import com.event.reservationservice.models.Event;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.concurrent.TimeUnit;

@FeignClient(name = "EVENT-SERVICE")

public interface EventOpenFeign {

    Cache<Long, Event> eventCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    Cache<Long, Class> classCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    @CircuitBreaker(name="reservationServiceCB1", fallbackMethod = "getDefaultEvent")
    default Event getEvent(Long id) {
        Event fetchedEvent = fetchEventFromService(id);
        Event cachedEvent = eventCache.getIfPresent(id);

        if (cachedEvent == null) {
            eventCache.put(id, fetchedEvent);
        }
        return fetchedEvent;
    }

    @GetMapping("/events/{id}")
    Event fetchEventFromService(@PathVariable Long id);

    default Event getDefaultEvent(Long id, Throwable t) {
        System.out.println("**********************inside getDefaultEvent*********");

        Event cachedEvent = eventCache.getIfPresent(id);
        if (cachedEvent != null) {
            return cachedEvent;
        }
        // Return a default Event object
        Event event=new Event();event.setIdEvent(id);event.setTitle("default event");
        return event;
    }


    @CircuitBreaker(name="reservationServiceCB2", fallbackMethod = "getDefaultClass")
    default Class getClasse(Long id) {
        Class fetchedClass = fetchClassFromService(id);
        Class cachedClass = classCache.getIfPresent(id);

        if (cachedClass == null) {
            classCache.put(id, fetchedClass);
        }
        return fetchedClass;
    }

    @GetMapping("/classes/{id}")
    Class fetchClassFromService(@PathVariable Long id);

    default Class getDefaultClass(Long id, Throwable t) {
        System.out.println("**********************inside getDefaultClass*********");

        Class cachedClass = classCache.getIfPresent(id);
        if (cachedClass != null) {
            return cachedClass;
        }
        Class c=new Class();c.setIdClass(id);c.setClassName("default name");
        return c;// or a default Class object
    }




    @PutMapping("/increment/{id}")
    public void incrementCapacity(@PathVariable Long id);

    @PutMapping("/dcrement/{id}")
    public void decrementCapacity(@PathVariable Long id);


    @PutMapping("/dcrement_classe/{id}")
    public void decrementClasse(@PathVariable Long id);

    @PutMapping("/increment_classe/{id}")
    public void incrementClasse(@PathVariable Long id);

}

package com.event.reservationservice.web;

import com.event.reservationservice.models.Event;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "EVENT-SERVICE")

public interface EventOpenFeign {

    @GetMapping("/events/{id}")
    public Event getEvent(@PathVariable Long id);

    @PutMapping("/increment/{id}")
    public void incrementCapacity(@PathVariable Long id);

    @PutMapping("/dcrement/{id}")
    public void decrementCapacity(@PathVariable Long id);
}

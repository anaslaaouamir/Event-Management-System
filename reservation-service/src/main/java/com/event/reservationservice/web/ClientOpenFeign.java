package com.event.reservationservice.web;

import com.event.reservationservice.models.Client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CLIENT-SERVICE")

public interface ClientOpenFeign {
    @GetMapping("/clients/{id}")
    public Client getClient(@PathVariable Long id);
}

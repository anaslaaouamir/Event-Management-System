package com.event.eventservice.web;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "reservation-service")

public interface ReservationOpenFeign {
    @DeleteMapping("/delete_by_event/{id_event}")
    public void deleteReservationByEvent(@PathVariable Long id_event);
}

package com.event.reservationservice.models;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter

public class Event {
    private Long idEvent;
    private LocalDateTime eventDateTime;
    private String title;
    private String description;
    private String location;
    private int capacity;
}

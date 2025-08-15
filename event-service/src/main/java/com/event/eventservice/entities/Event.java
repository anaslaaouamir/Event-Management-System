package com.event.eventservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter

public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvent;
    private LocalDateTime eventDateTime;
    private String title;
    private String description;
    private String location;
    private int capacity;
    private String placeType;


}

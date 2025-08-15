package com.event.reservationservice.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter

public class Client {
    private Long idClient;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
}

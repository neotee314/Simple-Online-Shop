package com.neotee.ecommercesystem.solution.client.application.dto;

import lombok.*;

import java.util.UUID;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private UUID id;
    private String name;
    private String emailString;
    private String city;
    private String street;
    private String zipCodeString;
}

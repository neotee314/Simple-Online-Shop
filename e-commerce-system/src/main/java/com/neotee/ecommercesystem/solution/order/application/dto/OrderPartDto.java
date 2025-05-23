package com.neotee.ecommercesystem.solution.order.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPartDto {
    private UUID thingId ;
    private int quantity = 0;

}

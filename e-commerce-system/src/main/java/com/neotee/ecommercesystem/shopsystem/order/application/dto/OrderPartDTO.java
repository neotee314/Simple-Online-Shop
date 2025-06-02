package com.neotee.ecommercesystem.shopsystem.order.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPartDTO {
    private UUID thingId ;
    private int quantity ;

}

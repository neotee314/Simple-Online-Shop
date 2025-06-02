package com.neotee.ecommercesystem.shopsystem.order.application.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private UUID orderId;
    private String clientEmail;
    private List<OrderPartDTO> orderParts;
}

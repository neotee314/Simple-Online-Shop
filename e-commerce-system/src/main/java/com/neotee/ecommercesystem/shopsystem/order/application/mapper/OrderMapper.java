package com.neotee.ecommercesystem.shopsystem.order.application.mapper;

import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderPartRequestDTO;
import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderPartResponseDTO;
import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderResponseDTO;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderPart;
import com.neotee.ecommercesystem.shopsystem.product.application.service.ProductApplicationService;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ProductApplicationService productService;

    public OrderResponseDTO toResponseDTO(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderPartResponseDTO> partDTOs = order.getOrderParts().stream()
                .map(this::toPartResponseDTO)
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                order.getId(),
                order.getClientEmail() != null ? String.valueOf(order.getClientEmail()) : null,
                partDTOs,
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getSubmissionDate() != null ? order.getSubmissionDate().toString() : null
        );
    }

    public OrderPartResponseDTO toPartResponseDTO(OrderPart part) {
        if (part == null) {
            return null;
        }

        return new OrderPartResponseDTO(
                part.getProduct() != null ? part.getProduct().getId() : null,
                part.getProduct() != null ? part.getProduct().getName() : null,
                part.getOrderQuantity()
        );
    }

    public Map<Product, Integer> toProductQuantityMap(List<OrderPartRequestDTO> partDTOs) {
        if (partDTOs == null) {
            return Map.of();
        }

        return partDTOs.stream()
                .collect(Collectors.toMap(
                        dto -> productService.findById(dto.productId()),
                        OrderPartRequestDTO::quantity
                ));
    }
}
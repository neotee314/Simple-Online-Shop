package com.neotee.ecommercesystem.shopsystem.order.application.api;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.shopsystem.client.application.service.ClientApplicationService;
import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderResponseDTO;
import com.neotee.ecommercesystem.shopsystem.order.application.mapper.OrderMapper;
import com.neotee.ecommercesystem.shopsystem.order.application.service.OrderApplicationService;
import com.neotee.ecommercesystem.shopsystem.product.application.service.ProductApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Verwaltung von Bestellungen")
public class OrderController {

    private final OrderApplicationService orderService;
    private final ClientApplicationService clientService;
    private final ProductApplicationService productService;
    private final OrderMapper orderMapper;

    @Operation(summary = "Get order by ID")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable OrderId id) {
        var order = orderService.findById(id);
        return ResponseEntity.ok(orderMapper.toResponseDTO(order));
    }

    @Operation(summary = "Get order history by email")
    @GetMapping("/history")
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(@RequestParam String email) {
        var clientEmail = Email.of(email);
        var orders = orderService.findByClientEmail(clientEmail);
        var response = orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Submit an order")
    @PatchMapping("/{id}/submit")
    public ResponseEntity<Void> submitOrder(@PathVariable OrderId id) {
        orderService.submitOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cancel an order")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable OrderId id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deliver an order")
    @PatchMapping("/{id}/deliver")
    public ResponseEntity<Void> deliverOrder(@PathVariable OrderId id) {
        orderService.deliverOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all orders")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllOrders() {
        orderService.deleteAllOrders();
        return ResponseEntity.noContent().build();
    }
}
package com.neotee.ecommercesystem.shopsystem.order.application.controller;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderDTO;
import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderPartDTO;
import com.neotee.ecommercesystem.shopsystem.order.application.service.OrdeUseCaseService;
import com.neotee.ecommercesystem.shopsystem.order.application.service.OrderApplicationService;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import com.neotee.ecommercesystem.shopsystem.thing.domain.ThingRepository;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Verwaltung von Bestellungen")
public class OrderController {

    private final OrderApplicationService orderApplicationService;


    @Operation(summary = "Löscht alle Bestellungen")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllOrders() {
        orderApplicationService.deleteAllOrders();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Liefert die Bestellhistorie für eine E-Mail")
    @GetMapping("/history")
    public ResponseEntity<List<OrderDTO>> getOrderHistory(@RequestParam String email) {
        List<OrderDTO> history = orderApplicationService.getOrderHistory(email);
        return ResponseEntity.ok(history);
    }
}

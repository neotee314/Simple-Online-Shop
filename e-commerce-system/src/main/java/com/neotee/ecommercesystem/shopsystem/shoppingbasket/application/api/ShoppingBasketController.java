package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.api;

import com.neotee.ecommercesystem.domainprimitives.ClientId;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.ShoppingBasketId;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.CheckoutResponseDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketPartRequestDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketResponseDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.mapper.ShoppingBasketMapper;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.service.ShoppingBasketApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shoppingBaskets")
@RequiredArgsConstructor
public class ShoppingBasketController {

    private final ShoppingBasketApplicationService basketService;
    private final ShoppingBasketMapper basketMapper;


    @GetMapping
    public ResponseEntity<ShoppingBasketResponseDTO> getBasketByClientId(@RequestParam(required = false) UUID clientId) {
        if (clientId == null) return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        var basket = basketService.getBasketByClientId(ClientId.of(clientId));
        return ResponseEntity.ok(basketMapper.toResponseDTO(basket));
    }

    @Operation(summary = "Get basket by ID")
    @GetMapping("/{basketId}")
    public ResponseEntity<ShoppingBasketResponseDTO> getBasketById(@PathVariable UUID basketId) {
        var basket = basketService.getBasketById(ShoppingBasketId.of(basketId));
        return ResponseEntity.ok(basketMapper.toResponseDTO(basket));
    }

    @Operation(summary = "Add item to basket")
    @PostMapping("/{basketId}/parts")
    public ResponseEntity<ShoppingBasketResponseDTO> addItem(@PathVariable UUID basketId, @Valid @RequestBody ShoppingBasketPartRequestDTO request) {
        var basket = basketService.addItem(
                ShoppingBasketId.of(basketId),
                ProductId.of(request.productId()),
                request.quantity()
        );
        return new ResponseEntity<>(basketMapper.toResponseDTO(basket), HttpStatus.CREATED);
    }

    @Operation(summary = "Remove item from basket")
    @DeleteMapping("/{basketId}/parts/{productId}")
    public ResponseEntity<ShoppingBasketResponseDTO> removeItem(@PathVariable UUID basketId, @PathVariable UUID productId) {
        var basket = basketService.removeItem(ShoppingBasketId.of(basketId), ProductId.of(productId));
        return ResponseEntity.ok(basketMapper.toResponseDTO(basket));
    }

    @Operation(summary = "Remove item with quantity from basket")
    @DeleteMapping("/{basketId}/parts/{productId}/quantity/{quantity}")
    public ResponseEntity<ShoppingBasketResponseDTO> removeItemWithQuantity(@PathVariable UUID basketId, @PathVariable UUID productId, @PathVariable int quantity) {
        var basket = basketService.removeItemWithQuantity(ShoppingBasketId.of(basketId), ProductId.of(productId), quantity);
        return ResponseEntity.ok(basketMapper.toResponseDTO(basket));
    }

    @Operation(summary = "Clear basket")
    @DeleteMapping("/{basketId}/clear")
    public ResponseEntity<Void> clearBasket(@PathVariable UUID basketId) {
        basketService.clearBasket(ShoppingBasketId.of(basketId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Checkout basket")
    @PostMapping("/{basketId}/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkout(@PathVariable UUID basketId) {
        var orderId = basketService.checkout(ShoppingBasketId.of(basketId));
        return new ResponseEntity<>(new CheckoutResponseDTO(orderId.getId()), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete all baskets")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllBaskets() {
        basketService.deleteAllBaskets();
        return ResponseEntity.noContent().build();
    }
}
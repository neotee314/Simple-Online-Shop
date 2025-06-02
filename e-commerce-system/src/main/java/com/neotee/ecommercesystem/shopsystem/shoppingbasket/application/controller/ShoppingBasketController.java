package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.controller;

import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.IdDto;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketPartDto;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.service.ShoppingBasketApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shoppingBaskets")
@RequiredArgsConstructor
@Tag(name = "Shopping Basket Management", description = "Verwaltung von Shopping Basket")
public class ShoppingBasketController {

    private final ShoppingBasketApplicationService shoppingBasketApplicationService;

    @Operation(summary = "Get basket by ClientId", description = "Returns the shopping basket for the given client")
    @GetMapping
    public ResponseEntity<ShoppingBasketDTO> getBasketByClientId(@RequestParam(value = "clientId", required = false)UUID clientId) {
        ShoppingBasketDTO basket = shoppingBasketApplicationService.getBasketByClientId(clientId);
        return ResponseEntity.ok(basket);
    }

    @Operation(summary= "Get basket by id", description = "Returns the shoppingbasket for the given Id")
    @GetMapping("/{shoppingBasket-id}")
    public ResponseEntity<ShoppingBasketDTO> getBasketById(@PathVariable("shoppingBasket-id") UUID shoppingBasketId) {
        ShoppingBasketDTO basket = shoppingBasketApplicationService.getBasketById(shoppingBasketId);
        return ResponseEntity.ok(basket);
    }


    @Operation(summary = "Add thing to basket", description = "Adds an item to the shopping basket")
    @PostMapping("/{shoppingBasket-id}/parts")
    public ResponseEntity<Void> addThingToBasket(@PathVariable("shoppingBasket-id") UUID basketId,
                                                 @RequestBody ShoppingBasketPartDto request) {
        shoppingBasketApplicationService.addThingToBasket(basketId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Remove thing from basket", description = "Removes an item from the shopping basket")
    @DeleteMapping("/{shoppingBasket-id}/parts/{thing-id}")
    public ResponseEntity<Void> removeThingFromBasket(@PathVariable("shoppingBasket-id") UUID shoppingBasketId,
                                                      @PathVariable("thing-id") UUID thingId) {
        shoppingBasketApplicationService.removeThingFromShoppingBasket(shoppingBasketId, thingId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Checkout basket", description = "Checks out the basket and creates an order")
    @PostMapping("/{shoppingBasket-id}/checkout")
    public ResponseEntity<IdDto> checkoutBasket(@PathVariable("shoppingBasket-id") UUID shoppingBasketId) {
        UUID orderId = shoppingBasketApplicationService.checkoutBasket(shoppingBasketId);
        IdDto idDto = new IdDto();
        idDto.setId(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(idDto);
    }
}

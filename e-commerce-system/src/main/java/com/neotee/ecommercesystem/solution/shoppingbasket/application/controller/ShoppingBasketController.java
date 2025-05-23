package com.neotee.ecommercesystem.solution.shoppingbasket.application.controller;

import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.AddThingRequest;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.ShoppingBasketDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.ShoppingBasketPartDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.service.ShoppingBasketApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shoppingBaskets")
@RequiredArgsConstructor
@Tag(name = "Shopping Basket", description = "APIs for managing shopping baskets")
public class ShoppingBasketController {

    private final ShoppingBasketApplicationService shoppingBasketApplicationService;

    @Operation(summary = "Get basket by client ID", description = "Returns the shopping basket for the given client ID")
    @GetMapping
    public ShoppingBasketDto getBasketByClientId(@RequestParam UUID clientId) {
        return shoppingBasketApplicationService.getBasketByClientId(clientId);
    }

    @Operation(summary = "Add thing to basket", description = "Adds an item to the shopping basket")
    @PostMapping("/{basketId}/parts")
    public void addThingToBasket(@PathVariable UUID basketId, @RequestBody ShoppingBasketPartDto request) {
        shoppingBasketApplicationService.addThingToBasket(basketId, request);
    }

    @Operation(summary = "Remove thing from basket", description = "Removes an item from the shopping basket")
    @DeleteMapping("/{basketId}/parts/{thingId}")
    public void removeThingFromBasket(@PathVariable UUID basketId, @RequestBody ShoppingBasketPartDto request) {
        shoppingBasketApplicationService.removeThingFromBasket(basketId, request);
    }

    @Operation(summary = "Checkout basket", description = "Checks out the basket and creates an order")
    @PostMapping("/{basketId}/checkout")
    public void checkoutBasket(@PathVariable UUID basketId) {
        shoppingBasketApplicationService.checkoutBasket(basketId);
    }
}

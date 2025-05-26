package com.neotee.ecommercesystem.solution.shoppingbasket.application.controller;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.IdDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.ShoppingBasketDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.ShoppingBasketPartDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.service.ShoppingBasketApplicationService;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasket;
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
@Tag(name = "Shopping Basket", description = "APIs for managing shopping baskets")
public class ShoppingBasketController {

    private final ShoppingBasketApplicationService shoppingBasketApplicationService;


    @Operation(summary = "Get basket by ClientId", description = "Returns the shopping basket for the given client")
    @GetMapping
    public ResponseEntity<ShoppingBasketDto> getBasketByClientId(@RequestParam(value = "clientId", required = false) UUID clientId) {
        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

       ShoppingBasketDto shoppingBasketDto= shoppingBasketApplicationService.getBasketByClientId(clientId);
        if (shoppingBasketDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(shoppingBasketDto, HttpStatus.OK);


    }

    @Operation(summary = "Add thing to basket", description = "Adds an item to the shopping basket")
    @PostMapping("/{shoppingBasket-id}/parts")
    public ResponseEntity<?> addThingToBasket(@PathVariable("shoppingBasket-id") UUID basketId, @RequestBody ShoppingBasketPartDto request) {
        try {
            shoppingBasketApplicationService.addThingToBasket(basketId, request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            if (e.getMessage().contains("Thing is not available in the requested quantity")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if (e.getMessage().contains("ThingId is null"))
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            if (e.getMessage().contains("Thing quantity is less than or equal to zero"))
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            if (e.getMessage().contains("Shopping basket not found"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Remove thing from basket", description = "Removes an item from the shopping basket")
    @DeleteMapping("/{shoppingBasket-id}/parts/{thing-id}")
    public ResponseEntity<?> removeThingFromBasket(@PathVariable("shoppingBasket-id") UUID shoppingBasketId, @PathVariable("thing-id") UUID thingId) {
        try {
            if (shoppingBasketId == null || thingId == null)
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            ShoppingBasket shoppingBasket = shoppingBasketApplicationService.findById(shoppingBasketId);
            if (shoppingBasket == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            shoppingBasketApplicationService.removeThingFromShoppingBasket(shoppingBasket, thingId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ShopException e) {
            if (e.getMessage().equals("Thing is not in the cart of client"))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @Operation(summary = "Checkout basket", description = "Checks out the basket and creates an order")
    @PostMapping("/{shoppingBasket-id}/checkout")
    public ResponseEntity<IdDto> checkoutBasket(@PathVariable("shoppingBasket-id") UUID shoppingBasketId) {
        try {
            UUID orderId = shoppingBasketApplicationService.checkoutBasket(shoppingBasketId);
            IdDto idDto = new IdDto();
            idDto.setId(orderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(idDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}

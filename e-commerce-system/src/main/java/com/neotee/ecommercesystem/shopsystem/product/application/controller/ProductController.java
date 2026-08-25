package com.neotee.ecommercesystem.shopsystem.product.application.controller;

import com.neotee.ecommercesystem.shopsystem.product.application.dto.ProductRequestDto;
import com.neotee.ecommercesystem.shopsystem.product.application.dto.SalesPriceDto;
import com.neotee.ecommercesystem.shopsystem.product.application.dto.ProductResponseDto;
import com.neotee.ecommercesystem.shopsystem.product.application.service.ProductApplicationService;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/things")
@RequiredArgsConstructor
@Tag(name = "Thing Catalog", description = "Verwaltung von Produkte")
public class ProductController {

    private final ProductApplicationService productApplicationService;

    @Operation(summary = "Get all Things")
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDto>> getAllThings() {
        return ResponseEntity.ok(productApplicationService.getAllThings());
    }


    @Operation(summary = "Search things by name")
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> searchThingsByName(@RequestParam(value = "name", required = false) String name) {
        return ResponseEntity.ok(productApplicationService.searchThingsByName(name));
    }

    @Operation(summary = "Get thing by ID")
    @GetMapping("/{thing-id}")
    public ResponseEntity<ProductResponseDto> getThingById(@PathVariable("thing-id") UUID thingId) {
        return ResponseEntity.ok(productApplicationService.getThingById(thingId));
    }

    @Operation(summary = "Change sales price of a thing")
    @PatchMapping("/{thing-id}")
    public ResponseEntity<Void> changeSalesPrice(@PathVariable("thing-id") UUID thingId,
                                                 @RequestBody SalesPriceDto salesPriceDto) {
        productApplicationService.changeSalesPrice(thingId, salesPriceDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add thing to catalog")
    @PostMapping
    public ResponseEntity<Void> addThingToCatalog(@RequestBody ProductRequestDto dto) {
        productApplicationService.addThingToCatalog(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove thing from catalog")
    @DeleteMapping("/{thing-id}")
    public ResponseEntity<Void> removeThingFromCatalog(@PathVariable("thing-id") UUID thingId) {
        productApplicationService.removeThingFromCatalog(thingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get sales price of a thing")
    @GetMapping("/{thing-id}/salesPrice")
    public ResponseEntity<MoneyType> getSalesPrice(@PathVariable("thing-id") UUID thingId) {
        return ResponseEntity.ok(productApplicationService.getSalesPrice(thingId));
    }

    @Operation(summary = "Delete the entire catalog")
    @DeleteMapping
    public ResponseEntity<Void> deleteThingCatalog() {
        productApplicationService.deleteThingCatalog();
        return ResponseEntity.noContent().build();
    }
}

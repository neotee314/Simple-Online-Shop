package com.neotee.ecommercesystem.shopsystem.thing.application.controller;

import com.neotee.ecommercesystem.shopsystem.thing.application.dto.SalesPriceDTO;
import com.neotee.ecommercesystem.shopsystem.thing.application.dto.ThingDTO;
import com.neotee.ecommercesystem.shopsystem.thing.application.service.ThingApplicationService;
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
public class ThingController {

    private final ThingApplicationService thingApplicationService;

    @Operation(summary = "Get all Things")
    @GetMapping("/all")
    public ResponseEntity<List<ThingDTO>> getAllThings() {
        return ResponseEntity.ok(thingApplicationService.getAllThings());
    }


    @Operation(summary = "Search things by name")
    @GetMapping
    public ResponseEntity<List<ThingDTO>> searchThingsByName(@RequestParam(value = "name", required = false) String name) {
        return ResponseEntity.ok(thingApplicationService.searchThingsByName(name));
    }

    @Operation(summary = "Get thing by ID")
    @GetMapping("/{thing-id}")
    public ResponseEntity<ThingDTO> getThingById(@PathVariable("thing-id") UUID thingId) {
        return ResponseEntity.ok(thingApplicationService.getThingById(thingId));
    }

    @Operation(summary = "Change sales price of a thing")
    @PatchMapping("/{thing-id}")
    public ResponseEntity<Void> changeSalesPrice(@PathVariable("thing-id") UUID thingId,
                                                 @RequestBody SalesPriceDTO salesPriceDto) {
        thingApplicationService.changeSalesPrice(thingId, salesPriceDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add thing to catalog")
    @PostMapping
    public ResponseEntity<Void> addThingToCatalog(@RequestBody ThingDTO dto) {
        thingApplicationService.addThingToCatalog(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove thing from catalog")
    @DeleteMapping("/{thing-id}")
    public ResponseEntity<Void> removeThingFromCatalog(@PathVariable("thing-id") UUID thingId) {
        thingApplicationService.removeThingFromCatalog(thingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get sales price of a thing")
    @GetMapping("/{thing-id}/salesPrice")
    public ResponseEntity<MoneyType> getSalesPrice(@PathVariable("thing-id") UUID thingId) {
        return ResponseEntity.ok(thingApplicationService.getSalesPrice(thingId));
    }

    @Operation(summary = "Delete the entire catalog")
    @DeleteMapping
    public ResponseEntity<Void> deleteThingCatalog() {
        thingApplicationService.deleteThingCatalog();
        return ResponseEntity.noContent().build();
    }
}

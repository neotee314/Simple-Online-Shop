package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.controller;

import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service.DeliveryPackageUseCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/deliveryPackages")
@RequiredArgsConstructor
@Tag(name = "DeliveryPackage Management", description = "Verwaltung von DeliverPackages")
public class DeliveryPackageController {

    private final DeliveryPackageUseCaseService deliveryPackageService;

    @Operation(
            summary = "Gibt Storage-Units zurück, die zu einer Bestellung beitragen",
            description = "Diese Methode gibt alle StorageUnit-IDs zurück, die Pakete zu der angegebenen Bestellung beigetragen haben."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Erfolgreich gefunden"),
            @ApiResponse(responseCode = "404", description = "Keine Pakete für diese Bestellung gefunden"),
            @ApiResponse(responseCode = "500", description = "Interner Fehler")
    })
    @GetMapping("/contributors/{orderId}")
    public ResponseEntity<List<UUID>> getContributingStorageUnitsForOrder(
            @Parameter(description = "ID der Bestellung", required = true)
            @PathVariable UUID orderId) {

        List<UUID> result = deliveryPackageService.getContributingStorageUnitsForOrder(orderId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Gibt Artikel und Mengen für Bestellung und StorageUnit zurück",
            description = "Diese Methode gibt eine Map von Artikel-UUID zu Menge zurück, die von einer bestimmten StorageUnit zur Lieferung beigetragen wurden."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Erfolgreich gefunden"),
            @ApiResponse(responseCode = "404", description = "Keine passenden Pakete gefunden"),
            @ApiResponse(responseCode = "500", description = "Interner Fehler")
    })
    @GetMapping("/{orderId}/storageUnit/{storageUnitId}")
    public ResponseEntity<Map<UUID, Integer>> getDeliveryPackageParts(
            @Parameter(description = "ID der Bestellung", required = true)
            @PathVariable UUID orderId,
            @Parameter(description = "ID der StorageUnit", required = true)
            @PathVariable UUID storageUnitId) {

        Map<UUID, Integer> result = deliveryPackageService.getDeliveryPackageForOrderAndStorageUnit(orderId, storageUnitId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Löscht alle DeliveryPackages",
            description = "Diese Methode entfernt alle gespeicherten Lieferpakete. Vorsicht: Dies wirkt sich auf alle Bestellungen aus."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Erfolgreich gelöscht"),
            @ApiResponse(responseCode = "500", description = "Interner Fehler")
    })
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllDeliveryPackages() {
        deliveryPackageService.deleteAllDeliveryPackages();
        return ResponseEntity.noContent().build();
    }
}

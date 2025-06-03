package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.controller;

import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto.DeliveryPackageDTO;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service.DeliveryPackageApplicationService;
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

    private final DeliveryPackageApplicationService deliveryPackageApplicationService;

    @Operation(
            summary = "Gibt Storage-Units zurück, die zu einer Bestellung beitragen",
            description = "Diese Methode gibt alle StorageUnit-IDs zurück, die Pakete zu der angegebenen Bestellung beigetragen haben."
    )
    @GetMapping
    public ResponseEntity<List<DeliveryPackageDTO>> getContributingStorageUnitsForOrder(
            @Parameter(description = "ID der Bestellung")
            @RequestParam(value = "orderId", required = false) UUID orderId) {

        List<DeliveryPackageDTO> result = deliveryPackageApplicationService.getContributingStorageUnitsForOrder(orderId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Gibt Artikel und Mengen für Bestellung und StorageUnit zurück",
            description = "Diese Methode gibt eine Map von Artikel-UUID zu Menge zurück, die von einer bestimmten StorageUnit zur Lieferung beigetragen wurden."
    )
    @GetMapping("/{orderId}/storageUnit/{storageUnitId}")
    public ResponseEntity<List<DeliveryPackageDTO>> getDeliveryPackageParts(
            @Parameter(description = "ID der Bestellung", required = true)
            @PathVariable UUID orderId,
            @Parameter(description = "ID der StorageUnit", required = true)
            @PathVariable UUID storageUnitId) {

        List<DeliveryPackageDTO> result = deliveryPackageApplicationService.getDeliveryPackageForOrderAndStorageUnit(orderId, storageUnitId);
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
        deliveryPackageApplicationService.deleteAllDeliveryPackages();
        return ResponseEntity.noContent().build();
    }
}

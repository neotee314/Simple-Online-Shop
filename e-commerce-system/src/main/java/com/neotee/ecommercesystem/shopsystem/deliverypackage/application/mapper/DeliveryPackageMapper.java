package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.mapper;

import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto.DeliveryPackagePartResponseDTO;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto.DeliveryPackageResponseDTO;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackagePart;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DeliveryPackageMapper {

    public DeliveryPackageResponseDTO toResponseDTO(DeliveryPackage deliveryPackage) {
        if (deliveryPackage == null) {
            return null;
        }

        var partDTOs = deliveryPackage.getParts().stream()
                .map(this::toPartResponseDTO)
                .collect(Collectors.toList());

        return new DeliveryPackageResponseDTO(
                deliveryPackage.getId().getId(),
                deliveryPackage.getOrder().getId().getId(),
                deliveryPackage.getStorageUnitId().getId(),
                partDTOs,
                deliveryPackage.getTotalQuantity(),
                deliveryPackage.getPartCount()
        );
    }

    public DeliveryPackagePartResponseDTO toPartResponseDTO(DeliveryPackagePart part) {
        if (part == null) {
            return null;
        }

        return new DeliveryPackagePartResponseDTO(
                part.getProduct().getId().getId(),
                part.getProduct().getName(),
                part.getQuantity()
        );
    }
}
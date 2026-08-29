package com.neotee.ecommercesystem.shopsystem.delivery.application.mapper;

import com.neotee.ecommercesystem.shopsystem.delivery.application.dto.DeliveryPackageResponseDTO;
import com.neotee.ecommercesystem.shopsystem.delivery.application.dto.DeliveryResponseDTO;
import com.neotee.ecommercesystem.shopsystem.delivery.domain.model.Delivery;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.model.DeliveryPackage;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public DeliveryResponseDTO toResponseDTO(Delivery delivery) {
        if (delivery == null) return null;
        return new DeliveryResponseDTO(
                delivery.getId().getId(),
                delivery.getOrder().getId().getId(),
                delivery.getDeliveryRecipient().getName(),
                delivery.getDeliveryRecipient().getEmail().toString(),
                delivery.getDeliveryPackages().stream()
                        .map(pkg -> pkg.getId().getId())
                        .toList()
        );
    }

    public DeliveryPackageResponseDTO toPackageResponseDTO(DeliveryPackage deliveryPackage) {
        if (deliveryPackage == null) return null;
        return new DeliveryPackageResponseDTO(
                deliveryPackage.getId().getId(),
                deliveryPackage.getStatus() != null ? deliveryPackage.getStatus().name() : null
        );
    }
}
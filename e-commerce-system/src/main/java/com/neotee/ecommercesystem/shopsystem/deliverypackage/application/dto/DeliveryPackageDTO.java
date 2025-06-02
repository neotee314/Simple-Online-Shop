package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto;


import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DeliveryPackageDTO {
    private UUID id;
    private UUID storageUnitId;
    private UUID orderId;
    private List<DeliveryPackagePartDTO> deliveryPackageParts;

}

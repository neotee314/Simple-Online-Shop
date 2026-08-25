package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.mapper;

import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto.DeliveryPackagePartDTO;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackagePart;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import org.mapstruct.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DeliveryPackagePartMapper {

    @Autowired
    protected ProductService productService;

    @Mapping(target = "thingId", source = "thing", qualifiedByName = "mapThingToUUID")
    public abstract DeliveryPackagePartDTO toDto(DeliveryPackagePart part);

    @Mapping(target = "thing", source = "thingId", qualifiedByName = "mapUUIDToThing")
    public abstract DeliveryPackagePart toEntity(DeliveryPackagePartDTO partDto);

    @Named("mapThingToUUID")
    public UUID mapThingToUUID(Product product) {
        return product.getProductId().getId();
    }

    @Named("mapUUIDToThing")
    public Product mapUUIDToThing(UUID id) {
        return productService.findById(id);
    }

    public DeliveryPackagePartDTO map(UUID thingId, Integer quantity) {
        DeliveryPackagePartDTO dto = new DeliveryPackagePartDTO();
        dto.setThingId(thingId);
        dto.setQuantity(quantity);
        return dto;
    }
}

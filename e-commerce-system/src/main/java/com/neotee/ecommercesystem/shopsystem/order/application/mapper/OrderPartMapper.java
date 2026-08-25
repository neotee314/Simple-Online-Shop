package com.neotee.ecommercesystem.shopsystem.order.application.mapper;

import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderPartDTO;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderPart;
import com.neotee.ecommercesystem.shopsystem.product.application.service.ProductService;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderPartMapper {

    @Autowired
    protected ProductService productService;

    @Mapping(target = "thingId", source = "thing", qualifiedByName = "mapThingToUUID")
    @Mapping(target = "quantity", source = "orderQuantity")
    public abstract OrderPartDTO toDto(OrderPart orderPart);

    @Mapping(target = "thing", source = "thingId", qualifiedByName = "mapUUIDToThing")
    @Mapping(target = "orderQuantity", source = "quantity")
    public abstract OrderPart toEntity(OrderPartDTO orderPartDTO);

    @Named("mapThingToUUID")
    public UUID mapThingToUUID(Product product) {
        return product.getProductId().getId();
    }

    @Named("mapUUIDToThing")
    public Product mapUUIDToThing(UUID thingId) {
        return productService.findById(thingId);
    }
}

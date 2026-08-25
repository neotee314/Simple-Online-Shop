package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.mapper;

import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketPartDto;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketPart;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketPartId;
import com.neotee.ecommercesystem.shopsystem.product.application.service.ProductService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ShoppingBasketPartMapper {

    @Autowired
    private ProductService productService;

    @Mapping(target = "thingId", source = "thing", qualifiedByName = "mapThingToUUID")
    @Mapping(target = "quantity", source = "quantity")
    public abstract ShoppingBasketPartDto toDto(ShoppingBasketPart shoppingBasketPart);


    @Mapping(target = "thing", source = "thingId", qualifiedByName = "mapUUIDToThing")
    @Mapping(target = "quantity", source = "quantity")
    public abstract ShoppingBasketPart toEntity(ShoppingBasketPartDto shoppingBasketPartDto);

    @Named("mapUUIDToShoppingBasketPartId")
    public ShoppingBasketPartId mapUUIDToShoppingBasketPartId(UUID id) {
        return new ShoppingBasketPartId(id);
    }

    @Named("mapShoppingBasketPartIdToUUID")
    public UUID mapShoppingBasketPartIdToUUID(ShoppingBasketPartId shoppingBasketPartId) {
        return shoppingBasketPartId.getId();
    }

    @Named("mapThingToUUID")
    public UUID mapThingToUUID(Product product) {
        return product.getProductId().getId();
    }
    @Named("mapUUIDToThing")
    public Product mapUUIDToThing(UUID thingId) {
        Product product = productService.findById(thingId);
        if (product == null) return null;
        return product;
    }

}

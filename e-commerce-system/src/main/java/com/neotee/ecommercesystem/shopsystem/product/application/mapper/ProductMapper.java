package com.neotee.ecommercesystem.shopsystem.product.application.mapper;

import com.neotee.ecommercesystem.shopsystem.product.application.dto.ProductResponseDto;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.product.domain.ProductId;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(source = "id", target = "thingId", qualifiedByName = "uuidToThingId")
    @Mapping(source = "purchasePrice", target = "purchasePrice", qualifiedByName = "floatToMoney")
    @Mapping(source = "salePrice", target = "salesPrice", qualifiedByName = "floatToMoney")
    Product toEntity(ProductResponseDto dto);

    @Mapping(source = "thingId", target = "id", qualifiedByName = "thingIdToUuid")
    @Mapping(source = "purchasePrice", target = "purchasePrice", qualifiedByName = "moneyToFloat")
    @Mapping(source = "salesPrice", target = "salePrice", qualifiedByName = "moneyToFloat")
    ProductResponseDto toDTO(Product entity);


    @Named("uuidToThingId")
    static ProductId uuidToThingId(UUID id) {
        return id == null ? null : new ProductId(id);
    }

    @Named("thingIdToUuid")
    static UUID thingIdToUuid(ProductId productId) {
        return productId == null ? null : productId.getId();
    }

    @Named("floatToMoney")
    static Money floatToMoney(Float value) {
        if (value == null) return null;
        MoneyType moneyType =  Money.of(value,"EUR");
        return (Money) moneyType;
    }

    @Named("moneyToFloat")
    static Float moneyToFloat(Money money) {
        return money == null ? null : money.getAmount();
    }
}

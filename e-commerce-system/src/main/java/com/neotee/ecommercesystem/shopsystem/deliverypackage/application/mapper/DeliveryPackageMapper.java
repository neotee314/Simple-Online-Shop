package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.mapper;

import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto.DeliveryPackageDTO;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackageId;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {DeliveryPackagePartMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DeliveryPackageMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "mapDeliveryPackageIdToUUID")
    @Mapping(target = "storageUnitId", source = "storageUnit.storageId.id")
    @Mapping(target = "orderId", source = "order.orderId.id")
    public abstract DeliveryPackageDTO toDto(DeliveryPackage entity);

    // Wenn du von DTO zurück zu Entity mappen willst
    @Mapping(target = "id", source = "id", qualifiedByName = "mapUUIDToDeliveryPackageId")
    @Mapping(target = "storageUnit", source = "storageUnitId", qualifiedByName = "mapUUIDToStorageUnit")
    @Mapping(target = "order", source = "orderId", qualifiedByName = "mapUUIDToOrder")
    public abstract DeliveryPackage toEntity(DeliveryPackageDTO dto);

    @Named("mapUUIDToDeliveryPackageId")
    public DeliveryPackageId mapUUIDToDeliveryPackageId(UUID id) {
        return new DeliveryPackageId(id);
    }

    @Named("mapDeliveryPackageIdToUUID")
    public UUID mapDeliveryPackageIdToUUID(DeliveryPackageId id) {
        return id.getId();
    }

    @Named("mapUUIDToStorageUnit")
    public StorageUnit mapUUIDToStorageUnit(UUID id) {
        StorageUnit su = new StorageUnit();
        su.setStorageId(new com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitId(id));
        return su;
    }

    @Named("mapUUIDToOrder")
    public Order mapUUIDToOrder(UUID id) {
        Order order = new Order();
        order.setOrderId(new com.neotee.ecommercesystem.shopsystem.order.domain.OrderId(id));
        return order;
    }
}

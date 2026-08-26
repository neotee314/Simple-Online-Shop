package com.neotee.ecommercesystem.shopsystem.storageunit.domain.event;

import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;

public record StockChangedEvent(
    StorageUnitId storageUnitId,
    ProductId productId,
    int newQuantity,
    int oldQuantity
) {}
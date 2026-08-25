package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out;

import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;

public interface FindOrderPort {
    Order findById(OrderId orderId);
}
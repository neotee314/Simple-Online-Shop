package com.neotee.ecommercesystem.shopsystem.order.domain.event;

import com.neotee.ecommercesystem.domainprimitives.OrderId;

public record OrderCreatedEvent(OrderId orderId) {}
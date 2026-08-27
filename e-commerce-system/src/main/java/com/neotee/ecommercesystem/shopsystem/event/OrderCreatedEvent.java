package com.neotee.ecommercesystem.shopsystem.event;

import com.neotee.ecommercesystem.domainprimitives.OrderId;

public record OrderCreatedEvent(OrderId orderId) {}
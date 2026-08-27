package com.neotee.ecommercesystem.event;

import com.neotee.ecommercesystem.domainprimitives.OrderId;

public record OrderCreatedEvent(OrderId orderId) {}
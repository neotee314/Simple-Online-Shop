package com.neotee.ecommercesystem.events;

import com.neotee.ecommercesystem.domainprimitives.OrderId;

public record OrderCreatedEvent(OrderId orderId) {}
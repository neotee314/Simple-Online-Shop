package com.neotee.ecommercesystem.shopsystem.delivery.application.service;


import com.neotee.ecommercesystem.shopsystem.delivery.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryContentRepository deliveryContentRepository;

}

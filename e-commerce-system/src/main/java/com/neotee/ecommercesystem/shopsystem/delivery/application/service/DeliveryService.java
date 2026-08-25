package com.neotee.ecommercesystem.shopsystem.delivery.application.service;


import com.neotee.ecommercesystem.shopsystem.delivery.domain.*;
import com.neotee.ecommercesystem.usecases.ClientType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryContentRepository deliveryContentRepository;

}

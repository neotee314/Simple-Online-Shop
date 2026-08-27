package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.config;

import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.service.DeliveryPackageDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeliveryPackageConfiguration {

    @Bean
    public DeliveryPackageDomainService deliveryPackageDomainService() {
        return new DeliveryPackageDomainService();
    }
}
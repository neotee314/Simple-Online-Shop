package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.mapper;

import com.neotee.ecommercesystem.domainprimitives.ClientId;
import com.neotee.ecommercesystem.domainprimitives.ShoppingBasketId;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketPartRequestDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketPartResponseDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketResponseDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasket;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketPart;
import com.neotee.ecommercesystem.shopsystem.product.application.service.ProductApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShoppingBasketMapper {

    private final ProductApplicationService productService;

    public ShoppingBasketResponseDTO toResponseDTO(ShoppingBasket basket) {
        if (basket == null) return null;

        var partDTOs = basket.getParts().stream()
                .map(this::toPartResponseDTO)
                .collect(Collectors.toList());

        return new ShoppingBasketResponseDTO(
                basket.getId(),
                basket.getClient() != null ? basket.getClient().getId() : null,
                basket.getClient() != null ? basket.getClient().getEmail().getEmailAddress() : null,
                partDTOs,
                basket.getTotalPrice() != null ? basket.getTotalPrice().getAmount().doubleValue() : 0.0,
                basket.getBasketState() != null ? basket.getBasketState().name() : "EMPTY",
                basket.getTotalQuantity()
        );
    }

    public ShoppingBasketPartResponseDTO toPartResponseDTO(ShoppingBasketPart part) {
        if (part == null) return null;

        return new ShoppingBasketPartResponseDTO(
                part.getProduct().getId(),
                part.getProduct().getName(),
                part.getQuantity(),
                part.getSalesPrice() != null ? part.getSalesPrice().getAmount().doubleValue() : 0.0,
                part.getSubtotal() != null ? part.getSubtotal().getAmount().doubleValue() : 0.0
        );
    }

    public ShoppingBasketPart toPartEntity(ShoppingBasketPartRequestDTO dto) {
        if (dto == null) return null;

        var product = productService.findById(dto.productId());
        return ShoppingBasketPart.create(product, dto.quantity());
    }
}
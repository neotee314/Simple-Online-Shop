package com.neotee.ecommercesystem.shopsystem.product.application.mapper;

import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.shopsystem.product.application.dto.ProductRequestDTO;
import com.neotee.ecommercesystem.shopsystem.product.application.dto.ProductResponseDTO;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Money purchasePrice = (Money) Money.of(dto.purchasePrice(), "EUR");
        Money salesPrice = (Money) Money.of(dto.salePrice(), "EUR");

        if (dto.stockQuantity() != null) {
            return Product.create(
                dto.name(),
                dto.description(),
                dto.size(),
                purchasePrice,
                salesPrice,
                dto.stockQuantity()
            );
        }

        return Product.create(
            dto.name(),
            dto.description(),
            dto.size(),
            purchasePrice,
            salesPrice
        );
    }

    public ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getSize(),
            product.getPurchasePrice() != null ? product.getPurchasePrice().getAmount() : null,
            product.getSalesPrice() != null ? product.getSalesPrice().getAmount() : null,
            product.getStockQuantity()
        );
    }
}
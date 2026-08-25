package com.neotee.ecommercesystem.shopsystem.product.application.service;

import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.exception.EntityIdNullException;
import com.neotee.ecommercesystem.exception.ValueObjectNullOrEmptyException;
import com.neotee.ecommercesystem.shopsystem.product.application.dto.ProductRequestDto;
import com.neotee.ecommercesystem.shopsystem.product.application.dto.ProductResponseDto;
import com.neotee.ecommercesystem.shopsystem.product.application.dto.SalesPriceDto;
import com.neotee.ecommercesystem.shopsystem.product.application.mapper.ProductMapper;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.product.domain.ProductId;
import com.neotee.ecommercesystem.shopsystem.product.domain.ProductRepository;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductApplicationService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductCatalogService productCatalogService;

    public List<ProductResponseDto> searchThingsByName(String name) {
        if (name == null || name.isEmpty()) throw new ValueObjectNullOrEmptyException();
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        List<Product> products = productRepository.findByName(name);
        for (Product product : products) {
            ProductResponseDto productResponseDto = new ProductResponseDto();
            productResponseDto = productMapper.toDTO(product);
            productResponseDtos.add(productResponseDto);
        }
        return productResponseDtos;
    }

    public ProductResponseDto getThingById(UUID thingId) {
        if (thingId == null) throw new EntityIdNullException();
        Product product = productRepository.findByThingId(new ProductId(thingId));
        return productMapper.toDTO(product);

    }

    public void changeSalesPrice(UUID thingId, SalesPriceDto salesPriceDto) {
        if (thingId == null) throw new EntityIdNullException();
        if (salesPriceDto == null) throw new ValueObjectNullOrEmptyException();
        Product product = productRepository.findByThingId(new ProductId(thingId));
        MoneyType money  = Money.of(salesPriceDto.getSalesPrice(),salesPriceDto.getCurrency());
        product.setSalesPrice((Money) money);
        productRepository.save(product);
    }


    public void removeThingFromCatalog(UUID thingId) {
        productCatalogService.removeThingFromCatalog(thingId);
    }

    public MoneyType getSalesPrice(UUID thingId) {
        return productCatalogService.getSalesPrice(thingId);

    }

    public void deleteThingCatalog() {
        productCatalogService.deleteThingCatalog();
    }

    public void addThingToCatalog(ProductRequestDto dto) {
        if (dto == null) throw new ValueObjectNullOrEmptyException();
        MoneyType salesPrice = Money.of(dto.getSalePrice(),"EUR");
        MoneyType purchasePrice = Money.of(dto.getPurchasePrice(),"EUR");
        UUID thingId = UUID.randomUUID();
        productCatalogService.addThingToCatalog(thingId,dto.getName(),dto.getDescription(),dto.getSize(),
                salesPrice,purchasePrice);

    }

    public List<ProductResponseDto> getAllThings() {
        List<Product> products = productRepository.findAll();
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for (Product product : products) {
            productResponseDtos.add(productMapper.toDTO(product));
        }
        return productResponseDtos;
    }
}

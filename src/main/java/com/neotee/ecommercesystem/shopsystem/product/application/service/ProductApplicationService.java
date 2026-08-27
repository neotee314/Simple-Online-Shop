package com.neotee.ecommercesystem.shopsystem.product.application.service;

import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.exceptions.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.product.application.dto.SalesPriceDTO;
import com.neotee.ecommercesystem.shopsystem.product.application.port.out.ProductAvailabilityPort;
import com.neotee.ecommercesystem.shopsystem.product.application.port.out.ProductOrderHistoryPort;
import com.neotee.ecommercesystem.shopsystem.product.application.port.out.ProductReservationPort;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

    private final ProductRepository productRepository;
    private final ProductReservationPort productReservationPort;
    private final ProductAvailabilityPort productAvailabilityPort;
    private final ProductOrderHistoryPort productOrderHistoryPort;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByName(name);
    }

    public Product getProductById(ProductId productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("ProductApplicationService", "Produkt nicht gefunden."));
    }

    public Product findById(ProductId productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("ProductApplicationService", "Produkt nicht gefunden."));
    }


    public Product addProduct(Product product) {
        return productRepository.save(product);
    }


    public void changeSalesPrice(ProductId productId, SalesPriceDTO salesPriceDTO) {
        if (productId == null)
            throw new DomainValidationException("ProductApplicationService", "Product ID darf nicht null sein.");

        if (salesPriceDTO == null)
            throw new DomainValidationException("ProductApplicationService", "Sales Price DTO darf nicht null sein.");


        var product = findById(productId);
        var newSalesPrice = (Money) Money.of(salesPriceDTO.salesPrice(), salesPriceDTO.currency());

        product.updatePrice(product.getPurchasePrice(), newSalesPrice);
        productRepository.save(product);
    }


    public void removeProduct(ProductId productId) {
        var product = findById(productId);
        if (productAvailabilityPort.isInStock(product))
            throw new DomainValidationException("ProductApplicationService", "Produkt hat noch Lagerbestand.");

        if (productReservationPort.isReservedInAnyBasket(product))
            throw new DomainValidationException("ProductApplicationService", "Produkt ist noch in einem Warenkorb reserviert.");

        if (productOrderHistoryPort.isPartOfCompletedOrder(product))
            throw new DomainValidationException("ProductApplicationService", "Produkt ist Teil einer abgeschlossenen Bestellung.");


        productRepository.deleteById(productId);
    }

    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    public void updateStock(ProductId productId, int quantity) {
        if (productId == null)
            throw new DomainValidationException("ProductApplicationService", "Product ID darf nicht null sein.");

        var product = findById(productId);
        product.increaseStock(quantity);
        productRepository.save(product);
    }

}
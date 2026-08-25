package com.neotee.ecommercesystem.shopsystem.product.application.api;

import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.shopsystem.product.application.dto.ProductRequestDTO;
import com.neotee.ecommercesystem.shopsystem.product.application.dto.ProductResponseDTO;
import com.neotee.ecommercesystem.shopsystem.product.application.dto.SalesPriceDTO;
import com.neotee.ecommercesystem.shopsystem.product.application.mapper.ProductMapper;
import com.neotee.ecommercesystem.shopsystem.product.application.service.ProductApplicationService;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Catalog", description = "Verwaltung von Produkten")
public class ProductController {

    private final ProductApplicationService productService;
    private final ProductMapper productMapper;

    @Operation(summary = "Get all Products")
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        var products = productService.getAllProducts();
        var response = products.stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search products by name")
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProductsByName(@RequestParam String name) {
        var products = productService.searchProductsByName(name);
        var response = products.stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable ProductId id) {
        var product = productService.getProductById(id);
        return ResponseEntity.ok(productMapper.toResponseDTO(product));
    }

    @Operation(summary = "Add product to catalog")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> addProduct(@Valid @RequestBody ProductRequestDTO request) {
        var product = productMapper.toEntity(request);
        var savedProduct = productService.addProduct(product);
        return new ResponseEntity<>(productMapper.toResponseDTO(savedProduct), HttpStatus.CREATED);
    }

    @Operation(summary = "Change sales price of a product")
    @PatchMapping("/{id}/price")
    public ResponseEntity<Void> changeSalesPrice(
            @PathVariable ProductId id,
            @Valid @RequestBody SalesPriceDTO salesPriceDTO) {
        productService.changeSalesPrice(id, salesPriceDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update stock quantity")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable ProductId id,
            @RequestParam int quantity) {
        productService.updateStock(id, quantity);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove product from catalog")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeProduct(@PathVariable ProductId id) {
        productService.removeProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get sales price of a product")
    @GetMapping("/{id}/salesPrice")
    public ResponseEntity<MoneyType> getSalesPrice(@PathVariable ProductId id) {
        var product = productService.getProductById(id);
        var salesPrice = (Money) Money.of(product.getSalesPrice().getAmount(), product.getSalesPrice().getCurrency());
        return ResponseEntity.ok(salesPrice);
    }

    @Operation(summary = "Delete the entire catalog")
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllProducts() {
        productService.deleteAllProducts();
        return ResponseEntity.noContent().build();
    }
}
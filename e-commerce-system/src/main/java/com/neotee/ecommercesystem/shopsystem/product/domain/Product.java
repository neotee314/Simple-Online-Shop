package com.neotee.ecommercesystem.shopsystem.product.domain;

import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.exception.ValueObjectNullOrEmptyException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    private ProductId productId;
    private String name;
    private String description;
    private Float size;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "purchase_price")),
            @AttributeOverride(name = "currency", column = @Column(name = "purchase_currency"))
    })
    private Money purchasePrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "sales_price")),
            @AttributeOverride(name = "currency", column = @Column(name = "sales_currency"))
    })
    private Money salesPrice;

    public Product(UUID thingId, String name, String description, Float size,
                   Money purchasePrice, Money salesPrice) {
        if (thingId == null || name == null || name.isBlank() || description == null || description.isBlank() ||
                (size != null && size <= 0) ||
                purchasePrice == null || salesPrice == null ||
                purchasePrice.getAmount() <= 0 || salesPrice.getAmount() <= 0 ||
                purchasePrice.largerThan(salesPrice)
        )
            throw new ValueObjectNullOrEmptyException();


        this.productId = new ProductId(thingId);
        this.name = name;
        this.description = description;
        this.size = size;
        this.purchasePrice = purchasePrice;
        this.salesPrice = salesPrice;
    }

    public Money getSellingPrice() {
        return (Money) Money.of(salesPrice.getAmount(), "EUR");
    }


    public UUID getUUID() {
        return productId.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return productId != null ? productId.hashCode() : 0;
    }

}

package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.client.application.service.ClientApplicationService;
import com.neotee.ecommercesystem.shopsystem.product.application.service.ProductApplicationService;
import com.neotee.ecommercesystem.usecases.ShoppingBasketUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyFantasticShoppingBasketUseCaseService implements ShoppingBasketUseCases {

    private final ShoppingBasketApplicationService basketService;
    private final ClientApplicationService clientApplicationService;
    private final ProductApplicationService productApplicationService;

    @Override
    @Transactional
    public void addProductToShoppingBasket(EmailType clientEmail, UUID productId, int quantity) {
        if (clientEmail == null)
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Client Email darf nicht null sein.");
        if (productId == null)
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Product ID darf nicht null sein.");
        if (quantity <= 0)
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Quantity muss größer als 0 sein.");

        var client = clientApplicationService.findByEmail((Email) clientEmail);
        var product = productApplicationService.findById(ProductId.of(productId));
        basketService.addItem(client, product, quantity);
    }

    @Override
    @Transactional
    public void removeProductFromShoppingBasket(EmailType clientEmail, UUID productId, int quantity) {
        if (clientEmail == null)
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Client Email darf nicht null sein.");
        if (productId == null)
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Product ID darf nicht null sein.");
        if (quantity <= 0)
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Quantity muss größer als 0 sein.");

        var client = clientApplicationService.findByEmail((Email) clientEmail);
        var product = productApplicationService.findById(ProductId.of(productId));
        basketService.removeItem(client, product, quantity);
    }

    @Override
    public Map<UUID, Integer> getShoppingBasketAsMap(EmailType clientEmail) {
        if (clientEmail == null)
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Client Email darf nicht null sein.");

        var client = clientApplicationService.findByEmail((Email) clientEmail);
        var basketMap = basketService.getBasket(client);

        return basketMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getId().getId(),
                        Map.Entry::getValue
                ));
    }

    @Override
    public MoneyType getShoppingBasketAsMoneyValue(EmailType clientEmail) {
        if (clientEmail == null)
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Client Email darf nicht null sein.");

        var client = clientApplicationService.findByEmail((Email) clientEmail);
        var basket = basketService.getBasketByClient(client);

        return basket.getTotalPrice();
    }

    @Override
    public int getReservedStockInShoppingBaskets(UUID productId) {
        if (productId == null)
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Thing ID darf nicht null sein.");

        return basketService.getReservedQuantityForProduct(ProductId.of(productId));
    }

    @Override
    public boolean isEmpty(EmailType clientEmail) {
        if (clientEmail == null)
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Client Email darf nicht null sein.");

        var client = clientApplicationService.findByEmail((Email) clientEmail);
        var basket = basketService.getBasketByClient(client);

        return basket.getParts().isEmpty();
    }

    @Override
    @Transactional
    public UUID checkout(EmailType clientEmail) {
        if (clientEmail == null)
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Client Email darf nicht null sein.");

        var client = clientApplicationService.findByEmail((Email) clientEmail);
        var basket = basketService.getBasketByClient(client);

        if (basket.isEmpty())
            throw new DomainValidationException("MyFantasticShoppingBasketUseCaseService", "Warenkorb ist leer.");

        var orderId = basketService.checkout(basket.getId());
        return orderId.getId();
    }

    @Override
    @Transactional
    public void emptyAllShoppingBaskets() {
        basketService.deleteAllBaskets();
    }
}
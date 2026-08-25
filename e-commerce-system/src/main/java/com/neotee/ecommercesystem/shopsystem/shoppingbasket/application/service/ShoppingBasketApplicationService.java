package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.service;

import com.neotee.ecommercesystem.domainprimitives.ClientId;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.ShoppingBasketId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.exceptions.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out.CreateOrderPort;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasket;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketPart;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketRepository;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out.FindProductForShoppingBasketPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingBasketApplicationService {

    private final ShoppingBasketRepository basketRepository;
    private final CreateOrderPort createOrderPort;
    private final FindProductForShoppingBasketPort findProductPort;

    public ShoppingBasket getBasketByClient(Client client) {
        return findOrCreateBasket(client);
    }

    public ShoppingBasket getBasketByClientId(ClientId clientId) {
        return basketRepository.findByClientId(clientId).orElseThrow(() -> new EntityNotFoundException("ShoppingBasketApplicationService",
                "Basket for this client does not exist"));
    }

    public ShoppingBasket addItem(Client client, Product product, Integer quantity) {
        var basket = findOrCreateBasket(client);
        basket.addItem(product, quantity);
        return basketRepository.save(basket);
    }

    public ShoppingBasket addItem(ShoppingBasketId basketId, ProductId productId, Integer quantity) {
        var basket = findBasketById(basketId);
        var product = findProductPort.findById(productId);
        basket.addItem(product, quantity);
        return basketRepository.save(basket);
    }


    public ShoppingBasket removeItem(Client client, Product product, Integer quantity) {
        var basket = basketRepository.findByClient(client).orElseThrow(() -> new EntityNotFoundException("ShoppingBasketApplicationService",
                "Basket for this client does not exist"));
        basket.removeItem(product, quantity);
        return basketRepository.save(basket);
    }

    public ShoppingBasket removeItem(ShoppingBasketId basketId, ProductId productId) {
        var basket = findBasketById(basketId);
        var product = findProductPort.findById(productId);
        basket.removeItem(product);
        return basketRepository.save(basket);
    }

    public ShoppingBasket removeItemWithQuantity(ShoppingBasketId basketId, ProductId productId, int quantity) {
        var basket = findBasketById(basketId);
        var product = findProductPort.findById(productId);
        basket.removeItem(product,quantity);
        return basketRepository.save(basket);
    }

    public void clearBasket(ShoppingBasketId basketId) {
        var basket = findBasketById(basketId);
        basketRepository.delete(basket);

    }

    public OrderId checkout(ShoppingBasketId basketId) {
        var basket = findBasketById(basketId);

        if (!basket.hasItems()) {
            throw new DomainValidationException("ShoppingBasketApplicationService", "Warenkorb ist leer.");
        }

        var client = basket.getClient();
        var orderId = OrderId.newId();

        for (var part : basket.getParts()) {
            createOrderPort.createOrder(client, part.getProduct(), part.getQuantity());
        }

        basket.clear();
        basketRepository.save(basket);

        return orderId;
    }


    public void deleteAllBaskets() {
        basketRepository.deleteAll();
    }

    private ShoppingBasket findBasketById(ShoppingBasketId basketId) {
        return basketRepository.findById(basketId)
                .orElseThrow(() -> new DomainValidationException("ShoppingBasketApplicationService", "Warenkorb nicht gefunden."));
    }

    private ShoppingBasket findOrCreateBasket(Client client) {
        return basketRepository.findByClient(client)
                .orElseGet(() -> {
                    var newBasket = ShoppingBasket.create(client);
                    return basketRepository.save(newBasket);
                });
    }

    public Map<Product, Integer> getBasket(Client client) {
        if (client == null)
            throw new DomainValidationException("ShoppingBasketApplicationService", "Client darf nicht null sein.");

        var basket = findOrCreateBasket(client);
        return basket.getParts().stream()
                .collect(Collectors.toMap(
                        ShoppingBasketPart::getProduct,
                        ShoppingBasketPart::getQuantity
                ));
    }

    public Integer getReservedQuantityForProduct(ProductId productId) {
        if (productId == null)
            throw new DomainValidationException("ShoppingBasketApplicationService", "Product ID darf nicht null sein.");

        return basketRepository.findAll().stream()
                .mapToInt(basket -> basket.getReservedQuantityForProduct(productId))
                .sum();
    }


    public ShoppingBasket getBasketById(ShoppingBasketId shoppingBasketId) {
        return basketRepository.findById(shoppingBasketId).orElseThrow(() -> new EntityNotFoundException("ShoppingBasketApplicationService",
                "Basket for this client does not exist"));
    }



}
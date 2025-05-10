package com.neotee.ecommercesystem.solution.shoppingbasket.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.solution.deliverypackage.application.DeliveryPackageService;
import com.neotee.ecommercesystem.solution.order.application.OrderService;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketRepository;
import com.neotee.ecommercesystem.solution.thing.application.ThingService;
import com.neotee.ecommercesystem.usecases.ShoppingBasketUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingBasketUseCasesService implements ShoppingBasketUseCases {

    private final ShoppingBasketRepository shoppingBasketRepository;
    private final ThingService thingService;
    private final OrderService orderService;
    private final DeliveryPackageService deliveryPackageService;

    @Override
    @Transactional
    public void addThingToShoppingBasket(EmailType clientEmail, UUID thingId, int quantity) {
        // Find the shopping basket for the client
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail(clientEmail)
                .orElse(new ShoppingBasket((Email) clientEmail));
        Money price = thingService.getSalesPrice(thingId);
        shoppingBasket.addItem(thingId, quantity, price);
        shoppingBasketRepository.save(shoppingBasket);
    }

    @Override
    @Transactional
    public void removeThingFromShoppingBasket(EmailType clientEmail, UUID thingId, int quantity) {
        // Find the shopping basket for the client
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail(clientEmail)
                .orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));
        if (!thingService.existsById(thingId)) throw new ShopException("Thing does not exist");
        if (!shoppingBasket.contains(thingId)) throw new ShopException("Thing is not in the shopping basket");
        // Remove item from the shopping basket
        shoppingBasket.removeItem(thingId, quantity);
        shoppingBasketRepository.save(shoppingBasket);
    }

    @Override
    public Map<UUID, Integer> getShoppingBasketAsMap(EmailType clientEmail) {
        // Find the shopping basket for the client
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail(clientEmail)
                .orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));
        // Convert shopping basket parts to map
        return shoppingBasket.getAsMap();
    }

    @Override
    public MoneyType getShoppingBasketAsMoneyValue(EmailType clientEmail) {
        // Find the shopping basket for the client
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail(clientEmail)
                .orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));
        // Calculate total price for the items in the shopping basket
        return shoppingBasket.getAsMoneyValue();
    }

    @Override
    public int getReservedStockInShoppingBaskets(UUID thingId) {
        List<ShoppingBasket> allBaskets = shoppingBasketRepository.findAll();
        return allBaskets.stream()
                .mapToInt(basket -> basket.getReservedQuantityForThing(thingId))
                .sum();
    }


    @Override
    public boolean isEmpty(EmailType clientEmail) {
        // Find the shopping basket for the client
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail(clientEmail)
                .orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));
        return shoppingBasket.isEmpty();

    }

    @Override
    @Transactional
    public UUID checkout(EmailType clientEmail) {
        // Find the shopping basket for the client or create a new
        ShoppingBasket basket = shoppingBasketRepository.findByClientEmail(clientEmail)
                .orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));
        if (basket.isEmpty()) {
            throw new ShopException("Shopping basket is empty");
        }
        // Create a new order
        Map<UUID, Integer> shoppingPartMap = basket.getPartsAsMapValue();
        UUID orderId = orderService.createOrder(shoppingPartMap, (Email) clientEmail);

        //create Delievery packages
        deliveryPackageService.createDeliveryPackage(orderId);

        basket.checkout();
        shoppingBasketRepository.save(basket);

        return orderId;
    }

    @Override
    public void emptyAllShoppingBaskets() {
        shoppingBasketRepository.deleteAll();
    }
}
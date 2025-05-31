package com.neotee.ecommercesystem.solution.shoppingbasket.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.exception.*;
import com.neotee.ecommercesystem.solution.deliverypackage.application.service.DeliveryPackageService;
import com.neotee.ecommercesystem.solution.order.application.service.OrderService;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketRepository;
import com.neotee.ecommercesystem.solution.storageunit.application.service.InventoryFulfillmentService;
import com.neotee.ecommercesystem.solution.thing.application.service.ThingService;
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
    private final ClientBasketServiceInterface clientBasketServiceInterface;
    private final InventoryFulfillmentService inventoryFulfillmentService;
    private final ReservationService reservationService;

    @Override
    @Transactional
    public void addThingToShoppingBasket(EmailType clientEmail, UUID thingId, int quantity) {
        // Find the shopping basket for the client
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail(clientEmail)
                .orElseThrow(EntityNotFoundException::new);
        int currentInventory = inventoryFulfillmentService.getAvailableInventory(thingId);
        int currentReserved = reservationService.getTotalReservedInAllBaskets(thingId);
        if (currentInventory < quantity + currentReserved)
            throw new ThingQuantityNotAvailableException();
        Money price = thingService.getSalesPrice(thingId);
        shoppingBasket.addItem(thingId, quantity, price);
        shoppingBasketRepository.save(shoppingBasket);
    }

    @Override
    @Transactional
    public void removeThingFromShoppingBasket(EmailType clientEmail, UUID thingId, int quantity) {
        // Find the shopping basket for the client
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail(clientEmail)
                .orElseThrow(EntityNotFoundException::new);
        if (!thingService.existsById(thingId)) throw new EntityNotFoundException();
        if (!shoppingBasket.contains(thingId)) throw new ThingNotInShoppingBasketException();
        // Remove item from the shopping basket
        shoppingBasket.removeItem(thingId, quantity);
        shoppingBasketRepository.save(shoppingBasket);
    }

    @Override
    public Map<UUID, Integer> getShoppingBasketAsMap(EmailType clientEmail) {
        // Find the shopping basket for the client
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail(clientEmail)
                .orElseThrow(EntityNotFoundException::new);
        // Convert shopping basket parts to map
        return shoppingBasket.getAsMap();
    }

    @Override
    public MoneyType getShoppingBasketAsMoneyValue(EmailType clientEmail) {
        // Find the shopping basket for the client
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail(clientEmail)
                .orElseThrow(EntityNotFoundException::new);
        // Calculate total price for the items in the shopping basket
        return shoppingBasket.getAsMoneyValue();
    }

    @Override
    @Transactional
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
                .orElseThrow(EntityNotFoundException::new);
        return shoppingBasket.isEmpty();

    }

    @Override
    @Transactional
    public UUID checkout(EmailType clientEmail) {
        // Find the shopping basket for the client or create a new
        ShoppingBasket basket = shoppingBasketRepository.findByClientEmail(clientEmail)
                .orElseThrow(EntityNotFoundException::new);
        if (basket.isEmpty()) throw new ShoppingBasketEmptyException();

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
        clientBasketServiceInterface.emptyAllBasket();
        shoppingBasketRepository.deleteAll();
    }
}
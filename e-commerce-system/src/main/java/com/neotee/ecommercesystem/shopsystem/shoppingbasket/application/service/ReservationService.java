package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.service;

import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.exception.ThingNotInShoppingBasketException;
import com.neotee.ecommercesystem.exception.ThingQuantityNotAvailableException;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasket;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketId;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketPartRepository;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketRepository;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.service.ReservedQuantityService;
import com.neotee.ecommercesystem.shopsystem.thing.application.service.ReservationCheckServiceInterface;
import com.neotee.ecommercesystem.shopsystem.thing.domain.ThingId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService implements ReservationCheckServiceInterface, ReservedQuantityService {
    private final ShoppingBasketRepository shoppingBasketRepository;
    private final ShoppingBasketPartRepository shoppingBasketPartRepository;

    @Override
    public Integer getTotalReservedInAllBaskets(UUID thingId) {
        // Get all shopping baskets from the repository
        List<ShoppingBasket> allBaskets = shoppingBasketRepository.findAll();

        // Calculate the total reserved quantity for the item across all baskets
        Integer total = allBaskets.stream()
                .mapToInt(basket -> basket.getReservedQuantityForThing(new ThingId(thingId)))
                .sum();  // Sum the total
        return total;
    }

    @Override
    public void removeFromReservedQuantity(UUID thingId, Integer quantityToRemove) {
        List<ShoppingBasketId> basketIds = getAllBasketContaining(thingId);

        if (basketIds.isEmpty()) throw new ThingNotInShoppingBasketException();

        int totalRemoved = 0;

        for (ShoppingBasketId basketId : basketIds) {
            ShoppingBasket basket = shoppingBasketRepository.findById(basketId)
                    .orElseThrow(EntityNotFoundException::new);

            int removed = basket.removeReservedItems(new ThingId(thingId), quantityToRemove - totalRemoved);
            totalRemoved += removed;

            shoppingBasketRepository.save(basket);

            if (totalRemoved >= quantityToRemove) break;
        }

        if (totalRemoved < quantityToRemove) {
            throw new ThingQuantityNotAvailableException();
        }
    }


    private List<ShoppingBasketId> getAllBasketContaining(UUID thingId) {
        // Get all shopping baskets
        List<ShoppingBasket> allBaskets = shoppingBasketRepository.findAll();

        // Filter the baskets that contain the given thingId
        return allBaskets.stream()
                .filter(basket -> basket.contains(thingId))  // Check if any part in the basket has the thingId
                .map(ShoppingBasket::getId)  // Extract the UUID of each basket
                .collect(Collectors.toList()).reversed();  // Collect into a list of UUIDs
    }


    @Override
    public boolean isReservedInAnyBasket(UUID thingId) {
        return getTotalReservedInAllBaskets(thingId) > 0;
    }

    @Override
    public void deleteShoppingBasketParts() {
        shoppingBasketPartRepository.deleteAll();

    }


}

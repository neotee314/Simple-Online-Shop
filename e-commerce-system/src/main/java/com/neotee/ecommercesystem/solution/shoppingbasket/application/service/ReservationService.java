package com.neotee.ecommercesystem.solution.shoppingbasket.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasket;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketRepository;
import com.neotee.ecommercesystem.solution.storageunit.application.service.ReservedQuantityService;
import com.neotee.ecommercesystem.solution.thing.application.service.ReservationCheckServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService implements ReservationCheckServiceInterface, ReservedQuantityService {
    private final ShoppingBasketRepository shoppingBasketRepository;
    @Override
    public int getTotalReservedInAllBaskets(UUID thingId) {
        // Get all shopping baskets from the repository
        List<ShoppingBasket> allBaskets = shoppingBasketRepository.findAll();

        // Calculate the total reserved quantity for the item across all baskets
        Integer total = allBaskets.stream()
                .mapToInt(basket -> basket.getReservedQuantityForThing(thingId))
                .sum();  // Sum the total
        return total;
    }

    @Override
    public void removeFromReservedQuantity(UUID thingId, int quantityToRemove) {
        List<UUID> basketIds = getAllBasketContaining(thingId);

        if (basketIds.isEmpty()) throw new ShopException("No baskets contain the item");

        int totalRemoved = 0;

        for (UUID basketId : basketIds) {
            ShoppingBasket basket = shoppingBasketRepository.findById(basketId)
                    .orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));

            int removed = basket.removeReservedItems(thingId, quantityToRemove - totalRemoved);
            totalRemoved += removed;

            shoppingBasketRepository.save(basket);

            if (totalRemoved >= quantityToRemove) break;
        }

        if (totalRemoved < quantityToRemove) {
            throw new ShopException("Cannot remove more than the reserved quantity");
        }
    }


    private List<UUID> getAllBasketContaining(UUID thingId) {
        // Get all shopping baskets
        List<ShoppingBasket> allBaskets = shoppingBasketRepository.findAll();

        // Filter the baskets that contain the given thingId
        return allBaskets.stream()
                .filter(basket -> basket.contains(thingId))  // Check if any part in the basket has the thingId
                .map(ShoppingBasket::getId)  // Extract the UUID of each basket
                .collect(Collectors.toList());  // Collect into a list of UUIDs
    }


    @Override
    public boolean isReservedInBasket(UUID thingId) {
       return getTotalReservedInAllBaskets(thingId) > 0;
    }


}

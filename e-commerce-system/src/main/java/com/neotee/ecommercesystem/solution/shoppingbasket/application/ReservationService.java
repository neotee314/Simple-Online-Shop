package com.neotee.ecommercesystem.solution.shoppingbasket.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasket;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketRepository;
import com.neotee.ecommercesystem.solution.storageunit.application.ReservationServiceInterface;
import com.neotee.ecommercesystem.solution.thing.application.ReservationCatalogServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService implements ReservationCatalogServiceInterface, ReservationServiceInterface {
    private final ShoppingBasketRepository shoppingBasketRepository;

    public int getTotalReservedInAllBaskets(UUID thingId) {
        // Get all shopping baskets from the repository
        List<ShoppingBasket> allBaskets = shoppingBasketRepository.findAll();

        // Calculate the total reserved quantity for the item across all baskets
        Integer total = allBaskets.stream()
                .mapToInt(basket -> basket.getReservedQuantityForThing(thingId))
                .sum();  // Sum the total
        return total;
    }


    public void removeFromReservedQuantity(UUID thingId, int removedQuantity) {

        // Get all baskets containing the item identified by thingId
        List<UUID> basketsContainingThing = getAllBasketContaining(thingId);

        // If no baskets contain the item, return early
        if (basketsContainingThing.isEmpty()) throw new ShopException("No baskets contain the item");

        Random random = new Random();
        int totalReserved = 0;

        // Calculate the total reserved quantity for the specified thingId in all baskets
        for (UUID basketId : basketsContainingThing) {
            ShoppingBasket basket = shoppingBasketRepository.findById(basketId).orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));
            totalReserved += basket.getReservedQuantityForThing(thingId);  // Assuming this method exists
        }

        // Ensure we cannot remove more than what is reserved
        if (totalReserved < removedQuantity) {
            throw new ShopException("Cannot remove more than the reserved quantity of the item in baskets");
        }

        // Randomly remove the quantity from the baskets
        while (removedQuantity > 0) {
            int randomIndex = random.nextInt(basketsContainingThing.size());
            UUID basketId = basketsContainingThing.get(randomIndex);
            ShoppingBasket basket = shoppingBasketRepository.findById(basketId).orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));

            // Attempt to remove one item from the basket
            boolean isRemoved = basket.removeItem(thingId, 1);

            // Save the basket after modification
            shoppingBasketRepository.save(basket);

            // If successfully removed, decrease the quantity left to remove
            if (isRemoved) removedQuantity -= 1;
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

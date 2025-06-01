package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.exception.*;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketPartDto;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.mapper.ShoppingBasketMapper;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.mapper.ShoppingBasketPartMapper;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasket;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketId;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketPart;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketRepository;
import com.neotee.ecommercesystem.shopsystem.thing.application.service.ThingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingBasketApplicationService {
    private final ShoppingBasketRepository shoppingBasketRepository;
    private final ClientBasketServiceInterface clientBasketServiceInterface;
    private final ShoppingBasketMapper basketMapper;
    private final ShoppingBasketPartMapper partMapper;
    private final ShoppingBasketUseCasesService shoppingBasketUseCasesService;
    private final ThingService thingService;

    public ShoppingBasketDTO getBasketByClientId(UUID clientId) {
        if (clientId == null ) throw new EntityIdNullException();
        Email clientEmail = clientBasketServiceInterface.findClientEmail(clientId);
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail(clientEmail).orElse(null);
        if (shoppingBasket == null) throw new EntityNotFoundException();
        return basketMapper.toDto(shoppingBasket);
    }

    public void addThingToBasket(UUID basketId, ShoppingBasketPartDto request) {
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findById(new ShoppingBasketId(basketId)).orElseThrow(
                EntityNotFoundException::new);
        if (request == null || request.getThingId() == null) throw new EntityIdNullException();
        if (request.getQuantity() < 0) throw new QuantityNegativeException();
        if (!thingService.existsById(request.getThingId())) throw new EntityNotFoundException();
        ShoppingBasketPart part = partMapper.toEntity(request);
        Money price = thingService.getSalesPrice(part.getThingId());
        part.setSalesPrice(price);
        shoppingBasketUseCasesService.addThingToShoppingBasket(shoppingBasket.getClientEmail(), part.getThingId(), part.getQuantity());
    }

    public UUID checkoutBasket(UUID basketId) {
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findById(new ShoppingBasketId(basketId)).orElseThrow(EntityNotFoundException::new);
        if (shoppingBasket.isEmpty()) throw new ShoppingBasketEmptyException();
        return shoppingBasketUseCasesService.checkout(shoppingBasket.getClientEmail());
    }


    public ShoppingBasket findById(UUID basketId) {
        return shoppingBasketRepository.findById(new ShoppingBasketId(basketId))
                .orElse(null);
    }

    public void removeThingFromShoppingBasket(UUID basketId, UUID thingId) {
        if (basketId == null || thingId == null) {
            throw new EntityIdNullException();
        }
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findById(new ShoppingBasketId(basketId)).orElseThrow(EntityNotFoundException::new);
        if (!thingService.existsById(thingId)) throw new EntityNotFoundException();
        if (!shoppingBasket.contains(thingId)) throw new ThingNotInShoppingBasketException();
        shoppingBasket.removeItem(thingId);
        shoppingBasketRepository.save(shoppingBasket);

    }

    public ShoppingBasketDTO getBasketById(UUID shoppingBasketId) {
        if (shoppingBasketId == null) throw new EntityIdNullException();
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findById(new ShoppingBasketId(shoppingBasketId)).orElseThrow(EntityNotFoundException::new);
        return basketMapper.toDto(shoppingBasket);
    }
}

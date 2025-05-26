package com.neotee.ecommercesystem.solution.shoppingbasket.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.solution.client.application.service.ClientService;
import com.neotee.ecommercesystem.solution.client.domain.Client;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.ShoppingBasketDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.ShoppingBasketPartDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.mapper.ShoppingBasketMapper;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.mapper.ShoppingBasketPartMapper;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasket;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketPart;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketRepository;
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

    public ShoppingBasketDto getBasketByClientId(UUID clientId) {
        if (clientId == null) return null;
        Client client = clientBasketServiceInterface.findById(clientId);
        if (client == null) return null;
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail( client.getEmail()).orElse(null);
        return basketMapper.toDto(shoppingBasket);
    }

    public void addThingToBasket(UUID basketId, ShoppingBasketPartDto request) {
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findById(basketId).orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));
        if(request==null || request.getThingId()==null) throw new ShopException("ThingId is null");
        if(request.getQuantity()<0) throw new ShopException("Thing quantity is less than or equal to zero");
        ShoppingBasketPart part = partMapper.toEntity(request);
        shoppingBasketUseCasesService.addThingToShoppingBasket(shoppingBasket.getClientEmail(),part.getThingId(),part.getQuantity());
    }

    public UUID checkoutBasket(UUID basketId) {
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findById(basketId).orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));
        if (shoppingBasket.isEmpty()) throw new ShopException("Shopping basket is empty");
        return shoppingBasketUseCasesService.checkout(shoppingBasket.getClientEmail());
    }


    public ShoppingBasket findById(UUID shoppingBasketId) {
        return shoppingBasketRepository.findById(shoppingBasketId)
                .orElse(null);
    }

    public void removeThingFromShoppingBasket(ShoppingBasket shoppingBasket,UUID thingId) {
        shoppingBasket.removeItem(thingId);
        shoppingBasketRepository.save(shoppingBasket);

    }
}

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
    private final ClientService clientService;
    private final ShoppingBasketMapper basketMapper;
    private final ShoppingBasketPartMapper partMapper;
    private final ShoppingBasketUseCasesService shoppingBasketUseCasesService;

    public ShoppingBasketDto getBasketByClientId(UUID clientId) {
        Client client = clientService.findById(clientId);
        if (client == null) throw new ShopException("Client not found");
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail( client.getEmail()).orElse(null);
        ShoppingBasketDto shoppingBasketDto = basketMapper.toDto(shoppingBasket);
        return shoppingBasketDto;
    }

    public void addThingToBasket(UUID basketId, ShoppingBasketPartDto request) {
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findById(basketId).orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));
        if(request==null || request.getThingId()==null || request.getQuantity()<=0) throw new ShopException("ThingId is null");
        ShoppingBasketPart part = partMapper.toEntity(request);
        shoppingBasketUseCasesService.addThingToShoppingBasket(shoppingBasket.getClientEmail(),part.getThingId(),part.getQuantity());
    }
    public void removeThingFromBasket(UUID basketId, ShoppingBasketPartDto request) {
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findById(basketId).orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));
        if(request==null || request.getThingId()==null || request.getQuantity()<=0) throw new ShopException("ThingId is null");
        ShoppingBasketPart part = partMapper.toEntity(request);
        shoppingBasketUseCasesService.removeThingFromShoppingBasket(shoppingBasket.getClientEmail(),part.getThingId(),part.getQuantity());
    }

    public void checkoutBasket(UUID basketId) {
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findById(basketId).orElseThrow(() -> new ShopException("ShoppingBasket does not exist"));
        if (shoppingBasket.isEmpty()) throw new ShopException("Shopping basket is empty");
        shoppingBasketUseCasesService.checkout(shoppingBasket.getClientEmail());
    }
}

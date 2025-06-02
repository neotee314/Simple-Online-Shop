package com.neotee.ecommercesystem.shopsystem.thing.application.service;

import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.exception.EntityIdNullException;
import com.neotee.ecommercesystem.exception.ValueObjectNullOrEmptyException;
import com.neotee.ecommercesystem.shopsystem.thing.application.dto.SalesPriceDTO;
import com.neotee.ecommercesystem.shopsystem.thing.application.dto.ThingDTO;
import com.neotee.ecommercesystem.shopsystem.thing.application.mapper.ThingMapper;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import com.neotee.ecommercesystem.shopsystem.thing.domain.ThingId;
import com.neotee.ecommercesystem.shopsystem.thing.domain.ThingRepository;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThingApplicationService {
    private final ThingRepository thingRepository;
    private final ThingMapper thingMapper;
    private final ThingCatalogService thingCatalogService;

    public List<ThingDTO> searchThingsByName(String name) {
        if (name == null || name.isEmpty()) throw new ValueObjectNullOrEmptyException();
        List<ThingDTO> thingDTOs = new ArrayList<>();
        List<Thing> things = thingRepository.findByName(name);
        for (Thing thing : things) {
            ThingDTO thingDTO = new ThingDTO();
            thingDTO = thingMapper.toDTO(thing);
            thingDTOs.add(thingDTO);
        }
        return thingDTOs;
    }

    public ThingDTO getThingById(UUID thingId) {
        if (thingId == null) throw new EntityIdNullException();
        Thing thing = thingRepository.findByThingId(new ThingId(thingId));
        return thingMapper.toDTO(thing);

    }

    public void changeSalesPrice(UUID thingId, SalesPriceDTO salesPriceDto) {
        if (thingId == null) throw new EntityIdNullException();
        if (salesPriceDto == null) throw new ValueObjectNullOrEmptyException();
        Thing thing = thingRepository.findByThingId(new ThingId(thingId));
        MoneyType money  = Money.of(salesPriceDto.getSalesPrice(),salesPriceDto.getCurrency());
        thing.setSalesPrice((Money) money);
        thingRepository.save(thing);
    }


    public void removeThingFromCatalog(UUID thingId) {
        thingCatalogService.removeThingFromCatalog(thingId);
    }

    public MoneyType getSalesPrice(UUID thingId) {
        return thingCatalogService.getSalesPrice(thingId);

    }

    public void deleteThingCatalog() {
        thingCatalogService.deleteThingCatalog();
    }

    public void addThingToCatalog(ThingDTO dto) {
        if (dto == null) throw new ValueObjectNullOrEmptyException();
        MoneyType salesPrice = Money.of(dto.getSalePrice(),"EUR");
        MoneyType purchasePrice = Money.of(dto.getPurchasePrice(),"EUR");
        thingCatalogService.addThingToCatalog(dto.getId(),dto.getName(),dto.getDescription(),dto.getSize(),
                salesPrice,purchasePrice);

    }

    public List<ThingDTO> getAllThings() {
        List<Thing> things = thingRepository.findAll();
        List<ThingDTO> thingDTOs = new ArrayList<>();
        for (Thing thing : things) {
            thingDTOs.add(thingMapper.toDTO(thing));
        }
        return thingDTOs;
    }
}

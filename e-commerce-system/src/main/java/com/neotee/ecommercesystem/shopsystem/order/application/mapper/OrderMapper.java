package com.neotee.ecommercesystem.shopsystem.order.application.mapper;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderDTO;
import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderPartDTO;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {OrderPartMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderMapper {

    @Mapping(target = "orderId", source = "orderId", qualifiedByName = "mapOrderIdToUUID")
    @Mapping(target = "clientEmail", source = "clientEmail", qualifiedByName = "mapEmailToString")
    @Mapping(target = "orderParts", source = "orderParts")
    public abstract OrderDTO toDto(Order order);

    @Mapping(target = "orderId", source = "orderId", qualifiedByName = "mapUUIDToOrderId")
    @Mapping(target = "clientEmail", source = "clientEmail", qualifiedByName = "mapStringToEmail")
    @Mapping(target = "orderParts", source = "orderParts")
    public abstract Order toEntity(OrderDTO orderDto);

    @Named("mapUUIDToOrderId")
    public OrderId mapUUIDToOrderId(UUID id) {
        return new OrderId(id);
    }

    @Named("mapOrderIdToUUID")
    public UUID mapOrderIdToUUID(OrderId orderId) {
        return orderId.getId();
    }

    @Named("mapEmailToString")
    public String mapEmailToString(Email email) {
        return email.getEmailAddress();
    }

    @Named("mapStringToEmail")
    public Email mapStringToEmail(String email) {
        return Email.of(email);
    }
    public List<OrderPartDTO> mapToOrderPartDTOs(Map<UUID, Integer> orderPartsMap) {
        return orderPartsMap.entrySet().stream()
                .map(entry -> new OrderPartDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public OrderDTO mapToOrderDTO(String clientEmail, List<OrderPartDTO> orderParts) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(null);
        dto.setClientEmail(clientEmail);
        dto.setOrderParts(orderParts);
        return dto;
    }
}

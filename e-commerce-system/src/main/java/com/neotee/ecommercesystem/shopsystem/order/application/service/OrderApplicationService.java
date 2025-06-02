package com.neotee.ecommercesystem.shopsystem.order.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderDTO;
import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderPartDTO;
import com.neotee.ecommercesystem.shopsystem.order.application.mapper.OrderMapper;
import com.neotee.ecommercesystem.usecases.OrderUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderUseCases orderUseCases;
    private final OrderMapper orderMapper;


    public List<OrderDTO> getOrderHistory(String email) {
        EmailType emailType = Email.of(email);
        Map<UUID, Integer> orderHistory = orderUseCases.getOrderHistory(emailType);

        List<OrderPartDTO> orderParts = orderMapper.mapToOrderPartDTOs(orderHistory);
        OrderDTO orderDTO = orderMapper.mapToOrderDTO(email, orderParts);

        return Collections.singletonList(orderDTO);
    }



    public void deleteAllOrders() {
        orderUseCases.deleteAllOrders();
    }


}

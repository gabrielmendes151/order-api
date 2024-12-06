package com.orderapi.controller;

import com.orderapi.controllers.OrderController;
import com.orderapi.dtos.OrderResponse;
import com.orderapi.enums.OrderStatus;
import com.orderapi.services.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;


    @Test
    void fetchOrders_shouldReturnPagedOrders() {
        var order1 = OrderResponse.builder()
                .externalOrderId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .orderStatus(OrderStatus.RECEIVED)
                .build();

        var order2 = OrderResponse.builder()
                .externalOrderId(UUID.fromString("987e4567-e89b-12d3-a456-426614174999"))
                .orderStatus(OrderStatus.RECEIVED)
                .build();

        Page<OrderResponse> mockPage = new PageImpl<>(List.of(order1, order2), PageRequest.of(0, 10, DESC, "createdAt"), 2);

        when(orderService.fetchOrders(0, 10)).thenReturn(mockPage);

        Page<OrderResponse> response = orderController.fetchOrders(0, 10);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        assertEquals(order1.getExternalOrderId(), response.getContent().get(0).getExternalOrderId());
        verify(orderService, times(1)).fetchOrders(0, 10);
    }
}

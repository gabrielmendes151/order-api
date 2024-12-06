package com.orderapi.service;

import com.orderapi.dtos.OrderRequest;
import com.orderapi.dtos.OrderResponse;
import com.orderapi.enums.OrderStatus;
import com.orderapi.exceptions.DuplicatedOrderException;
import com.orderapi.models.Order;
import com.orderapi.models.Product;
import com.orderapi.repositories.OrderRepository;
import com.orderapi.repositories.ProductRepository;
import com.orderapi.services.OrderService;
import org.springframework.data.domain.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;


    @Test
    void preSave_shouldSaveOrderWhenNotDuplicated() {
        UUID externalOrderId = UUID.randomUUID();
        OrderRequest request = OrderRequest.builder()
                .externalOrderId(externalOrderId)
                .build();

        when(orderRepository.findByExternalOrderId(externalOrderId))
                .thenReturn(Optional.empty());

        Order savedOrder = orderService.preSave(request);

        assertNotNull(savedOrder);
        assertEquals(externalOrderId, savedOrder.getExternalOrderId());
        assertEquals(OrderStatus.RECEIVED, savedOrder.getOrderStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void preSave_shouldThrowExceptionWhenDuplicatedOrder() {
        UUID externalOrderId = UUID.randomUUID();
        OrderRequest request = OrderRequest.builder()
                .externalOrderId(externalOrderId)
                .build();

        when(orderRepository.findByExternalOrderId(externalOrderId))
                .thenReturn(Optional.of(Order.builder().build()));

        assertThrows(DuplicatedOrderException.class, () -> orderService.preSave(request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void processOrder_shouldSaveOrderAndProducts() {
        UUID externalOrderId = UUID.randomUUID();
        Order order = Order.builder()
                .externalOrderId(externalOrderId)
                .orderStatus(OrderStatus.RECEIVED)
                .updatedAt(LocalDateTime.now())
                .build();

        OrderRequest request = OrderRequest.builder()
                .externalOrderId(externalOrderId)
                .products(List.of(
                        OrderRequest.ProductRequest.builder()
                                .description("Product A")
                                .value(BigDecimal.valueOf(100.50))
                                .build()
                ))
                .build();

        orderService.processOrder(order, request);

        assertEquals(1, order.getProducts().size());
        assertEquals(BigDecimal.valueOf(100.50), order.getTotalValue());
        verify(orderRepository, times(1)).save(order);
        verify(productRepository, times(1)).saveAll(anyList());
    }

    @Test
    void fetchOrders_shouldReturnPagedOrders() {
        var order1 = Order.builder()
                .id(1L)
                .externalOrderId(UUID.randomUUID())
                .orderStatus(OrderStatus.RECEIVED)
                .build();
        order1.setProducts(List.of(new Product()));
        var order2 = Order.builder()
                .id(2L)
                .externalOrderId(UUID.randomUUID())
                .orderStatus(OrderStatus.RECEIVED)
                .build();

        order2.setProducts(List.of(new Product()));

        Page<Order> page = new PageImpl<>(List.of(order1, order2));
        when(orderRepository.findAllPageable(any(PageRequest.class)))
                .thenReturn(page);

        Page<OrderResponse> result = orderService.fetchOrders(0, 2);

        assertEquals(2, result.getContent().size());
        verify(orderRepository, times(1)).findAllPageable(any(PageRequest.class));
    }
}
package com.orderapi.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderapi.consumers.ConsumerService;
import com.orderapi.dtos.OrderRequest;
import com.orderapi.models.Order;
import com.orderapi.services.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsumerServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private ConsumerService consumerService;

    @Captor
    private ArgumentCaptor<String> messageCaptor;


    @Test
    void consumeOrder_shouldProcessOrderSuccessfully() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = """
                {
                  "externalOrderId": "123e4567-e89b-12d3-a456-426614174000",
                  "products": [
                    {
                      "description": "Product A",
                      "value": 100.50
                    },
                    {
                      "description": "Product B",
                      "value": 200.75
                    }
                  ]
                }
                """;

        OrderRequest orderRequest = objectMapper.readValue(message, OrderRequest.class);
        Order order = Order.builder()
                .externalOrderId(UUID.fromString(orderRequest.getExternalOrderId().toString()))
                .build();

        Acknowledgment acknowledgment = mock(Acknowledgment.class);

        when(orderService.preSave(any())).thenReturn(order);


        consumerService.consumeOrder(message, acknowledgment);


        verify(orderService, times(1)).preSave(any());
        verify(orderService, times(1)).processOrder(any(),any());
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void consumeOrder_shouldHandleExceptionAndNotAcknowledge() throws Exception {
        String message = """
                {
                  "externalOrderId": "123e4567-e89b-12d3-a456-426614174000",
                  "products": [
                    {
                      "description": "Product A",
                      "value": 100.50
                    }
                  ]
                }
                """;

        Acknowledgment acknowledgment = mock(Acknowledgment.class);

        when(orderService.preSave(any(OrderRequest.class))).thenThrow(new RuntimeException("Test exception"));


        consumerService.consumeOrder(message, acknowledgment);


        verify(orderService, times(1)).preSave(any(OrderRequest.class));
        verify(orderService, never()).processOrder(any(Order.class), any(OrderRequest.class));
        verify(acknowledgment, never()).acknowledge();
    }
}
package com.orderapi.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderapi.dto.OrderRequest;
import com.orderapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final OrderService orderService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "${app.consumer-topic}", groupId = "order-group")
    public void consumeOrder(String message, Acknowledgment acknowledgment) {
        try {
            log.info("Message received: {}", message);
            var mapper = new ObjectMapper();
            OrderRequest orderRequest = mapper.readValue(message, OrderRequest.class);
            var order = orderService.preSave(orderRequest);
            orderService.processOrder(order, orderRequest);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing order", e);
            //Could be send to a dql to avoid infinity reprocess in invalid data for example
        }
    }
}

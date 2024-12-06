package com.orderapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderapi.dto.OrderRequest;
import com.orderapi.dto.OrderResponse;
import com.orderapi.enums.OrderStatus;
import com.orderapi.exceptions.DuplicatedOrderException;
import com.orderapi.models.Order;
import com.orderapi.models.Product;
import com.orderapi.repository.OrderRepository;
import com.orderapi.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order preSave(OrderRequest request) {
        if (orderRepository.findByExternalOrderId(request.getExternalOrderId()).isPresent()) {
            throw new DuplicatedOrderException("Duplicated order ID: " + request.getExternalOrderId());
        }
        var order = Order.builder()
                .externalOrderId(request.getExternalOrderId())
                .orderStatus(OrderStatus.RECEIVED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        try {
            log.debug("Saving pre order: {}", order);
            orderRepository.save(order);

        } catch (Exception e) {
            handleWithProcessingError(order, e);
        }

        return order;
    }

    @Transactional
    public void processOrder(Order order, OrderRequest request) {
        try {
            List<Product> products = request.getProducts().stream()
                    .map(productRequest -> Product.of(order, productRequest))
                    .collect(toList());

            order.setUpdatedAt(LocalDateTime.now());
            order.setProducts(products);
            order.setTotalValue(calculateTotalValue(products));

            orderRepository.save(order);
            productRepository.saveAll(products);
            log.debug("Order and products saved. [Order: {}, Products: {}]", order, products);
        } catch (Exception e) {
            handleWithProcessingError(order, e);
        }
    }

    public Page<OrderResponse> fetchOrders(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable paging = PageRequest.of(page, size, sort);
        return orderRepository.findAllPageable(paging)
                .map(OrderResponse::of);
    }

    private static BigDecimal calculateTotalValue(List<Product> products) {
        return products.stream()
                .map(Product::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void handleWithProcessingError(Order order, Exception e) {
        log.error("Error in save order", e);
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.ERROR);
        orderRepository.save(order);
    }
}


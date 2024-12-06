package com.orderapi.controllers;

import com.orderapi.dtos.OrderResponse;
import com.orderapi.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("orders")
@Tag(name = "Order Controller")
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get orders", description = "Return orders by page and size")
    @GetMapping
    public Page<OrderResponse> fetchOrders(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Fetching orders with pagination page:{} size:{}", page, size);
        final var orders = orderService.fetchOrders(page, size);
        log.info("Returning orders data successfully {}", orders);

        return orders;
    }
}

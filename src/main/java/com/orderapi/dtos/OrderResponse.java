package com.orderapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.orderapi.enums.OrderStatus;
import com.orderapi.models.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private UUID externalOrderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private OrderStatus orderStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    private BigDecimal totalValue;
    private List<ProductResponse> products;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductResponse {

        private String description;

        private BigDecimal value;
    }

    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .externalOrderId(order.getExternalOrderId())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .totalValue(order.getTotalValue())
                .products(order.getProducts().stream()
                        .map(prod -> new ProductResponse(prod.getDescription(), prod.getValue()))
                        .collect(Collectors.toList())
                )
                .build();
    }
}


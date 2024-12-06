package com.orderapi.dto;

import com.orderapi.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private Long externalId;
    private ZonedDateTime createdDate;
    private OrderStatus orderStatus;
    private ZonedDateTime lastModifiedDate;
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
}


package com.orderapi.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotNull
    private UUID externalOrderId;

    @Valid
    @NotEmpty
    private List<ProductRequest> products;


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductRequest {

        @NotNull
        private String description;
        @NotNull
        private BigDecimal value;
    }
}

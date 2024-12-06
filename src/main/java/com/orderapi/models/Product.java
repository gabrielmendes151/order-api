package com.orderapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.orderapi.dto.OrderRequest;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "order_fk", referencedColumnName = "id", foreignKey = @ForeignKey(name = "order_prod_fk"), nullable = false)
    private Order order;

    public static Product of(Order order, OrderRequest.ProductRequest productRequest) {
        return Product.builder()
                .order(order)
                .description(productRequest.getDescription())
                .value(productRequest.getValue())
                .build();
    }
}

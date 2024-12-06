package com.orderapi.repositories;

import com.orderapi.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByExternalOrderId(UUID externalOrderId);

    @Query(value = "select o from Order o join fetch o.products p",
            countQuery = " select count(o) from Order o")
    Page<Order> findAllPageable(Pageable pageable);
}

package com.sbsr.sstashed.repository;

import com.sbsr.sstashed.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Find all items in an order
    List<OrderItem> findByOrderId(Long orderId);

    // Find items by product
    List<OrderItem> findByProductId(Long productId);

    // Count items in an order
    Long countByOrderId(Long orderId);

    // Get total quantity for a product across all orders
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.product.id = :productId")
    Integer getTotalQuantitySoldForProduct(@Param("productId") Long productId);
}
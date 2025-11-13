package com.sbsr.sstashed.repository;

import com.sbsr.sstashed.model.Order;
import com.sbsr.sstashed.model.OrderStatus;
import com.sbsr.sstashed.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find order by order number
    Optional<Order> findByOrderNumber(String orderNumber);

    // Find all orders by user (paginated) - FIXED
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Find orders by user and status - FIXED
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    // Find orders by status
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // Find orders by payment status
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);

    // Find recent orders - FIXED
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC")
    List<Order> findRecentOrdersByUserId(@Param("userId") Long userId, Pageable pageable);

    // Find orders within date range
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    // Count orders by user - FIXED
    Long countByUserId(Long userId);

    // Count orders by status
    Long countByStatus(OrderStatus status);
}
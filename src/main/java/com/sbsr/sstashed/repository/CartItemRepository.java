package com.sbsr.sstashed.repository;

import com.sbsr.sstashed.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Find all items in a cart
    List<CartItem> findByCartId(Long cartId);

    // Find specific item in cart
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    // Delete all items in a cart
    void deleteByCartId(Long cartId);

    // Count items in a cart
    Long countByCartId(Long cartId);

    // Calculate total items quantity in cart
    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Integer getTotalQuantityByCartId(@Param("cartId") Long cartId);
}
package com.sbsr.sstashed.repository;

import com.sbsr.sstashed.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Find all wishlist items for a user
    List<Wishlist> findByUserId(Long userId);

    // Find specific wishlist item
    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    // Check if product is in wishlist
    Boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Delete by user and product
    void deleteByUserIdAndProductId(Long userId, Long productId);

    // Count wishlist items for user
    Long countByUserId(Long userId);
}
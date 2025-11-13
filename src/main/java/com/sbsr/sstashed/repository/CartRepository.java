package com.sbsr.sstashed.repository;

import com.sbsr.sstashed.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Find cart by user ID
    Optional<Cart> findByUserId(Long userId);

    // Check if cart exists for user
    Boolean existsByUserId(Long userId);
}
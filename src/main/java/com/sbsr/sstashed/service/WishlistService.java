package com.sbsr.sstashed.service;

import com.sbsr.sstashed.exception.BadRequestException;
import com.sbsr.sstashed.exception.ResourceNotFoundException;
import com.sbsr.sstashed.model.Product;
import com.sbsr.sstashed.model.User;
import com.sbsr.sstashed.model.Wishlist;
import com.sbsr.sstashed.repository.ProductRepository;
import com.sbsr.sstashed.repository.UserRepository;
import com.sbsr.sstashed.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Wishlist> getUserWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    public Wishlist addToWishlist(Long userId, Long productId) {
        // FIXED: Changed to BadRequestException
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new BadRequestException("Product already in wishlist");
        }

        // FIXED: Changed to ResourceNotFoundException
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();

        return wishlistRepository.save(wishlist);
    }

    public void removeFromWishlist(Long userId, Long productId) {
        // FIXED: Better error message
        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ResourceNotFoundException("Wishlist item not found for user " + userId + " and product " + productId);
        }
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional(readOnly = true)
    public Long getWishlistCount(Long userId) {
        return wishlistRepository.countByUserId(userId);
    }

    public void clearWishlist(Long userId) {
        List<Wishlist> items = wishlistRepository.findByUserId(userId);
        wishlistRepository.deleteAll(items);
    }
}
package com.sbsr.sstashed.controller;

import com.sbsr.sstashed.model.User;
import com.sbsr.sstashed.model.Wishlist;
import com.sbsr.sstashed.repository.UserRepository;
import com.sbsr.sstashed.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository;

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<?> getWishlist(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<Wishlist> wishlist = wishlistService.getUserWishlist(userId);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<?> addToWishlist(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        try {
            Long userId = getCurrentUserId(authentication);
            Wishlist wishlist = wishlistService.addToWishlist(userId, productId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Added to wishlist successfully");
            response.put("wishlist", wishlist);

            // FIXED: Return 201 Created instead of 200
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> removeFromWishlist(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        try {
            Long userId = getCurrentUserId(authentication);
            wishlistService.removeFromWishlist(userId, productId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Removed from wishlist successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<?> checkWishlist(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        try {
            Long userId = getCurrentUserId(authentication);
            boolean isInWishlist = wishlistService.isInWishlist(userId, productId);

            Map<String, Boolean> response = new HashMap<>();
            response.put("isInWishlist", isInWishlist);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getWishlistCount(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            Long count = wishlistService.getWishlistCount(userId);

            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> clearWishlist(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            wishlistService.clearWishlist(userId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Wishlist cleared successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
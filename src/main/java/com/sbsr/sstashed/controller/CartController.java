package com.sbsr.sstashed.controller;

import com.sbsr.sstashed.dto.request.AddToCartRequest;
import com.sbsr.sstashed.dto.request.UpdateCartItemRequest;
import com.sbsr.sstashed.dto.response.CartResponse;
import com.sbsr.sstashed.model.Cart;
import com.sbsr.sstashed.model.CartItem;
import com.sbsr.sstashed.model.User;
import com.sbsr.sstashed.repository.UserRepository;
import com.sbsr.sstashed.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<?> getCart(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<CartItem> items = cartService.getCartItems(userId);
            BigDecimal total = cartService.calculateCartTotal(userId);
            Integer itemCount = cartService.getCartItemCount(userId);

            CartResponse response = CartResponse.builder()
                    .items(items)
                    .total(total)
                    .itemCount(itemCount)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/items")
    public ResponseEntity<?> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication
    ) {
        try {
            Long userId = getCurrentUserId(authentication);
            CartItem cartItem = cartService.addItemToCart(
                    userId,
                    request.getProductId(),
                    request.getQuantity()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Item added to cart successfully");
            response.put("cartItem", cartItem);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication authentication
    ) {
        try {
            getCurrentUserId(authentication); // Verify user is authenticated

            CartItem cartItem = cartService.updateCartItemQuantity(itemId, request.getQuantity());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cart item updated successfully");
            response.put("cartItem", cartItem);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable Long itemId,
            Authentication authentication
    ) {
        try {
            getCurrentUserId(authentication); // Verify user is authenticated
            cartService.removeItemFromCart(itemId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Item removed from cart successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            cartService.clearCart(userId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Cart cleared successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getCartItemCount(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            Integer count = cartService.getCartItemCount(userId);

            Map<String, Integer> response = new HashMap<>();
            response.put("count", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
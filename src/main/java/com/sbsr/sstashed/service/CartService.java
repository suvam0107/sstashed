package com.sbsr.sstashed.service;

import com.sbsr.sstashed.exception.ResourceNotFoundException;
import com.sbsr.sstashed.exception.BadRequestException;
import com.sbsr.sstashed.exception.InsufficientStockException;
import com.sbsr.sstashed.model.*;
import com.sbsr.sstashed.repository.CartItemRepository;
import com.sbsr.sstashed.repository.CartRepository;
import com.sbsr.sstashed.repository.ProductRepository;
import com.sbsr.sstashed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Get or create cart for user
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

                    Cart cart = Cart.builder()
                            .user(user)
                            .build();

                    return cartRepository.save(cart);
                });
    }

    // Get cart by user ID
    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    // Add item to cart
    public CartItem addItemToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Check if product is active and in stock
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BadRequestException("Product is not available");
        }

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(product.getName(), product.getStockQuantity(), quantity);
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException(product.getName(), product.getStockQuantity(), quantity);
            }

            item.setQuantity(newQuantity);
            return cartItemRepository.save(item);
        } else {
            // Add new item
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();

            return cartItemRepository.save(cartItem);
        }
    }

    // Update cart item quantity
    public CartItem updateCartItemQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        Product product = cartItem.getProduct();

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(product.getName(), product.getStockQuantity(), quantity);
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    // Remove item from cart
    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // Clear cart
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user", "userId", userId));

        cartItemRepository.deleteByCartId(cart.getId());
    }

    // Get all items in cart
    public List<CartItem> getCartItems(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user", "userId", userId));

        return cartItemRepository.findByCartId(cart.getId());
    }

    // Calculate cart total
    public BigDecimal calculateCartTotal(Long userId) {
        List<CartItem> items = getCartItems(userId);

        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Get cart item count
    public Integer getCartItemCount(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) {
            return 0;
        }

        return cartItemRepository.getTotalQuantityByCartId(cart.getId());
    }
}
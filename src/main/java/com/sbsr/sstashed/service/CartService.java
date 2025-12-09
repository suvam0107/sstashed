package com.sbsr.sstashed.service;

import com.sbsr.sstashed.exception.ResourceNotFoundException;
import com.sbsr.sstashed.exception.BadRequestException;
import com.sbsr.sstashed.exception.InsufficientStockException;
import com.sbsr.sstashed.exception.UnauthorizedException;
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

    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public CartItem addItemToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BadRequestException("Product is not available");
        }

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(product.getName(), product.getStockQuantity(), quantity);
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            // FIXED: Check new total quantity, not just additional
            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException(product.getName(), product.getStockQuantity(), newQuantity);
            }

            item.setQuantity(newQuantity);
            return cartItemRepository.save(item);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();

            return cartItemRepository.save(cartItem);
        }
    }

    public CartItem updateCartItemQuantity(Long cartItemId, Integer quantity, Long userId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to modify this cart item");
        }

        Product product = cartItem.getProduct();

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(product.getName(), product.getStockQuantity(), quantity);
        }

        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice());
        return cartItemRepository.save(cartItem);
    }

    public void removeItemFromCart(Long cartItemId, Long userId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to remove this cart item");
        }

        cartItemRepository.deleteById(cartItemId);
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user", "userId", userId));

        cartItemRepository.deleteByCartId(cart.getId());
    }

    public List<CartItem> getCartItems(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user", "userId", userId));

        return cartItemRepository.findByCartId(cart.getId());
    }

    public BigDecimal calculateCartTotal(Long userId) {
        List<CartItem> items = getCartItems(userId);

        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getCartItemCount(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) {
            return 0;
        }

        Integer count = cartItemRepository.getTotalQuantityByCartId(cart.getId());
        return count != null ? count : 0; // FIXED: Null safety
    }
}
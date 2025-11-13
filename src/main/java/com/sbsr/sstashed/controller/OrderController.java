package com.sbsr.sstashed.controller;

import com.sbsr.sstashed.dto.request.CheckoutRequest;
import com.sbsr.sstashed.model.Order;
import com.sbsr.sstashed.model.OrderItem;
import com.sbsr.sstashed.model.User;
import com.sbsr.sstashed.repository.UserRepository;
import com.sbsr.sstashed.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody CheckoutRequest request,
            Authentication authentication
    ) {
        try {
            Long userId = getCurrentUserId(authentication);

            Order order = orderService.createOrderFromCart(
                    userId,
                    request.getAddressId(),
                    request.getPaymentMethod()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order placed successfully");
            response.put("order", order);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        try {
            Long userId = getCurrentUserId(authentication);
            Pageable pageable = PageRequest.of(page, size);

            Page<Order> orders = orderService.getUserOrders(userId, pageable);

            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        try {
            getCurrentUserId(authentication); // Verify user is authenticated

            return orderService.getOrderById(orderId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Order not found with id: " + orderId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<?> getOrderItems(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        try {
            getCurrentUserId(authentication); // Verify user is authenticated

            List<OrderItem> items = orderService.getOrderItems(orderId);

            return ResponseEntity.ok(items);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<?> getOrderByNumber(
            @PathVariable String orderNumber,
            Authentication authentication
    ) {
        try {
            getCurrentUserId(authentication); // Verify user is authenticated

            return orderService.getOrderByOrderNumber(orderNumber)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Order not found with number: " + orderNumber);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        try {
            getCurrentUserId(authentication); // Verify user is authenticated

            Order order = orderService.cancelOrder(orderId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order cancelled successfully");
            response.put("order", order);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getUserOrderCount(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            Long count = orderService.getUserOrderCount(userId);

            Map<String, Long> response = new HashMap<>();
            response.put("count", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
package com.sbsr.sstashed.controller;

import com.sbsr.sstashed.dto.request.AddressRequest;
import com.sbsr.sstashed.dto.request.UpdateProfileRequest;
import com.sbsr.sstashed.model.User;
import com.sbsr.sstashed.model.UserAddress;
import com.sbsr.sstashed.repository.UserRepository;
import com.sbsr.sstashed.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final UserRepository userRepository;

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);

            return userService.getUserById(userId)
                    .map(user -> {
                        // Don't send password in response
                        user.setPassword(null);
                        return ResponseEntity.ok(user);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication
    ) {
        try {
            Long userId = getCurrentUserId(authentication);

            User userDetails = new User();
            userDetails.setFirstName(request.getFirstName());
            userDetails.setLastName(request.getLastName());
            userDetails.setPhone(request.getPhone());

            User updatedUser = userService.updateUserProfile(userId, userDetails);
            updatedUser.setPassword(null); // Don't send password

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("user", updatedUser);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getUserAddresses(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<UserAddress> addresses = userService.getUserAddresses(userId);

            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/addresses")
    public ResponseEntity<?> addAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication
    ) {
        try {
            Long userId = getCurrentUserId(authentication);

            UserAddress address = UserAddress.builder()
                    .addressType(request.getAddressType())
                    .streetAddress(request.getStreetAddress())
                    .city(request.getCity())
                    .state(request.getState())
                    .postalCode(request.getPostalCode())
                    .country(request.getCountry())
                    .isDefault(request.getIsDefault())
                    .build();

            UserAddress savedAddress = userService.addAddress(userId, address);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Address added successfully");
            response.put("address", savedAddress);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<?> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request,
            Authentication authentication
    ) {
        try {
            getCurrentUserId(authentication); // Verify user is authenticated

            UserAddress addressDetails = UserAddress.builder()
                    .addressType(request.getAddressType())
                    .streetAddress(request.getStreetAddress())
                    .city(request.getCity())
                    .state(request.getState())
                    .postalCode(request.getPostalCode())
                    .country(request.getCountry())
                    .isDefault(request.getIsDefault())
                    .build();

            UserAddress updatedAddress = userService.updateAddress(addressId, addressDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Address updated successfully");
            response.put("address", updatedAddress);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(
            @PathVariable Long addressId,
            Authentication authentication
    ) {
        try {
            getCurrentUserId(authentication); // Verify user is authenticated
            userService.deleteAddress(addressId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Address deleted successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/addresses/default")
    public ResponseEntity<?> getDefaultAddress(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);

            return userService.getDefaultAddress(userId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "No default address found");
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
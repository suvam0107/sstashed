package com.sbsr.sstashed.service;

import com.sbsr.sstashed.exception.ResourceNotFoundException;
import com.sbsr.sstashed.model.User;
import com.sbsr.sstashed.model.UserAddress;
import com.sbsr.sstashed.repository.UserAddressRepository;
import com.sbsr.sstashed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Update user profile
    public User updateUserProfile(Long userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhone(userDetails.getPhone());

        return userRepository.save(user);
    }

    // Get all addresses for a user
    public List<UserAddress> getUserAddresses(Long userId) {
        return userAddressRepository.findByUserId(userId);
    }

    // Add address for user
    public UserAddress addAddress(Long userId, UserAddress address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        address.setUser(user);

        // If this is set as default, unset other default addresses
        if (address.getIsDefault()) {
            userAddressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(existingDefault -> {
                        existingDefault.setIsDefault(false);
                        userAddressRepository.save(existingDefault);
                    });
        }

        return userAddressRepository.save(address);
    }

    // Update address
    public UserAddress updateAddress(Long addressId, UserAddress addressDetails) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        address.setAddressType(addressDetails.getAddressType());
        address.setStreetAddress(addressDetails.getStreetAddress());
        address.setCity(addressDetails.getCity());
        address.setState(addressDetails.getState());
        address.setPostalCode(addressDetails.getPostalCode());
        address.setCountry(addressDetails.getCountry());

        // Handle default address change
        if (addressDetails.getIsDefault() && !address.getIsDefault()) {
            userAddressRepository.findByUserIdAndIsDefaultTrue(address.getUser().getId())
                    .ifPresent(existingDefault -> {
                        existingDefault.setIsDefault(false);
                        userAddressRepository.save(existingDefault);
                    });
            address.setIsDefault(true);
        }

        return userAddressRepository.save(address);
    }

    // Delete address
    public void deleteAddress(Long addressId) {
        userAddressRepository.deleteById(addressId);
    }

    // Get default address
    public Optional<UserAddress> getDefaultAddress(Long userId) {
        return userAddressRepository.findByUserIdAndIsDefaultTrue(userId);
    }
}
package com.sbsr.sstashed.service;

import com.sbsr.sstashed.model.User;
import com.sbsr.sstashed.model.UserRole;
import com.sbsr.sstashed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sbsr.sstashed.exception.BadRequestException;
import com.sbsr.sstashed.exception.UnauthorizedException;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Register new user
    public User register(String email, String password, String firstName, String lastName, String phone) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists: " + email);
        }

        // Create new user
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    // Authenticate user
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return user;
    }

    // Check if email exists
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
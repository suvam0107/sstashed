package com.sbsr.sstashed.repository;

import com.sbsr.sstashed.model.User;
import com.sbsr.sstashed.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email
    Optional<User> findByEmail(String email);

    // Check if email exists
    Boolean existsByEmail(String email);

    // Find all users by role
    List<User> findByRole(UserRole role);

    // Find active users
    List<User> findByIsActiveTrue();

    // Find user by email and active status
    Optional<User> findByEmailAndIsActiveTrue(String email);
}
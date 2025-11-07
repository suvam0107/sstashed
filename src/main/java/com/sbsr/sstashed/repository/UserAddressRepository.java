package com.sbsr.sstashed.repository;

import com.sbsr.sstashed.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    // Find all addresses for a user
    List<UserAddress> findByUserId(Long userId);

    // Find default address for a user
    Optional<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);

    // Find addresses by user and type
    List<UserAddress> findByUserIdAndAddressType(Long userId, com.sbsr.sstashed.model.AddressType addressType);
}

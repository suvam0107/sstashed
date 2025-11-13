package com.sbsr.sstashed.repository;

import com.sbsr.sstashed.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // Find all images for a product
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    // Find primary image for a product
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);

    // Delete all images for a product
    void deleteByProductId(Long productId);
}
package com.sbsr.sstashed.repository;

import com.sbsr.sstashed.model.Product;
import com.sbsr.sstashed.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find all active products (paginated)
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    // Find products by category
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Find products by category and status
    Page<Product> findByCategoryIdAndStatus(Long categoryId, ProductStatus status, Pageable pageable);

    // Find products by artisan
    List<Product> findByArtisanId(Long artisanId);

    // Search products by name (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.status = :status")
    Page<Product> searchByName(@Param("keyword") String keyword, @Param("status") ProductStatus status, Pageable pageable);

    // Find products within price range
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.status = :status")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("status") ProductStatus status,
                                   Pageable pageable);

    // Search products by name or description
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.status = :status")
    Page<Product> searchProducts(@Param("keyword") String keyword, @Param("status") ProductStatus status, Pageable pageable);

    // Count products by status
    Long countByStatus(ProductStatus status);

    // Find products with low stock
    List<Product> findByStockQuantityLessThanAndStatus(Integer threshold, ProductStatus status);
}
package com.sbsr.sstashed.service;

import com.sbsr.sstashed.dto.response.ProductResponse;
import com.sbsr.sstashed.exception.ResourceNotFoundException;
import com.sbsr.sstashed.model.Category;
import com.sbsr.sstashed.model.Product;
import com.sbsr.sstashed.model.ProductStatus;
import com.sbsr.sstashed.repository.CategoryRepository;
import com.sbsr.sstashed.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // Convert Product entity to ProductResponse DTO
    private ProductResponse convertToDto(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .status(product.getStatus().name())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .artisanId(product.getArtisan() != null ? product.getArtisan().getId() : null)
                .artisanName(product.getArtisan() != null ?
                        product.getArtisan().getFirstName() + " " + product.getArtisan().getLastName() : null)
                .createdAt(product.getCreatedAt())
                .build();
    }

    // Get all active products (paginated)
    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        Page<Product> products = productRepository.findByStatus(ProductStatus.ACTIVE, pageable);
        return products.map(this::convertToDto);
    }

    // Get product by ID with images
    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id).map(this::convertToDto);
    }

    // Search products
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.searchProducts(keyword, ProductStatus.ACTIVE, pageable);
        return products.map(this::convertToDto);
    }

    // Get products by category
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndStatus(categoryId, ProductStatus.ACTIVE, pageable);
    }

    // Get products by price range
    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, ProductStatus.ACTIVE, pageable);
    }

    // Get all categories
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    // Get category by ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Check if product is in stock
    public boolean isProductInStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return product.getStatus() == ProductStatus.ACTIVE
                && product.getStockQuantity() >= quantity;
    }

    // Get product stock quantity
    public Integer getProductStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return product.getStockQuantity();
    }
}
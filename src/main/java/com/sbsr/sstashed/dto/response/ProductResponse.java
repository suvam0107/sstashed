package com.sbsr.sstashed.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private String status;

    // Category info (flattened)
    private Long categoryId;
    private String categoryName;

    // Artisan info (flattened)
    private Long artisanId;
    private String artisanName;

    private LocalDateTime createdAt;
}
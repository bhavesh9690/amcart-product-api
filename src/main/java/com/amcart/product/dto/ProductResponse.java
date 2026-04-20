package com.amcart.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Public product response DTO.
 *
 * imageUrls map keys: "main", "thumb", "zoom"
 * All values are CloudFront CDN URLs (never raw S3 URLs).
 *
 * Angular usage:
 *   imageUrl (product card)   → imageUrls.get("thumb")
 *   ngx-image-zoom (detail)   → imageUrls.get("zoom")
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private UUID id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String brandName;
    private UUID categoryId;
    private String categoryName;
    private boolean inStock;
    private boolean featured;
    private boolean newArrival;
    private boolean active;
    private Set<String> tags;

    /**
     * CDN image URLs keyed by variant name: "main", "thumb", "zoom".
     * Mapped from AWS CloudFront distribution domain.
     */
    private Map<String, String> imageUrls;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

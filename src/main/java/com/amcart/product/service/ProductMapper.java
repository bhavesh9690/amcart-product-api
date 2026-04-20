package com.amcart.product.service;

import com.amcart.product.domain.entity.Category;
import com.amcart.product.domain.entity.Product;
import com.amcart.product.domain.entity.ProductImage;
import com.amcart.product.dto.CategoryResponse;
import com.amcart.product.dto.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        // Copy lazy collections into plain Java collections to force initialization
        // while the Hibernate session is still open. Without this, the
        // PersistentSet/PersistentBag proxy is passed to Jackson which cannot
        // initialize it after the transaction closes.
        Set<String> tags = product.getTags() != null
                ? new HashSet<>(product.getTags()) : new HashSet<>();
        List<ProductImage> images = product.getImages() != null
                ? new java.util.ArrayList<>(product.getImages()) : java.util.List.of();

        Map<String, String> imageUrls = buildImageUrls(images);

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .inStock(product.isInStock())
                .featured(product.isFeatured())
                .newArrival(product.isNewArrival())
                .active(product.isActive())
                .tags(tags)
                .imageUrls(imageUrls)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public CategoryResponse toCategoryResponse(Category category) {
        List<CategoryResponse> children = category.getChildren().stream()
                .map(this::toCategoryResponse)
                .toList();

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .displayOrder(category.getDisplayOrder())
                .children(children)
                .build();
    }

    private Map<String, String> buildImageUrls(List<ProductImage> images) {
        Map<String, String> urls = new java.util.HashMap<>();
        for (ProductImage img : images) {
            String key = switch (img.getVariant()) {
                case MAIN    -> "main";
                case THUMB   -> "thumb";
                case ZOOM    -> "zoom";
                case GALLERY -> "gallery";
            };
            urls.putIfAbsent(key, img.getCdnUrl());
        }
        return urls;
    }
}

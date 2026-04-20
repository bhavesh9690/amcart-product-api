package com.amcart.product.controller;

import com.amcart.product.dto.CategoryResponse;
import com.amcart.product.dto.PagedResponse;
import com.amcart.product.dto.ProductResponse;
import com.amcart.product.service.CategoryService;
import com.amcart.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product category endpoints")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get full category tree")
    public ResponseEntity<List<CategoryResponse>> getCategoryTree() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }

    @GetMapping("/{id}/products")
    @Operation(summary = "Get products by category (paginated)")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByCategory(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(productService.getProductsByCategory(id,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }
}

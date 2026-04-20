package com.amcart.product.service;

import com.amcart.product.domain.entity.Category;
import com.amcart.product.domain.repository.CategoryRepository;
import com.amcart.product.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    /**
     * Returns the full category tree (roots + children).
     * Cached for 30 minutes (configured in RedisConfig).
     */
    @Cacheable("category:tree")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        List<Category> roots = categoryRepository.findRootCategories();
        // Eagerly initialize children within the transaction
        roots.forEach(c -> c.getChildren().size());
        return roots.stream().map(productMapper::toCategoryResponse).toList();
    }
}

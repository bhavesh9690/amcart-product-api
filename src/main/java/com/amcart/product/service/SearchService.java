package com.amcart.product.service;

import com.amcart.product.domain.repository.ProductRepository;
import com.amcart.product.dto.ProductResponse;
import com.amcart.product.service.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public SearchService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Performs a search across product name and description. Currently uses DB fallback.
     * Elasticsearch integration can be added later to replace this implementation.
     */
    public Page<ProductResponse> search(String q, Pageable pageable) {
        Page<com.amcart.product.domain.entity.Product> page = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndActiveTrue(q, q, pageable);
        return page.map(productMapper::toResponse);
    }
}


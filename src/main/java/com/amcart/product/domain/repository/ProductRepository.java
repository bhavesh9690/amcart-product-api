package com.amcart.product.domain.repository;

import com.amcart.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository
        extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlugAndActiveTrue(String slug);

    Optional<Product> findByIdAndActiveTrue(UUID id);

    Page<Product> findByFeaturedTrueAndActiveTrue(Pageable pageable);

    Page<Product> findByNewArrivalTrueAndActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndActiveTrue(UUID categoryId, Pageable pageable);

    @Query("""
           SELECT p FROM Product p
           WHERE p.category.id = :categoryId
             AND p.id <> :excludeId
             AND p.active = true
           ORDER BY p.createdAt DESC
           """)
    Page<Product> findRelatedProducts(@Param("categoryId") UUID categoryId,
                                      @Param("excludeId") UUID excludeId,
                                      Pageable pageable);
}

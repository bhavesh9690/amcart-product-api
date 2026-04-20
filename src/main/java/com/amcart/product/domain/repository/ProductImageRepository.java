package com.amcart.product.domain.repository;

import com.amcart.product.domain.entity.ImageVariant;
import com.amcart.product.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    List<ProductImage> findByProductId(UUID productId);

    Optional<ProductImage> findByProductIdAndVariant(UUID productId, ImageVariant variant);

    @Query("SELECT pi.s3Key FROM ProductImage pi WHERE pi.product.id = :productId")
    List<String> findS3KeysByProductId(@Param("productId") UUID productId);

    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
    void deleteAllByProductId(@Param("productId") UUID productId);
}

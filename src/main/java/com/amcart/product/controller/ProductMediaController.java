package com.amcart.product.controller;

import com.amcart.product.config.CloudFrontConfig;
import com.amcart.product.domain.entity.ImageVariant;
import com.amcart.product.domain.repository.ProductImageRepository;
import com.amcart.product.domain.repository.ProductRepository;
import com.amcart.product.service.MediaStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/images")
@RequiredArgsConstructor
@Tag(name = "Product Images", description = "Image upload and management for products")
public class ProductMediaController {

    private final MediaStorageService mediaStorageService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final S3Client s3Client;
    private final CloudFrontConfig cloudFrontConfig;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload product image — generates MAIN, THUMB, ZOOM variants on S3")
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable UUID productId,
            @RequestPart("image") MultipartFile image) throws IOException {

        var product = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        var cdnUrls = mediaStorageService.uploadProductImages(image, product);

        // Convert enum keys to lowercase string keys for JSON response
        Map<String, String> response = Map.of(
                "main",  cdnUrls.getOrDefault(ImageVariant.MAIN, ""),
                "thumb", cdnUrls.getOrDefault(ImageVariant.THUMB, ""),
                "zoom",  cdnUrls.getOrDefault(ImageVariant.ZOOM, "")
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Operation(summary = "Delete a single image variant from S3 and DB")
    public ResponseEntity<Void> deleteImage(
            @PathVariable UUID productId,
            @PathVariable UUID imageId) {

        var image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found: " + imageId));

        if (!image.getProduct().getId().equals(productId)) {
            return ResponseEntity.notFound().build();
        }

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(cloudFrontConfig.getBucketName())
                .key(image.getS3Key())
                .build());

        productImageRepository.delete(image);
        return ResponseEntity.noContent().build();
    }
}

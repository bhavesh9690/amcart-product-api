package com.amcart.product.service;

import com.amcart.product.config.CloudFrontConfig;
import com.amcart.product.domain.entity.ImageVariant;
import com.amcart.product.domain.entity.Product;
import com.amcart.product.domain.entity.ProductImage;
import com.amcart.product.domain.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles image validation, resizing (3 variants), parallel upload to S3,
 * CDN URL construction, and S3 object deletion.
 *
 * Security: only JPEG, PNG, and WebP are accepted — SVG is rejected to
 * prevent stored XSS via inline SVG content injection.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    @Value("${media.max-file-size-bytes:10485760}")   // 10 MB default
    private long maxFileSizeBytes;

    private final S3Client s3Client;
    private final CloudFrontConfig cloudFrontConfig;
    private final ProductImageRepository productImageRepository;

    private final ExecutorService uploadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    // -----------------------------------------------------------------------
    // Validation
    // -----------------------------------------------------------------------

    /**
     * Validates content-type and file size.
     * Throws {@link IllegalArgumentException} if the file is invalid.
     */
    public void validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Unsupported image type: " + contentType +
                    ". Allowed: " + ALLOWED_CONTENT_TYPES);
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new IllegalArgumentException(
                    "File size " + file.getSize() + " bytes exceeds maximum " + maxFileSizeBytes);
        }
    }

    // -----------------------------------------------------------------------
    // Upload
    // -----------------------------------------------------------------------

    /**
     * Validates, resizes to 3 variants, uploads all in parallel to S3,
     * persists ProductImage records, and returns a map of variant → CDN URL.
     */
    public Map<ImageVariant, String> uploadProductImages(MultipartFile file, Product product)
            throws IOException {

        validateFile(file);
        byte[] originalBytes = file.getBytes();
        String contentType = Objects.requireNonNull(file.getContentType()).toLowerCase();

        record VariantSpec(ImageVariant variant, int width, int height, boolean crop) {}

        List<VariantSpec> specs = List.of(
                new VariantSpec(ImageVariant.MAIN,  800,  800, false),
                new VariantSpec(ImageVariant.THUMB, 400,  400, true),
                new VariantSpec(ImageVariant.ZOOM, 1200, 1200, false)
        );

        Map<ImageVariant, String> cdnUrls = new EnumMap<>(ImageVariant.class);
        List<CompletableFuture<Void>> uploads = new ArrayList<>();

        for (VariantSpec spec : specs) {
            byte[] resized = resize(originalBytes, spec.width(), spec.height(), spec.crop());
            String s3Key = buildS3Key(product.getId(), spec.variant(), contentType);
            String cdnUrl = cloudFrontConfig.buildCdnUrl(s3Key);
            cdnUrls.put(spec.variant(), cdnUrl);

            boolean isPrimary = spec.variant() == ImageVariant.MAIN;
            ProductImage imageRecord = ProductImage.builder()
                    .product(product)
                    .s3Key(s3Key)
                    .cdnUrl(cdnUrl)
                    .variant(spec.variant())
                    .primary(isPrimary)
                    .build();

            uploads.add(CompletableFuture.runAsync(() -> {
                uploadToS3(s3Key, resized, contentType);
                productImageRepository.save(imageRecord);
            }, uploadExecutor));
        }

        CompletableFuture.allOf(uploads.toArray(new CompletableFuture[0])).join();
        log.info("Uploaded {} image variants for product {}", specs.size(), product.getId());
        return cdnUrls;
    }

    // -----------------------------------------------------------------------
    // Delete
    // -----------------------------------------------------------------------

    /**
     * Deletes all S3 objects for a product (batch delete) and removes DB records.
     */
    public void deleteProductImages(UUID productId) {
        List<String> s3Keys = productImageRepository.findS3KeysByProductId(productId);
        if (s3Keys.isEmpty()) return;

        List<ObjectIdentifier> identifiers = s3Keys.stream()
                .map(k -> ObjectIdentifier.builder().key(k).build())
                .toList();

        deleteFromS3(identifiers);
        productImageRepository.deleteAllByProductId(productId);
        log.info("Deleted {} S3 objects for product {}", s3Keys.size(), productId);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private byte[] resize(byte[] source, int width, int height, boolean crop) throws IOException {
        var out = new ByteArrayOutputStream();
        var builder = Thumbnails.of(new java.io.ByteArrayInputStream(source))
                .size(width, height)
                .outputFormat("jpg");
        if (crop) {
            builder.crop(Positions.CENTER);
        }
        builder.toOutputStream(out);
        return out.toByteArray();
    }

    private String buildS3Key(UUID productId, ImageVariant variant, String contentType) {
        String ext = contentType.contains("png") ? "png" : "jpg";
        return switch (variant) {
            case MAIN    -> "products/" + productId + "/main." + ext;
            case THUMB   -> "products/" + productId + "/thumb_400x400." + ext;
            case ZOOM    -> "products/" + productId + "/zoom_1200x1200." + ext;
            case GALLERY -> "products/" + productId + "/gallery_" + UUID.randomUUID() + "." + ext;
        };
    }

    private void uploadToS3(String key, byte[] data, String contentType) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(cloudFrontConfig.getBucketName())
                        .key(key)
                        .contentType(contentType)
                        .contentLength((long) data.length)
                        // No public-read ACL — bucket is private, CloudFront OAC is the reader
                        .build(),
                RequestBody.fromBytes(data));
    }

    private void deleteFromS3(List<ObjectIdentifier> identifiers) {
        s3Client.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(cloudFrontConfig.getBucketName())
                .delete(Delete.builder().objects(identifiers).quiet(true).build())
                .build());
    }
}

package com.amcart.product.service;

import com.amcart.product.domain.entity.Brand;
import com.amcart.product.domain.entity.Category;
import com.amcart.product.domain.entity.Product;
import com.amcart.product.domain.repository.BrandRepository;
import com.amcart.product.domain.repository.CategoryRepository;
import com.amcart.product.domain.repository.ProductRepository;
import com.amcart.product.dto.CreateProductRequest;
import com.amcart.product.dto.PagedResponse;
import com.amcart.product.dto.ProductResponse;
import com.amcart.product.dto.UpdateProductRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final MediaStorageService mediaStorageService;
    private final ProductMapper productMapper;
    private final ApplicationEventPublisher eventPublisher;

    // -----------------------------------------------------------------------
    // Read operations (cached)
    // -----------------------------------------------------------------------

    @Cacheable(cacheNames = "product", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        return productMapper.toResponse(product);
    }

    @Cacheable(cacheNames = "featured-products")
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getFeaturedProducts(Pageable pageable) {
        return PagedResponse.from(
                productRepository.findByFeaturedTrueAndActiveTrue(pageable)
                                 .map(productMapper::toResponse));
    }

    @Cacheable(cacheNames = "new-arrivals")
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getNewArrivals(Pageable pageable) {
        return PagedResponse.from(
                productRepository.findByNewArrivalTrueAndActiveTrue(pageable)
                                 .map(productMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getRelatedProducts(UUID productId, Pageable pageable) {
        Product product = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
        UUID categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        if (categoryId == null) {
            return PagedResponse.<ProductResponse>builder()
                    .content(java.util.List.of()).page(0).size(0)
                    .totalElements(0).totalPages(0).build();
        }
        return PagedResponse.from(
                productRepository.findRelatedProducts(categoryId, productId, pageable)
                                 .map(productMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable) {
        return PagedResponse.from(
                productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable)
                                 .map(productMapper::toResponse));
    }

    // -----------------------------------------------------------------------
    // Write operations (admin only — cache eviction)
    // -----------------------------------------------------------------------

    @Transactional
    @CacheEvict(cacheNames = {"featured-products", "new-arrivals"}, allEntries = true)
    public ProductResponse createProduct(CreateProductRequest request, MultipartFile image)
            throws IOException {

        Brand brand = request.getBrandId() != null
                ? brandRepository.findById(request.getBrandId()).orElse(null) : null;
        Category category = request.getCategoryId() != null
                ? categoryRepository.findById(request.getCategoryId()).orElse(null) : null;

        Product product = Product.builder()
                .name(request.getName())
                .slug(generateSlug(request.getName()))
                .description(request.getDescription())
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .brand(brand)
                .category(category)
                .featured(request.isFeatured())
                .newArrival(request.isNewArrival())
                .tags(request.getTags() != null ? request.getTags() : new java.util.HashSet<>())
                .build();

        product = productRepository.save(product);

        if (image != null && !image.isEmpty()) {
            mediaStorageService.uploadProductImages(image, product);
            product = productRepository.findById(product.getId()).orElseThrow();
        }

        eventPublisher.publishEvent(new ProductCreatedEvent(this, product.getId()));
        log.info("Product created: {} (id={})", product.getName(), product.getId());
        return productMapper.toResponse(product);
    }

    @Transactional
    @CacheEvict(cacheNames = {"product", "featured-products", "new-arrivals"}, allEntries = true)
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request, MultipartFile image)
            throws IOException {

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        if (request.getName() != null) {
            product.setName(request.getName());
            product.setSlug(generateSlug(request.getName()));
        }
        if (request.getDescription() != null)  product.setDescription(request.getDescription());
        if (request.getPrice() != null)         product.setPrice(request.getPrice());
        if (request.getSalePrice() != null)     product.setSalePrice(request.getSalePrice());
        if (request.getFeatured() != null)      product.setFeatured(request.getFeatured());
        if (request.getNewArrival() != null)    product.setNewArrival(request.getNewArrival());
        if (request.getActive() != null)        product.setActive(request.getActive());
        if (request.getTags() != null)          product.setTags(request.getTags());

        if (request.getBrandId() != null) {
            product.setBrand(brandRepository.findById(request.getBrandId()).orElse(null));
        }
        if (request.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(request.getCategoryId()).orElse(null));
        }

        if (image != null && !image.isEmpty()) {
            mediaStorageService.deleteProductImages(product.getId());
            mediaStorageService.uploadProductImages(image, product);
        }

        product = productRepository.save(product);
        eventPublisher.publishEvent(new ProductUpdatedEvent(this, product.getId()));
        return productMapper.toResponse(product);
    }

    @Transactional
    @CacheEvict(cacheNames = {"product", "featured-products", "new-arrivals"}, allEntries = true)
    public void deleteProduct(UUID id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        // Soft delete — preserves order/review history referential integrity
        product.setActive(false);
        productRepository.save(product);

        // Clean S3 asynchronously via event (best-effort; bounded to this service)
        mediaStorageService.deleteProductImages(id);
        eventPublisher.publishEvent(new ProductDeletedEvent(this, id));
        log.info("Product soft-deleted: {}", id);
    }

    // -----------------------------------------------------------------------
    // Slug generation
    // -----------------------------------------------------------------------

    private String generateSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    // -----------------------------------------------------------------------
    // Internal domain events (stubbed — swappable for AWS SNS/SQS later)
    // -----------------------------------------------------------------------

    public record ProductCreatedEvent(Object source, UUID productId) {}
    public record ProductUpdatedEvent(Object source, UUID productId) {}
    public record ProductDeletedEvent(Object source, UUID productId) {}
}

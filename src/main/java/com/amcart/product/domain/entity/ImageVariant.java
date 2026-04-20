package com.amcart.product.domain.entity;

/**
 * Mirrors the image_variant PostgreSQL ENUM defined in V4 migration.
 */
public enum ImageVariant {
    MAIN,
    THUMB,
    ZOOM,
    GALLERY
}

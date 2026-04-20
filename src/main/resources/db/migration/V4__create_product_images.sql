-- V4: product_images table
-- Each product can have multiple images (MAIN, THUMB, ZOOM variants) and gallery images
CREATE TYPE image_variant AS ENUM ('MAIN', 'THUMB', 'ZOOM', 'GALLERY');

CREATE TABLE product_images (
    id            UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id    UUID          NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    s3_key        VARCHAR(600)  NOT NULL,
    cdn_url       VARCHAR(800)  NOT NULL,
    variant       image_variant NOT NULL,
    display_order INT           NOT NULL DEFAULT 0,
    is_primary    BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_product_images_product_id         ON product_images (product_id);
CREATE INDEX idx_product_images_product_variant     ON product_images (product_id, variant);
CREATE UNIQUE INDEX idx_product_images_primary      ON product_images (product_id, is_primary)
    WHERE is_primary = TRUE;

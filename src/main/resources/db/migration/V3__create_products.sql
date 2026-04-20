-- V3: products table
CREATE TABLE products (
    id             UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(255)   NOT NULL,
    slug           VARCHAR(255)   NOT NULL UNIQUE,
    description    TEXT,
    price          NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    sale_price     NUMERIC(10, 2)           CHECK (sale_price >= 0),
    brand_id       UUID           REFERENCES brands(id)     ON DELETE SET NULL,
    category_id    UUID           REFERENCES categories(id) ON DELETE SET NULL,
    is_featured    BOOLEAN        NOT NULL DEFAULT FALSE,
    is_new_arrival BOOLEAN        NOT NULL DEFAULT FALSE,
    is_active      BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_products_category_id   ON products (category_id);
CREATE INDEX idx_products_brand_id      ON products (brand_id);
CREATE INDEX idx_products_is_featured   ON products (is_featured) WHERE is_featured = TRUE;
CREATE INDEX idx_products_is_new_arrival ON products (is_new_arrival) WHERE is_new_arrival = TRUE;
CREATE INDEX idx_products_is_active     ON products (is_active);
CREATE INDEX idx_products_created_at    ON products (created_at DESC);
CREATE INDEX idx_products_slug          ON products (slug);

-- V2: categories table (self-referencing tree)
CREATE TABLE categories (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(120) NOT NULL,
    slug          VARCHAR(120) NOT NULL UNIQUE,
    parent_id     UUID        REFERENCES categories(id) ON DELETE SET NULL,
    display_order INT         NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_categories_parent_id   ON categories (parent_id);
CREATE INDEX idx_categories_slug        ON categories (slug);
CREATE INDEX idx_categories_order       ON categories (display_order);

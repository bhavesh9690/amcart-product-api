-- V5: product_tags table
CREATE TABLE product_tags (
    product_id UUID        NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tag        VARCHAR(80) NOT NULL,
    PRIMARY KEY (product_id, tag)
);

CREATE INDEX idx_product_tags_tag ON product_tags (tag);

-- V6: Seed sample data (brands, categories, products, images, tags)

-- ── Brands ────────────────────────────────────────────────────────────────────
INSERT INTO brands (id, name, slug) VALUES
    ('b1000000-0000-0000-0000-000000000001', 'TechNova',    'technova'),
    ('b1000000-0000-0000-0000-000000000002', 'StyleCraft',  'stylecraft'),
    ('b1000000-0000-0000-0000-000000000003', 'HomeBliss',   'homebliss');

-- ── Categories ────────────────────────────────────────────────────────────────
INSERT INTO categories (id, name, slug, parent_id, display_order) VALUES
    ('c1000000-0000-0000-0000-000000000001', 'Electronics',       'electronics',        NULL, 1),
    ('c1000000-0000-0000-0000-000000000002', 'Clothing',          'clothing',           NULL, 2),
    ('c1000000-0000-0000-0000-000000000003', 'Home & Kitchen',    'home-kitchen',       NULL, 3),
    ('c1000000-0000-0000-0000-000000000004', 'Mobile Phones',     'mobile-phones',      'c1000000-0000-0000-0000-000000000001', 1),
    ('c1000000-0000-0000-0000-000000000005', 'Laptops',           'laptops',            'c1000000-0000-0000-0000-000000000001', 2),
    ('c1000000-0000-0000-0000-000000000006', 'Men''s Wear',       'mens-wear',          'c1000000-0000-0000-0000-000000000002', 1);

-- ── Products ──────────────────────────────────────────────────────────────────
INSERT INTO products (id, name, slug, description, price, sale_price, brand_id, category_id, is_featured, is_new_arrival, is_active) VALUES
    ('a1000000-0000-0000-0000-000000000001',
     'TechNova X12 Smartphone',
     'technova-x12-smartphone',
     'Flagship 6.7" AMOLED smartphone with 200MP camera and 5000mAh battery.',
     999.99, 849.99,
     'b1000000-0000-0000-0000-000000000001',
     'c1000000-0000-0000-0000-000000000004',
     TRUE, TRUE, TRUE),

    ('a1000000-0000-0000-0000-000000000002',
     'TechNova ProBook 15',
     'technova-probook-15',
     '15.6" laptop with Intel Core i7, 16GB RAM, 512GB SSD, and 4K display.',
     1299.99, NULL,
     'b1000000-0000-0000-0000-000000000001',
     'c1000000-0000-0000-0000-000000000005',
     TRUE, FALSE, TRUE),

    ('a1000000-0000-0000-0000-000000000003',
     'TechNova BudPro Wireless Earbuds',
     'technova-budpro-wireless-earbuds',
     'True wireless earbuds with active noise cancellation and 30h battery life.',
     149.99, 119.99,
     'b1000000-0000-0000-0000-000000000001',
     'c1000000-0000-0000-0000-000000000001',
     FALSE, TRUE, TRUE),

    ('a1000000-0000-0000-0000-000000000004',
     'TechNova SmartWatch Ultra',
     'technova-smartwatch-ultra',
     'Advanced health tracking smartwatch with GPS and AMOLED always-on display.',
     349.99, 299.99,
     'b1000000-0000-0000-0000-000000000001',
     'c1000000-0000-0000-0000-000000000001',
     TRUE, TRUE, TRUE),

    ('a1000000-0000-0000-0000-000000000005',
     'StyleCraft Classic Polo T-Shirt',
     'stylecraft-classic-polo-tshirt',
     'Premium cotton polo shirt available in multiple colours. Slim fit.',
     39.99, NULL,
     'b1000000-0000-0000-0000-000000000002',
     'c1000000-0000-0000-0000-000000000006',
     FALSE, FALSE, TRUE),

    ('a1000000-0000-0000-0000-000000000006',
     'StyleCraft Urban Joggers',
     'stylecraft-urban-joggers',
     'Comfortable stretch-fabric joggers perfect for gym or casual wear.',
     59.99, 44.99,
     'b1000000-0000-0000-0000-000000000002',
     'c1000000-0000-0000-0000-000000000006',
     FALSE, TRUE, TRUE),

    ('a1000000-0000-0000-0000-000000000007',
     'StyleCraft Leather Wallet',
     'stylecraft-leather-wallet',
     'Genuine leather bi-fold wallet with RFID blocking and 8 card slots.',
     49.99, NULL,
     'b1000000-0000-0000-0000-000000000002',
     'c1000000-0000-0000-0000-000000000002',
     FALSE, FALSE, TRUE),

    ('a1000000-0000-0000-0000-000000000008',
     'HomeBliss Air Purifier 360',
     'homebliss-air-purifier-360',
     'HEPA + activated carbon air purifier covering up to 500 sq ft. Ultra-quiet.',
     199.99, 169.99,
     'b1000000-0000-0000-0000-000000000003',
     'c1000000-0000-0000-0000-000000000003',
     TRUE, FALSE, TRUE),

    ('a1000000-0000-0000-0000-000000000009',
     'HomeBliss Ceramic Non-Stick Cookware Set',
     'homebliss-ceramic-nonstick-cookware-set',
     '10-piece ceramic non-stick cookware set, oven safe up to 450°F.',
     129.99, 99.99,
     'b1000000-0000-0000-0000-000000000003',
     'c1000000-0000-0000-0000-000000000003',
     FALSE, FALSE, TRUE),

    ('a1000000-0000-0000-0000-000000000010',
     'HomeBliss Smart Coffee Maker',
     'homebliss-smart-coffee-maker',
     'Wi-Fi enabled coffee maker with built-in grinder, 12-cup carafe and app control.',
     89.99, 74.99,
     'b1000000-0000-0000-0000-000000000003',
     'c1000000-0000-0000-0000-000000000003',
     FALSE, TRUE, TRUE);

-- ── Product Images ────────────────────────────────────────────────────────────
INSERT INTO product_images (id, product_id, s3_key, cdn_url, variant, display_order, is_primary) VALUES
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000001', 'products/technova-x12/main.jpg',      'https://cdn.example.com/products/technova-x12/main.jpg',      'MAIN',    0, TRUE),
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000001', 'products/technova-x12/thumb.jpg',     'https://cdn.example.com/products/technova-x12/thumb.jpg',     'THUMB',   0, FALSE),
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000002', 'products/technova-probook/main.jpg',  'https://cdn.example.com/products/technova-probook/main.jpg',  'MAIN',    0, TRUE),
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000002', 'products/technova-probook/thumb.jpg', 'https://cdn.example.com/products/technova-probook/thumb.jpg', 'THUMB',   0, FALSE),
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000003', 'products/technova-budpro/main.jpg',   'https://cdn.example.com/products/technova-budpro/main.jpg',   'MAIN',    0, TRUE),
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000004', 'products/technova-watch/main.jpg',    'https://cdn.example.com/products/technova-watch/main.jpg',    'MAIN',    0, TRUE),
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000005', 'products/stylecraft-polo/main.jpg',   'https://cdn.example.com/products/stylecraft-polo/main.jpg',   'MAIN',    0, TRUE),
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000006', 'products/stylecraft-joggers/main.jpg','https://cdn.example.com/products/stylecraft-joggers/main.jpg','MAIN',    0, TRUE),
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000007', 'products/stylecraft-wallet/main.jpg', 'https://cdn.example.com/products/stylecraft-wallet/main.jpg', 'MAIN',    0, TRUE),
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000008', 'products/homebliss-purifier/main.jpg','https://cdn.example.com/products/homebliss-purifier/main.jpg','MAIN',    0, TRUE),
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000009', 'products/homebliss-cookware/main.jpg','https://cdn.example.com/products/homebliss-cookware/main.jpg','MAIN',    0, TRUE),
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000010', 'products/homebliss-coffee/main.jpg',  'https://cdn.example.com/products/homebliss-coffee/main.jpg',  'MAIN',    0, TRUE);

-- ── Product Tags ──────────────────────────────────────────────────────────────
INSERT INTO product_tags (product_id, tag) VALUES
    ('a1000000-0000-0000-0000-000000000001', 'smartphone'),
    ('a1000000-0000-0000-0000-000000000001', '5g'),
    ('a1000000-0000-0000-0000-000000000001', 'flagship'),
    ('a1000000-0000-0000-0000-000000000002', 'laptop'),
    ('a1000000-0000-0000-0000-000000000002', 'ultrabook'),
    ('a1000000-0000-0000-0000-000000000002', '4k'),
    ('a1000000-0000-0000-0000-000000000003', 'earbuds'),
    ('a1000000-0000-0000-0000-000000000003', 'wireless'),
    ('a1000000-0000-0000-0000-000000000003', 'anc'),
    ('a1000000-0000-0000-0000-000000000004', 'smartwatch'),
    ('a1000000-0000-0000-0000-000000000004', 'fitness'),
    ('a1000000-0000-0000-0000-000000000004', 'gps'),
    ('a1000000-0000-0000-0000-000000000005', 'polo'),
    ('a1000000-0000-0000-0000-000000000005', 'cotton'),
    ('a1000000-0000-0000-0000-000000000005', 'menswear'),
    ('a1000000-0000-0000-0000-000000000006', 'joggers'),
    ('a1000000-0000-0000-0000-000000000006', 'sportswear'),
    ('a1000000-0000-0000-0000-000000000007', 'wallet'),
    ('a1000000-0000-0000-0000-000000000007', 'leather'),
    ('a1000000-0000-0000-0000-000000000007', 'rfid'),
    ('a1000000-0000-0000-0000-000000000008', 'air-purifier'),
    ('a1000000-0000-0000-0000-000000000008', 'hepa'),
    ('a1000000-0000-0000-0000-000000000008', 'home'),
    ('a1000000-0000-0000-0000-000000000009', 'cookware'),
    ('a1000000-0000-0000-0000-000000000009', 'ceramic'),
    ('a1000000-0000-0000-0000-000000000009', 'non-stick'),
    ('a1000000-0000-0000-0000-000000000010', 'coffee-maker'),
    ('a1000000-0000-0000-0000-000000000010', 'smart-home'),
    ('a1000000-0000-0000-0000-000000000010', 'kitchen');


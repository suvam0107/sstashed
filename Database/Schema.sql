CREATE DATABASE sstashed CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sstashed;

-- Users Table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(15),
    role ENUM('CUSTOMER', 'ARTISAN', 'ADMIN') DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- User Addresses Table
CREATE TABLE user_addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    address_type ENUM('HOME', 'WORK', 'OTHER') DEFAULT 'HOME',
    street_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(50) DEFAULT 'India',
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- Categories Table
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
);

-- Products Table
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    artisan_id BIGINT NOT NULL,
    category_id BIGINT,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    image_url VARCHAR(255),
    status ENUM('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (artisan_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_artisan_id (artisan_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_price (price)
);

-- Carts Table
CREATE TABLE carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- Cart Items Table
CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_product (cart_id, product_id),
    INDEX idx_cart_id (cart_id),
    INDEX idx_product_id (product_id)
);

-- Orders Table
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    payment_method ENUM('COD', 'CARD', 'UPI', 'NET_BANKING') DEFAULT 'COD',
    shipping_address VARCHAR(255) NOT NULL,
    shipping_city VARCHAR(100) NOT NULL,
    shipping_state VARCHAR(100) NOT NULL,
    shipping_postal_code VARCHAR(20) NOT NULL,
    shipping_country VARCHAR(50) DEFAULT 'India',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_order_number (order_number),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date)
);

-- Order Items Table
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
);

-- Wishlist Table
CREATE TABLE wishlist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_product (user_id, product_id),
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id)
);

CREATE USER 'sstashed_user'@'localhost' IDENTIFIED BY 'ilovejava';

GRANT ALL PRIVILEGES ON sstashed.* TO 'sstashed_user'@'localhost';

FLUSH PRIVILEGES;

-- Insert sample categories
INSERT INTO categories (name, description, is_active) VALUES
('Handicrafts', 'Traditional handmade crafts', TRUE),
('Textiles', 'Handwoven textiles and fabrics', TRUE),
('Pottery', 'Clay and ceramic products', TRUE),
('Jewelry', 'Handmade jewelry and accessories', TRUE),
('Home Decor', 'Decorative items for home', TRUE);

-- Insert sample artisan user
INSERT INTO users (email, password, first_name, last_name, phone, role, is_active) VALUES
('artisan@example.com', 'password123', 'Ravi', 'Kumar', '9876543210', 'ARTISAN', TRUE);

-- Insert sample customer
INSERT INTO users (email, password, first_name, last_name, phone, role, is_active) VALUES
('customer@example.com', 'password123', 'Priya', 'Sharma', '9876543211', 'CUSTOMER', TRUE);

-- Insert sample products (assuming artisan_id = 1)
INSERT INTO products (artisan_id, category_id, name, description, price, stock_quantity, status) VALUES
(1, 1, 'Handwoven Basket', 'Beautiful handwoven basket made from natural materials', 599.00, 15, 'ACTIVE'),
(1, 2, 'Cotton Saree', 'Traditional handloom cotton saree with intricate designs', 1299.00, 8, 'ACTIVE'),
(1, 3, 'Clay Pot Set', 'Set of 3 handmade clay pots', 399.00, 20, 'ACTIVE'),
(1, 4, 'Beaded Necklace', 'Colorful beaded necklace handcrafted by local artisans', 799.00, 12, 'ACTIVE'),
(1, 5, 'Wall Hanging', 'Decorative wall hanging with traditional motifs', 899.00, 10, 'ACTIVE');

-- Insert Users (10 Artisans + 1 Customer)
INSERT INTO users (email, password, first_name, last_name, phone, role, is_active) VALUES
-- Artisans
('soham.sharma@artisan.com', 'password1', 'Soham', 'Sharma', '9876543210', 'ARTISAN', TRUE),
('aditya.kumar@artisan.com', 'password2', 'Aditya', 'Kumar', '9876543211', 'ARTISAN', TRUE),
('suyash.patel@artisan.com', 'password3', 'Suyash', 'Patel', '9876543212', 'ARTISAN', TRUE),
('ankit.reddy@artisan.com', 'password4', 'Ankit', 'Reddy', '9876543213', 'ARTISAN', TRUE),
('srinkonee.nair@artisan.com', 'password5', 'Srinkonee', 'Nair', '9876543214', 'ARTISAN', TRUE),
('tmojit.singh@artisan.com', 'password6', 'Tamojit', 'Singh', '9876543215', 'ARTISAN', TRUE),
('sriparna.das@artisan.com', 'password7', 'Sriparna', 'Das', '9876543216', 'ARTISAN', TRUE),
('tarif.mehta@artisan.com', 'password8', 'Tarif', 'Mehta', '9876543217', 'ARTISAN', TRUE),
('yashamoti.rao@artisan.com', 'password9', 'Yashamoti', 'Rao', '9876543218', 'ARTISAN', TRUE),
('ronit.joshi@artisan.com', 'password10', 'Ronit', 'Joshi', '9876543219', 'ARTISAN', TRUE),
-- Customer
('suvam01072005@gmail.com', 'password123', 'Suvam', 'Roy', '9831058408', 'CUSTOMER', TRUE),
('sidbose04@gmail.com', 'password123', 'Sutirtha', 'Bose', '9830453319', 'CUSTOMER', TRUE);

-- Insert Customer Address
INSERT INTO user_addresses (user_id, address_type, street_address, city, state, postal_code, country, is_default) VALUES
(11, 'HOME', '43/4B, S. N. Banerjee Road', 'Kolkata', 'West Bengal', '700014', 'India', TRUE),
(12, 'WORK', '909, Tower-C, IBM India Pvt Ltd, Sector 39', 'Gurugram', 'Haryana', '122003', 'India', TRUE);

-- Insert Categories
INSERT INTO categories (name, description, image_url, is_active) VALUES
('Pottery & Ceramics', 'Handcrafted pottery, ceramic bowls, plates, vases and decorative items made by skilled artisans', 'https://ik.imagekit.io/suvam0107/category/pottery&ceramics.png', TRUE),
('Textiles & Fabrics', 'Traditional handwoven textiles, embroidered fabrics, scarves, and home decor textiles', 'https://ik.imagekit.io/suvam0107/category/textiles&fabrics.png', TRUE),
('Wooden Crafts', 'Hand-carved wooden sculptures, furniture, toys, and decorative wooden items', 'https://ik.imagekit.io/suvam0107/category/woodencrafts.png', TRUE),
('Jewelry & Accessories', 'Handmade jewelry, beaded accessories, traditional ornaments and fashion accessories', 'https://ik.imagekit.io/suvam0107/category/jewelry&accessories.png', TRUE),
('Home Decor', 'Decorative items for home including wall hangings, lanterns, candles and artistic pieces', 'https://ik.imagekit.io/suvam0107/category/homedecor.png', TRUE),
('Bags & Baskets', 'Handwoven bags, baskets, jute products and eco-friendly carrying solutions', 'https://ik.imagekit.io/suvam0107/category/bags&baskets.png', TRUE);

-- Insert Products (40 Products)
INSERT INTO products (artisan_id, category_id, name, description, price, stock_quantity, image_url, status) VALUES
-- Pottery & Ceramics (8 products)
(1, 1, 'Handcrafted Blue Ceramic Vase', 'Beautiful hand-painted ceramic vase with traditional blue floral patterns. Perfect for fresh or dried flowers.', 1299.00, 15, 'https://ik.imagekit.io/suvam0107/product/HandcraftedBlueCeramicVase.jpg', 'ACTIVE'),
(1, 1, 'Terracotta Dinner Plate Set', 'Set of 8 rustic terracotta dinner plates and 1 glass, naturally finished and food-safe. Earthy and elegant.', 2499.00, 10, 'https://ik.imagekit.io/suvam0107/product/TerracottaDinnerPlateSet.jpg', 'ACTIVE'),
(2, 1, 'Ceramic Coffee Mug with Handle', 'Artisan-made ceramic coffee mug with unique glaze patterns. Microwave and dishwasher safe.', 399.00, 25, 'https://ik.imagekit.io/suvam0107/product/CeramicCoffeeMugwithHandle.jpg', 'ACTIVE'),
(2, 1, 'Decorative Pottery Bowl', 'Large decorative bowl with hand-carved patterns. Perfect as a centerpiece or fruit bowl.', 899.00, 8, 'https://ik.imagekit.io/suvam0107/product/DecorativePotteryBowl.jpg', 'ACTIVE'),
(3, 1, 'Handmade Clay Planters Set', 'Pair of 2 terracotta planters in different styles. Ideal for succulents and small plants.', 499.00, 20, 'https://ik.imagekit.io/suvam0107/product/HandmadeClayPlantersSet.jpg', 'ACTIVE'),
(3, 1, 'Ceramic Serving Platter', 'Ceramic serving platter with hand-painted designs. Perfect for special occasions.', 999.00, 12, 'https://ik.imagekit.io/suvam0107/product/CeramicServingPlatter.jpg', 'ACTIVE'),
(1, 1, 'Stoneware Tea Set', 'Complete tea set including 2 teapots, 1 pouring cup and 6 serving cups. Traditional craftsmanship meets modern design.', 2999.00, 6, 'https://ik.imagekit.io/suvam0107/product/StonewareTeaSet.jpg', 'ACTIVE'),
(2, 1, 'Ceramic Candle Holders Pair', 'Pair of elegant ceramic candle holders with reactive glaze finish. Adds warmth to any space.', 399.00, 18, 'https://ik.imagekit.io/suvam0107/product/CeramicCandleHoldersPair.jpg', 'ACTIVE'),

-- Textiles & Fabrics (8 products)
(4, 2, 'Handwoven Cotton Throw Blanket', 'Soft cotton throw blanket with traditional weaving patterns. Perfect for cozy evenings.', 2199.00, 15, 'https://ik.imagekit.io/suvam0107/product/HandwovenCottonThrowBlanket.jpg', 'ACTIVE'),
(4, 2, 'Embroidered Cushion Covers Set', 'Set of 4 cushion covers with intricate hand embroidery. Available in vibrant colors.', 1299.00, 22, 'https://ik.imagekit.io/suvam0107/product/EmbroideredCushionCoversSet.jpg', 'ACTIVE'),
(5, 2, 'Block Print Table Runner', 'Hand block printed table runner with traditional motifs. 100% cotton, natural dyes.', 899.00, 18, 'https://ik.imagekit.io/suvam0107/product/BlockPrintTableRunner.jpg', 'ACTIVE'),
(5, 2, 'Handloom Cotton Saree', 'Pure handloom cotton saree with beautiful border work. Traditional Indian elegance.', 4999.00, 8, 'https://ik.imagekit.io/suvam0107/product/HandloomCottonSaree.jpg', 'ACTIVE'),
(4, 2, 'Tie-Dye Silk Scarf', 'Luxurious silk scarf with hand-dyed tie-dye patterns. Lightweight and versatile.', 1599.00, 14, 'https://ik.imagekit.io/suvam0107/product/TieDyeSilkScarf.jpg', 'ACTIVE'),
(5, 2, 'Woven Wall Tapestry', 'Large woven wall tapestry with geometric patterns. Bohemian home decor statement piece.', 3299.00, 7, 'https://ik.imagekit.io/suvam0107/product/WovenWallTapestry.jpg', 'ACTIVE'),
(4, 2, 'Hand-Stitched Quilt', 'Queen-size hand-stitched quilt with patchwork design. Each piece is unique.', 5999.00, 5, 'https://ik.imagekit.io/suvam0107/product/HandStitchedQuilt.jpg', 'ACTIVE'),
(5, 2, 'Cotton Bath Towel Set', 'Set of 3 premium cotton towels with hand-woven borders. Highly absorbent.', 1899.00, 16, 'https://ik.imagekit.io/suvam0107/product/CottonBathTowelSet.jpg', 'ACTIVE'),

-- Wooden Crafts (8 products)
(6, 3, 'Hand-Carved Wooden Bowl', 'Beautiful hand-carved wooden serving bowl. Perfect for fruits or salads.', 1499.00, 12, 'https://ik.imagekit.io/suvam0107/product/HandCarvedWoodenBowl.jpg', 'ACTIVE'),
(6, 3, 'Wooden Wall Art Sculpture', 'Intricate wooden wall sculpture with 3D carved patterns. Statement piece for any room.', 2999.00, 8, 'https://ik.imagekit.io/suvam0107/product/WoodenWallArtSculpture.jpg', 'ACTIVE'),
(7, 3, 'Handmade Wooden Toys Set', 'Set of 5 eco-friendly wooden toys for children. Non-toxic finish, safe for kids.', 1799.00, 15, 'https://ik.imagekit.io/suvam0107/product/HandmadeWoodenToysSet.jpg', 'ACTIVE'),
(7, 3, 'Carved Wooden Jewelry Box', 'Elegant jewelry box with hand-carved floral designs. Velvet-lined interior.', 2299.00, 10, 'https://ik.imagekit.io/suvam0107/product/CarvedWoodenJewelryBox.jpg', 'ACTIVE'),
(6, 3, 'Wooden Serving Tray', 'Handcrafted wooden serving tray with handles. Perfect for breakfast in bed or entertaining.', 1299.00, 14, 'https://ik.imagekit.io/suvam0107/product/WoodenServingTray.jpg', 'ACTIVE'),
(7, 3, 'Decorative Wooden Elephant', 'Hand-carved wooden elephant figurine. Traditional Indian craftsmanship.', 899.00, 20, 'https://ik.imagekit.io/suvam0107/product/DecorativeWoodenElephant.jpg', 'ACTIVE'),
(6, 3, 'Wooden Photo Frame', 'Handcrafted wooden photo frame with intricate border carvings. Holds 5x7 photo.', 699.00, 18, 'https://ik.imagekit.io/suvam0107/product/WoodenPhotoFrame.jpg', 'ACTIVE'),
(7, 3, 'Wooden Spice Box with Spoons', 'Traditional wooden spice box with 7 compartments and serving spoons. Kitchen essential.', 1599.00, 11, 'https://ik.imagekit.io/suvam0107/product/WoodenSpiceBoxwithSpoons.jpg', 'ACTIVE'),

-- Jewelry & Accessories (8 products)
(8, 4, 'Beaded Statement Necklace', 'Handmade beaded necklace with colorful patterns. Perfect for festivals and parties.', 1199.00, 16, 'https://ik.imagekit.io/suvam0107/product/BeadedStatementNecklace.jpg', 'ACTIVE'),
(8, 4, 'Silver-Plated Jhumka Earrings', 'Traditional jhumka earrings with intricate silver plating. Lightweight and comfortable.', 799.00, 25, 'https://ik.imagekit.io/suvam0107/product/SilverPlatedJhumkaEarrings.jpg', 'ACTIVE'),
(9, 4, 'Handmade Brass Bangles Set', 'Set of 4 brass bangles with hand-etched designs. Adjustable size.', 1499.00, 14, 'https://ik.imagekit.io/suvam0107/product/HandmadeBrassBanglesSet.jpg', 'ACTIVE'),
(9, 4, 'Macrame Bracelet', 'Handwoven macrame bracelet with natural beads. Bohemian style accessory.', 399.00, 30, 'https://ik.imagekit.io/suvam0107/product/MacrameBracelet.jpg', 'ACTIVE'),
(8, 4, 'Oxidized Silver Pendant', 'Handcrafted oxidized silver pendant with traditional motif. Comes with chain.', 2199.00, 12, 'https://ik.imagekit.io/suvam0107/product/OxidizedSilverPendant.jpg', 'ACTIVE'),
(9, 4, 'Terracotta Jewelry Set', 'Complete jewelry set including necklace and earrings. Hand-painted terracotta.', 1299.00, 15, 'https://ik.imagekit.io/suvam0107/product/TerracottaJewelrySet.jpg', 'ACTIVE'),
(8, 4, 'Handmade Hair Accessories', 'Set of decorative hair pins and clips with beadwork. Perfect for special occasions.', 699.00, 22, 'https://ik.imagekit.io/suvam0107/product/HandmadeHairAccessories.jpg', 'ACTIVE'),
(9, 4, 'Leather Cord Necklace', 'Minimalist leather cord necklace with wooden pendant. Unisex design.', 599.00, 20, 'https://ik.imagekit.io/suvam0107/product/LeatherCordNecklace.jpg', 'ACTIVE'),

-- Home Decor (4 products)
(10, 5, 'Brass Hanging Diya Lamp', 'Traditional brass hanging diya with intricate cutwork. Creates beautiful light patterns.', 1599.00, 13, 'https://ik.imagekit.io/suvam0107/product/BrassHangingDiyaLamp.jpg', 'ACTIVE'),
(10, 5, 'Handmade Paper Lanterns Set', 'Set of 3 colorful paper lanterns. Perfect for parties and home decoration.', 899.00, 18, 'https://ik.imagekit.io/suvam0107/product/HandmadePaperLanternsSet.jpg', 'ACTIVE'),
(3, 5, 'Decorative Metal Wall Hanging', 'Artistic metal wall hanging with peacock design. Hand-painted in vibrant colors.', 2499.00, 9, 'https://ik.imagekit.io/suvam0107/product/DecorativeMetalWallHanging.jpg', 'ACTIVE'),
(10, 5, 'Scented Candle Set', 'Set of 4 handmade scented candles in ceramic holders. Natural ingredients.', 1199.00, 16, 'https://ik.imagekit.io/suvam0107/product/ScentedCandleSet.jpg', 'ACTIVE'),

-- Bags & Baskets (4 products)
(3, 6, 'Handwoven Jute Tote Bag', 'Eco-friendly jute tote bag with leather handles. Spacious and durable.', 799.00, 20, 'https://ik.imagekit.io/suvam0107/product/HandwovenJuteToteBag.jpg', 'ACTIVE'),
(5, 6, 'Bamboo Storage Basket Set', 'Set of 3 bamboo baskets in different sizes. Perfect for organizing home essentials.', 1499.00, 14, 'https://ik.imagekit.io/suvam0107/product/BambooStorageBasketSet.jpg', 'ACTIVE'),
(3, 6, 'Cotton Macrame Bag', 'Handmade macrame bag with wooden beads. Boho-chic style.', 1099.00, 17, 'https://ik.imagekit.io/suvam0107/product/CottonMacrameBag.jpg', 'ACTIVE'),
(5, 6, 'Woven Picnic Basket', 'Traditional woven picnic basket with handle and lid. Perfect for outdoor meals.', 1899.00, 10, 'https://ik.imagekit.io/suvam0107/product/WovenPicnicBasket.jpg', 'ACTIVE');

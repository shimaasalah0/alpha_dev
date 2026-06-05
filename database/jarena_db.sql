-- J Arena Football Field Booking System
-- Database setup script

CREATE DATABASE IF NOT EXISTS jarena_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE jarena_db;

-- Drop table if it already exists (for clean re-runs)
DROP TABLE IF EXISTS profiles;

CREATE TABLE profiles (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    full_name  VARCHAR(120) NOT NULL,
    email      VARCHAR(180) NOT NULL,
    phone      VARCHAR(30),
    password   VARCHAR(255) NOT NULL,
    role       ENUM('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Test users (plain-text passwords — swap for hashed in production)
INSERT INTO profiles (full_name, email, phone, password, role, created_at) VALUES
    ('System Admin',   'admin@jarena.com', '+60 11-0000 0001', 'admin123',    'ADMIN',    NOW()),
    ('Ali bin Ahmad',  'ali@email.com',    '+60 12-345 6789',  'customer123', 'CUSTOMER', NOW());

USE jarena_db;

-- Step 1: Drop existing tables (order matters — bookings references fields & profiles)
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS fields;

-- Step 2: fields (BIGINT matches profiles.id)
CREATE TABLE fields (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    name           VARCHAR(100) NOT NULL,
    location       VARCHAR(200),
    price_per_hour DOUBLE       NOT NULL,
    available      BOOLEAN      NOT NULL DEFAULT TRUE,
    image_url      VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Step 3: bookings (all ids BIGINT)
CREATE TABLE bookings (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    user_id      BIGINT       NOT NULL,
    field_id     BIGINT       NOT NULL,
    booking_date DATE         NOT NULL,
    start_time   TIME         NOT NULL,
    end_time     TIME         NOT NULL,
    total_price  DOUBLE       NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_booking_user  FOREIGN KEY (user_id)  REFERENCES profiles(id)  ON DELETE CASCADE,
    CONSTRAINT fk_booking_field FOREIGN KEY (field_id) REFERENCES fields(id)    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Step 4: Seed fields
INSERT INTO fields (name, location, price_per_hour, available) VALUES
    ('Arena A - Premium Field',  'Block A, J Arena', 80.00, 1),
    ('Arena B - Standard Field', 'Block B, J Arena', 50.00, 1),
    ('Arena C - Mini Field',     'Block C, J Arena', 35.00, 1);

-- Step 5: Seed bookings  (user_id 2 = CUSTOMER from jarena_db.sql)
INSERT INTO bookings (user_id, field_id, booking_date, start_time, end_time, total_price, status) VALUES
    (2, 1, '2026-05-19', '08:00:00', '10:00:00', 160.00, 'CONFIRMED'),
    (2, 2, '2026-05-19', '10:00:00', '11:00:00',  50.00, 'PENDING'),
    (2, 2, '2026-05-20', '09:00:00', '11:00:00', 100.00, 'CONFIRMED');

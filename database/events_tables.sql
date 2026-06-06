-- J Arena: Event Promotion Module Tables
-- Run inside jarena_db

CREATE TABLE IF NOT EXISTS events (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    event_date  DATE         NOT NULL,
    location    VARCHAR(200),
    image_url   VARCHAR(255),
    status      VARCHAR(20)  NOT NULL DEFAULT 'UPCOMING',
    created_by  BIGINT       NOT NULL,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_event_creator
        FOREIGN KEY (created_by) REFERENCES profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS event_registrations (
    id            BIGINT   NOT NULL AUTO_INCREMENT,
    event_id      BIGINT   NOT NULL,
    user_id       BIGINT   NOT NULL,
    registered_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_ereg_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_ereg_user  FOREIGN KEY (user_id)  REFERENCES profiles(id) ON DELETE CASCADE,
    UNIQUE KEY uq_event_user (event_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO events (title, description, event_date, location, status, created_by) VALUES
('Ramadan Football Cup',
 'Annual 5-a-side tournament during Ramadan. Open to all teams.',
 '2026-07-15', 'J Arena Main Ground', 'UPCOMING', 1),
('Youth League 2026',
 'Football league open to players under 18. Register your team now.',
 '2026-08-01', 'J Arena Field B', 'UPCOMING', 1),
('Corporate Football Day',
 'Team building football event for corporate teams.',
 '2026-06-28', 'J Arena All Fields', 'UPCOMING', 1),
('Summer Friendly Tournament',
 'Casual summer tournament. All skill levels welcome.',
 '2026-05-10', 'J Arena Field A', 'COMPLETED', 1);

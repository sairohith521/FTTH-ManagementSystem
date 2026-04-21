USE testdb;

DROP TABLE IF EXISTS service_areas;

CREATE TABLE service_areas (
    service_area_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pincode CHAR(6) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

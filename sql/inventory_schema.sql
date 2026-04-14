CREATE DATABASE IF NOT EXISTS testdb;
USE testdb;

DROP TABLE IF EXISTS ports;
DROP TABLE IF EXISTS splitters;
DROP TABLE IF EXISTS olts;
DROP TABLE IF EXISTS service_areas;

CREATE TABLE service_areas (
    service_area_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pincode CHAR(6) NOT NULL UNIQUE,
    area_name VARCHAR(100) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE olts (
    olt_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    olt_code VARCHAR(50) NOT NULL UNIQUE,
    service_area_id BIGINT NOT NULL,
    olt_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_olts_service_area
        FOREIGN KEY (service_area_id) REFERENCES service_areas(service_area_id)
        ON DELETE CASCADE
);

CREATE TABLE splitters (
    splitter_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    olt_id BIGINT NOT NULL,
    splitter_number INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_splitters_olt
        FOREIGN KEY (olt_id) REFERENCES olts(olt_id)
        ON DELETE CASCADE,
    CONSTRAINT uq_splitter_per_olt UNIQUE (olt_id, splitter_number)
);

CREATE TABLE ports (
    port_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    splitter_id BIGINT NOT NULL,
    port_number INT NOT NULL,
    port_status ENUM('AVAILABLE', 'ASSIGNED', 'FAULTY', 'DISABLED') NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ports_splitter
        FOREIGN KEY (splitter_id) REFERENCES splitters(splitter_id)
        ON DELETE CASCADE,
    CONSTRAINT uq_port_per_splitter UNIQUE (splitter_id, port_number)
);

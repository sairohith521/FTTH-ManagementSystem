USE testdb;

DROP TABLE IF EXISTS splitters;

CREATE TABLE splitters (
    splitter_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    olt_id BIGINT,
    splitter_code VARCHAR(50) UNIQUE NOT NULL,
    splitter_number INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

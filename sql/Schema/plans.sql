USE testdb;

DROP TABLE IF EXISTS plans;

CREATE TABLE plans (
    plan_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_code VARCHAR(50) UNIQUE NOT NULL,
    plan_name VARCHAR(100),
    speed_label VARCHAR(50),
    data_limit_label VARCHAR(50),
    ott_count INT,
    monthly_price DECIMAL(10,2),
    olt_type VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

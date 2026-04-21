USE testdb;

DROP TABLE IF EXISTS customer_connections;

CREATE TABLE customer_connections (
    connection_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT,
    plan_id BIGINT,
    port_id BIGINT,
    service_area_id BIGINT,
    connection_status ENUM('ACTIVE','SUSPENDED','DISCONNECTED'),
    activated_on DATE,
    disconnected_on DATE,
    billing_day TINYINT,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL
);

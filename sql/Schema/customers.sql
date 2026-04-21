USE testdb;

DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(150),
    email VARCHAR(150),
    salary DECIMAL(10,2),
    status ENUM('ACTIVE','INACTIVE'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

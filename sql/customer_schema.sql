CREATE DATABASE IF NOT EXISTS testdb;
USE testdb;
CREATE TABLE customers (
    customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_code VARCHAR(50) UNIQUE,
    full_name VARCHAR(100),
    email VARCHAR(100),
    pincode INT,
    salary DOUBLE,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
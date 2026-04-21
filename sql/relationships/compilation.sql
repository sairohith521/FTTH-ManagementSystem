USE testdb;

SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- DROP TABLES (CHILD → PARENT)
-- =========================
DROP TABLE IF EXISTS email_logs;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS customer_connections;
DROP TABLE IF EXISTS ports;
DROP TABLE IF EXISTS splitters;
DROP TABLE IF EXISTS olts;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS plans;
DROP TABLE IF EXISTS service_areas;
DROP TABLE IF EXISTS roles;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- CREATE TABLES (PARENT → CHILD)
-- =========================

CREATE TABLE roles (
    role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE service_areas (
    service_area_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pincode CHAR(6) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE olts (
    olt_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    olt_code VARCHAR(50) UNIQUE NOT NULL,
    service_area_id BIGINT,
    olt_type VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE splitters (
    splitter_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    olt_id BIGINT,
    splitter_code VARCHAR(50) UNIQUE NOT NULL,
    splitter_number INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ports (
    port_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    splitter_id BIGINT,
    port_number INT,
    port_status ENUM('FREE', 'USED', 'FAULTY') DEFAULT 'FREE'
);

CREATE TABLE customers (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(150),
    email VARCHAR(150),
    salary DECIMAL(10,2),
    status ENUM('ACTIVE', 'INACTIVE'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_connections (
    connection_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT,
    plan_id BIGINT,
    port_id BIGINT,
    service_area_id BIGINT,
    connection_status ENUM('ACTIVE', 'SUSPENDED', 'DISCONNECTED'),
    activated_on DATE,
    disconnected_on DATE,
    billing_day TINYINT,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL
);

CREATE TABLE bills (
    bill_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bill_no VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT,
    connection_id BIGINT,
    bill_date DATE,
    due_date DATE,
    plan_charge DECIMAL(10,2),
    gst_amount DECIMAL(10,2),
    total_amount DECIMAL(10,2),
    bill_status ENUM('PENDING', 'PAID', 'OVERDUE'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE email_logs (
    email_log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT,
    email_type ENUM('BILL', 'REMINDER', 'WELCOME'),
    recipient_email VARCHAR(150),
    subject VARCHAR(255),
    sent_status ENUM('SUCCESS', 'FAILED'),
    provider_response TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- FOREIGN KEYS (AFTER ALL TABLES)
-- =========================

ALTER TABLE users
ADD CONSTRAINT fk_users_role
FOREIGN KEY (role_id) REFERENCES roles(role_id);

ALTER TABLE olts
ADD CONSTRAINT fk_olts_service_area
FOREIGN KEY (service_area_id) REFERENCES service_areas(service_area_id);

ALTER TABLE splitters
ADD CONSTRAINT fk_splitters_olt
FOREIGN KEY (olt_id) REFERENCES olts(olt_id);

ALTER TABLE ports
ADD CONSTRAINT fk_ports_splitter
FOREIGN KEY (splitter_id) REFERENCES splitters(splitter_id);

ALTER TABLE customer_connections
ADD CONSTRAINT fk_cc_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
ADD CONSTRAINT fk_cc_plan FOREIGN KEY (plan_id) REFERENCES plans(plan_id),
ADD CONSTRAINT fk_cc_port FOREIGN KEY (port_id) REFERENCES ports(port_id),
ADD CONSTRAINT fk_cc_service_area FOREIGN KEY (service_area_id) REFERENCES service_areas(service_area_id),
ADD CONSTRAINT fk_cc_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
ADD CONSTRAINT fk_cc_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id);

ALTER TABLE bills
ADD CONSTRAINT fk_bills_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
ADD CONSTRAINT fk_bills_connection FOREIGN KEY (connection_id) REFERENCES customer_connections(connection_id);

ALTER TABLE email_logs
ADD CONSTRAINT fk_email_customer
FOREIGN KEY (customer_id) REFERENCES customers(customer_id);

-- =========================
-- SEED DATA (CORRECT ORDER)
-- =========================

INSERT INTO roles (role_code) VALUES
('ADMIN'),
('STAFF'),
('TECHNICIAN');

INSERT INTO service_areas (pincode) VALUES
('201301'),
('201302');

INSERT INTO plans
(plan_code, plan_name, speed_label, data_limit_label, ott_count, monthly_price, olt_type)
VALUES
('BASIC_50', 'Basic 50 Mbps', '50 Mbps', 'Unlimited', 1, 499.00, 'GPON'),
('PREMIUM_100', 'Premium 100 Mbps', '100 Mbps', 'Unlimited', 3, 799.00, 'GPON');

INSERT INTO users (username, password_hash, role_id) VALUES
('admin', 'admin_hash', 1),
('staff', 'staff_hash', 2);

INSERT INTO olts (olt_code, service_area_id, olt_type) VALUES
('OLT001', 1, 'GPON');

INSERT INTO splitters (olt_id, splitter_code, splitter_number) VALUES
(1, 'SPL001', 1);

INSERT INTO ports (splitter_id, port_number) VALUES
(1, 1),
(1, 2);

INSERT INTO customers
(customer_code, full_name, email, salary, status)
VALUES
('CUST001', 'Rohith Ganthi', 'rohith@example.com', 50000, 'ACTIVE');

INSERT INTO customer_connections
(customer_id, plan_id, port_id, service_area_id, connection_status, activated_on, billing_day, created_by)
VALUES
(1, 1, 1, 1, 'ACTIVE', CURRENT_DATE, 5, 1);

INSERT INTO bills
(bill_no, customer_id, connection_id, bill_date, due_date, plan_charge, gst_amount, total_amount, bill_status)
VALUES
('BILL001', 1, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL 10 DAY, 499.00, 89.82, 588.82, 'PENDING');

INSERT INTO email_logs
(customer_id, email_type, recipient_email, subject, sent_status)
VALUES
(1, 'WELCOME', 'rohith@example.com', 'Welcome to ISP', 'SUCCESS');
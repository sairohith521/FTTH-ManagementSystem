USE testdb;

DROP TABLE IF EXISTS bills;

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
    bill_status ENUM('PENDING','PAID','OVERDUE'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

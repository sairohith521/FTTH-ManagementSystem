USE testdb;

DROP TABLE IF EXISTS email_logs;

CREATE TABLE email_logs (
    email_log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT,
    email_type ENUM('BILL','REMINDER','WELCOME'),
    recipient_email VARCHAR(150),
    subject VARCHAR(255),
    sent_status ENUM('SUCCESS','FAILED'),
    provider_response TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

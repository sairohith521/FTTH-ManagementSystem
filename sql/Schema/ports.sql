USE testdb;

DROP TABLE IF EXISTS ports;

CREATE TABLE ports (
    port_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    splitter_id BIGINT,
    port_number INT,
    port_status ENUM('FREE','USED','FAULTY') DEFAULT 'FREE'
);

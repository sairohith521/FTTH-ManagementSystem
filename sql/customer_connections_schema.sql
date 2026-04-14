USE testdb;

-- ============================================================
-- customer_connections table
-- ============================================================
CREATE TABLE IF NOT EXISTS customer_connections (
    connection_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_name    VARCHAR(100) NOT NULL,
    plan_id          BIGINT NOT NULL,
    port_id          BIGINT NOT NULL,
    service_area_id  BIGINT NOT NULL,
    connection_status ENUM('ACTIVE','DISCONNECTED') NOT NULL DEFAULT 'ACTIVE',
    activated_on     DATE NOT NULL,
    disconnected_on  DATE NULL,
    billing_day      TINYINT NOT NULL DEFAULT 10,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_cc_plan FOREIGN KEY (plan_id)         REFERENCES plan_admin(plan_id),
    CONSTRAINT fk_cc_port FOREIGN KEY (port_id)         REFERENCES ports(port_id),
    CONSTRAINT fk_cc_sa   FOREIGN KEY (service_area_id) REFERENCES service_areas(service_area_id)
);

-- ============================================================
-- Fake customer data (2 customers on the 2 ASSIGNED ports)
-- ============================================================

-- Customer 1: Ravi on Silver plan, port in OLT500-562101-2, splitter 1, port 1
INSERT INTO customer_connections (customer_name, plan_id, port_id, service_area_id, connection_status, activated_on, billing_day)
SELECT
    'Ravi Kumar',
    pa.plan_id,
    p.port_id,
    sa.service_area_id,
    'ACTIVE',
    '2025-01-15',
    10
FROM ports p
JOIN splitters s ON s.splitter_id = p.splitter_id
JOIN olts o ON o.olt_id = s.olt_id
JOIN service_areas sa ON sa.service_area_id = o.service_area_id
CROSS JOIN plan_admin pa
WHERE o.olt_code = 'OLT500-562101-2'
  AND s.splitter_number = 1
  AND p.port_number = 1
  AND pa.plan_code = 'Silver'
LIMIT 1;

-- Customer 2: Priya on Platinum plan, port in OLT500-501301-3, splitter 1, port 1
INSERT INTO customer_connections (customer_name, plan_id, port_id, service_area_id, connection_status, activated_on, billing_day)
SELECT
    'Priya Sharma',
    pa.plan_id,
    p.port_id,
    sa.service_area_id,
    'ACTIVE',
    '2025-02-20',
    10
FROM ports p
JOIN splitters s ON s.splitter_id = p.splitter_id
JOIN olts o ON o.olt_id = s.olt_id
JOIN service_areas sa ON sa.service_area_id = o.service_area_id
CROSS JOIN plan_admin pa
WHERE o.olt_code = 'OLT500-501301-3'
  AND s.splitter_number = 1
  AND p.port_number = 1
  AND pa.plan_code = 'Platinum'
LIMIT 1;

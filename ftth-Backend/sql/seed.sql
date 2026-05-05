-- ============================================================
-- Aaha Telecom FTTH — Seed Data
-- Run AFTER schema.sql
-- ============================================================

USE testdb;

-- ============================================================
-- 1. roles
-- ============================================================

INSERT INTO roles (role_code) VALUES
('ADMIN'),
('CSR'),
('MAINT');

-- ============================================================
-- 2. users
-- ============================================================

INSERT INTO users (username, password_hash, role_id, is_active) VALUES
('admin',   'admin123', 1, TRUE),
('csr1',    'csr123',   2, TRUE),
('maint1',  'maint123', 3, TRUE),
('testuser','test123',  2, TRUE);

-- ============================================================
-- 3. service_areas
-- ============================================================

INSERT INTO service_areas (pincode, is_active) VALUES
('401301', TRUE),
('501221', TRUE),
('501301', TRUE),
('562101', TRUE);

-- ============================================================
-- 4. olts
-- ============================================================

INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT500-401301-1', service_area_id, 'OLT500', TRUE
FROM service_areas WHERE pincode = '401301';

INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT500-501221-1', service_area_id, 'OLT500', TRUE
FROM service_areas WHERE pincode = '501221';

INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT300-501301-1', service_area_id, 'OLT300', TRUE
FROM service_areas WHERE pincode = '501301';

INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT500-501301-3', service_area_id, 'OLT500', TRUE
FROM service_areas WHERE pincode = '501301';

INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT300-562101-1', service_area_id, 'OLT300', TRUE
FROM service_areas WHERE pincode = '562101';

INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT500-562101-2', service_area_id, 'OLT500', TRUE
FROM service_areas WHERE pincode = '562101';

-- ============================================================
-- 5. splitters
-- ============================================================

-- OLT500-401301-1
INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL1'), 1, TRUE FROM olts WHERE olt_code = 'OLT500-401301-1';

INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL2'), 2, TRUE FROM olts WHERE olt_code = 'OLT500-401301-1';

-- OLT500-501221-1
INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL1'), 1, TRUE FROM olts WHERE olt_code = 'OLT500-501221-1';

INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL2'), 2, TRUE FROM olts WHERE olt_code = 'OLT500-501221-1';

INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL3'), 3, TRUE FROM olts WHERE olt_code = 'OLT500-501221-1';

-- OLT300-501301-1
INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL1'), 1, TRUE FROM olts WHERE olt_code = 'OLT300-501301-1';

INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL2'), 2, TRUE FROM olts WHERE olt_code = 'OLT300-501301-1';

INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL3'), 3, TRUE FROM olts WHERE olt_code = 'OLT300-501301-1';

-- OLT500-501301-3
INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL1'), 1, TRUE FROM olts WHERE olt_code = 'OLT500-501301-3';

INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL2'), 2, TRUE FROM olts WHERE olt_code = 'OLT500-501301-3';

INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL3'), 3, TRUE FROM olts WHERE olt_code = 'OLT500-501301-3';

-- OLT300-562101-1
INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL1'), 1, TRUE FROM olts WHERE olt_code = 'OLT300-562101-1';

INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL2'), 2, TRUE FROM olts WHERE olt_code = 'OLT300-562101-1';

-- OLT500-562101-2
INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL1'), 1, TRUE FROM olts WHERE olt_code = 'OLT500-562101-2';

INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL2'), 2, TRUE FROM olts WHERE olt_code = 'OLT500-562101-2';

INSERT INTO splitters (olt_id, splitter_code, splitter_number, is_active)
SELECT olt_id, CONCAT(olt_code, '-SPL3'), 3, TRUE FROM olts WHERE olt_code = 'OLT500-562101-2';

-- ============================================================
-- 6. ports (3 ports per splitter)
-- ============================================================

INSERT INTO ports (splitter_id, port_number, port_status)
SELECT splitter_id, 1, 'AVAILABLE' FROM splitters;

INSERT INTO ports (splitter_id, port_number, port_status)
SELECT splitter_id, 2, 'AVAILABLE' FROM splitters;

INSERT INTO ports (splitter_id, port_number, port_status)
SELECT splitter_id, 3, 'AVAILABLE' FROM splitters;

-- Mark some ports as ASSIGNED
UPDATE ports p
JOIN splitters s ON s.splitter_id = p.splitter_id
JOIN olts o ON o.olt_id = s.olt_id
SET p.port_status = 'ASSIGNED'
WHERE o.olt_code IN ('OLT500-562101-2', 'OLT500-501301-3')
  AND s.splitter_number = 1
  AND p.port_number = 1;

-- ============================================================
-- 7. plans
-- ============================================================

INSERT INTO plans (
    plan_code, plan_name, speed_label, data_limit_label,
    ott_count, monthly_price, olt_type, is_active
) VALUES
('SILVER',   'Silver',   '300MBPS',  '60GB/Month',          2,  499.00, 'OLT300', TRUE),
('GOLD',     'Gold',     '500MBPS',  'Unlimited Internet',  4,  999.00, 'OLT500', TRUE),
('PLATINUM', 'Platinum', '1000MBPS', 'Unlimited Internet',  6, 1499.00, 'OLT500', TRUE);

-- ============================================================
-- 8. customers
-- ============================================================

INSERT INTO customers (customer_code, full_name, email, salary, status) VALUES
('AAHA-0001', 'Ravi Kumar',   'ravi@aaha.demo',   45000.00, 'ACTIVE'),
('AAHA-0002', 'Priya Sharma', 'priya@aaha.demo',  52000.00, 'ACTIVE');

-- ============================================================
-- 9. customer_connections
-- ============================================================

INSERT INTO customer_connections (
    customer_id, plan_id, port_id, service_area_id,
    connection_status, activated_on, billing_day
)
SELECT
    c.customer_id,
    pl.plan_id,
    p.port_id,
    sa.service_area_id,
    'ACTIVE',
    '2025-01-15',
    10
FROM customers c
JOIN plans pl ON pl.plan_code = 'SILVER'
JOIN ports p ON p.port_id = (
    SELECT p2.port_id
    FROM ports p2
    JOIN splitters s2 ON s2.splitter_id = p2.splitter_id
    JOIN olts o2 ON o2.olt_id = s2.olt_id
    WHERE o2.olt_code = 'OLT500-562101-2'
      AND s2.splitter_number = 1
      AND p2.port_number = 1
    LIMIT 1
)
JOIN service_areas sa ON sa.pincode = '562101'
WHERE c.customer_code = 'AAHA-0001'
LIMIT 1;

INSERT INTO customer_connections (
    customer_id, plan_id, port_id, service_area_id,
    connection_status, activated_on, billing_day
)
SELECT
    c.customer_id,
    pl.plan_id,
    p.port_id,
    sa.service_area_id,
    'ACTIVE',
    '2025-02-20',
    10
FROM customers c
JOIN plans pl ON pl.plan_code = 'PLATINUM'
JOIN ports p ON p.port_id = (
    SELECT p2.port_id
    FROM ports p2
    JOIN splitters s2 ON s2.splitter_id = p2.splitter_id
    JOIN olts o2 ON o2.olt_id = s2.olt_id
    WHERE o2.olt_code = 'OLT500-501301-3'
      AND s2.splitter_number = 1
      AND p2.port_number = 1
    LIMIT 1
)
JOIN service_areas sa ON sa.pincode = '501301'
WHERE c.customer_code = 'AAHA-0002'
LIMIT 1;
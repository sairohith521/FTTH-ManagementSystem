USE testdb;

-- Ravi Kumar: plan P1 (Silver), port 14 (OLT500-562101-2, splitter 1, port 1), service_area 4 (562101)
INSERT INTO customer_connections (customer_name, plan_id, port_id, service_area_id, connection_status, activated_on, billing_day)
VALUES ('Ravi Kumar', 1, 14, 4, 'ACTIVE', '2025-01-15', 10);

-- Priya Sharma: plan P2 (Platinum), port 9 (OLT500-501301-3, splitter 1, port 1), service_area 3 (501301)
INSERT INTO customer_connections (customer_name, plan_id, port_id, service_area_id, connection_status, activated_on, billing_day)
VALUES ('Priya Sharma', 2, 9, 3, 'ACTIVE', '2025-02-20', 10);

USE testdb;

INSERT INTO customer_connections (customer_id, plan_id, port_id, service_area_id, connection_status, activated_on, billing_day, created_by) VALUES
(1, 1, 1, 1, 'ACTIVE', CURRENT_DATE, 5, 1);

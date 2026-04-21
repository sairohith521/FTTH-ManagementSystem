USE testdb;

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

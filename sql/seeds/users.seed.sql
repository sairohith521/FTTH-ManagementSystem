USE testdb;

INSERT INTO users (username, password_hash, role_id) VALUES
('admin', 'admin_hash', 1),
('staff1', 'staff_hash', 2);

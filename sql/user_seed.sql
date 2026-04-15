USE testdb;
INSERT INTO user (username, password_hash, role_id, is_active)
VALUES
('admin', 'admin123', 1, TRUE),
('csr1', 'csr123', 2, TRUE),
('maint1', 'maint123', 3, TRUE),
('testuser', 'test123', 2, TRUE);
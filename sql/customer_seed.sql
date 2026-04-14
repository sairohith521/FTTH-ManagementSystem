USE testdb;

INSERT INTO customers (
    customer_code,
    full_name,
    email,
    pincode,
    salary,
    status
) VALUES
('AAHA-0001', 'Aashish', 'aashish@aaha.demo', 560002, 42000.00, 'DELETED'),
('AAHA-0002', 'Anvitha', 'anvitha@aaha.demo', 560002, 48000.00, 'DELETED'),
('AAHA-0003', 'Customer 3', 'customer3@aaha.demo', 110001, 35000.00, 'DELETED'),
('AAHA-0004', 'Customer 4', 'customer4@aaha.demo', 110001, 36000.00, 'DELETED'),
('AAHA-0008', 'PJ', 'pj@aaha.demo', 560002, 50000.00, 'ACTIVE');

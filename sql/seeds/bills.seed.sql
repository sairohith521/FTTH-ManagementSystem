USE testdb;

INSERT INTO bills (bill_no, customer_id, connection_id, bill_date, due_date, plan_charge, gst_amount, total_amount, bill_status) VALUES
('BILL001', 1, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL 10 DAY, 499.00, 89.82, 588.82, 'PENDING');

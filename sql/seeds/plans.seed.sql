USE testdb;

INSERT INTO plans (plan_code, plan_name, speed_label, data_limit_label, ott_count, monthly_price, olt_type) VALUES
('BASIC_50', 'Basic Plan', '50 Mbps', 'Unlimited', 1, 499.00, 'OLT300'),
('PREMIUM_100', 'Premium Plan', '100 Mbps', 'Unlimited', 3, 799.00, 'OLT500');

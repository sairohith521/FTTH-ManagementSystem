USE testdb;

INSERT INTO plan_admin (
    plan_code,
    plan_name,
    speed,
    data_limit,
    ott_count,
    price,
    olt_type,
    is_active
) VALUES
('1', 'Silver', '2GBPS', 'Unlimited Internet', 3, 499.00, 'OLT300', FALSE),
('2', 'Platinum', '35Gbps', 'Unlimited Internet', 4, 899.00, 'OLT300', FALSE);

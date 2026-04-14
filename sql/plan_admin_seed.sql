USE testdb;

INSERT INTO plan_admin (
    plan_name,
    speed,
    data_limit,
    ott_count,
    price,
    olt_type,
    is_active
) VALUES
('Silver', '2 MBPS', 'Unlimited Internet', 3, 499.00, 'OLT300', FALSE),
('Platinum', '35 MBPS', 'Unlimited Internet', 4, 899.00, 'OLT300', FALSE);

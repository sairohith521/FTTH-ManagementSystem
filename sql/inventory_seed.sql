USE testdb;

INSERT INTO service_areas (pincode, area_name, is_active) VALUES
('401301', 'Service Area 401301', TRUE),
('501221', 'Service Area 501221', TRUE),
('501301', 'Service Area 501301', TRUE),
('562101', 'Service Area 562101', TRUE);

INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT500-401301-1', service_area_id, 'OLT500', TRUE FROM service_areas WHERE pincode = '401301';
INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT500-501221-1', service_area_id, 'OLT500', TRUE FROM service_areas WHERE pincode = '501221';
INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT300-501301-1', service_area_id, 'OLT300', TRUE FROM service_areas WHERE pincode = '501301';
INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT500-501301-3', service_area_id, 'OLT500', TRUE FROM service_areas WHERE pincode = '501301';
INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT300-562101-1', service_area_id, 'OLT300', TRUE FROM service_areas WHERE pincode = '562101';
INSERT INTO olts (olt_code, service_area_id, olt_type, is_active)
SELECT 'OLT500-562101-2', service_area_id, 'OLT500', TRUE FROM service_areas WHERE pincode = '562101';

INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 1, TRUE FROM olts WHERE olt_code = 'OLT500-401301-1';
INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 2, TRUE FROM olts WHERE olt_code = 'OLT500-401301-1';

INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 1, TRUE FROM olts WHERE olt_code = 'OLT500-501221-1';
INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 2, TRUE FROM olts WHERE olt_code = 'OLT500-501221-1';
INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 3, TRUE FROM olts WHERE olt_code = 'OLT500-501221-1';

INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 1, TRUE FROM olts WHERE olt_code = 'OLT300-501301-1';
INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 2, TRUE FROM olts WHERE olt_code = 'OLT300-501301-1';
INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 3, TRUE FROM olts WHERE olt_code = 'OLT300-501301-1';

INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 1, TRUE FROM olts WHERE olt_code = 'OLT500-501301-3';
INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 2, TRUE FROM olts WHERE olt_code = 'OLT500-501301-3';
INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 3, TRUE FROM olts WHERE olt_code = 'OLT500-501301-3';

INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 1, TRUE FROM olts WHERE olt_code = 'OLT300-562101-1';
INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 2, TRUE FROM olts WHERE olt_code = 'OLT300-562101-1';

INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 1, TRUE FROM olts WHERE olt_code = 'OLT500-562101-2';
INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 2, TRUE FROM olts WHERE olt_code = 'OLT500-562101-2';
INSERT INTO splitters (olt_id, splitter_number, is_active)
SELECT olt_id, 3, TRUE FROM olts WHERE olt_code = 'OLT500-562101-2';

INSERT INTO ports (splitter_id, port_number, port_status)
SELECT splitter_id, 1,
       CASE WHEN splitter_number = 1 THEN 'AVAILABLE' ELSE 'AVAILABLE' END
FROM splitters;
INSERT INTO ports (splitter_id, port_number, port_status)
SELECT splitter_id, 2, 'AVAILABLE' FROM splitters;
INSERT INTO ports (splitter_id, port_number, port_status)
SELECT splitter_id, 3, 'AVAILABLE' FROM splitters;

UPDATE ports p
JOIN splitters s ON s.splitter_id = p.splitter_id
JOIN olts o ON o.olt_id = s.olt_id
SET p.port_status = 'ASSIGNED'
WHERE o.olt_code IN ('OLT500-562101-2', 'OLT500-501301-3')
  AND s.splitter_number = 1
  AND p.port_number = 1;

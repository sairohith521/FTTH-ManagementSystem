USE testdb;

-- Migration: plan_admin.plan_id -> BIGINT PK, plan_code -> VARCHAR UNIQUE
-- This migration preserves existing rows and moves old plan_id values into plan_code.

ALTER TABLE plan_admin
    ADD COLUMN new_plan_id BIGINT NOT NULL AUTO_INCREMENT,
    ADD UNIQUE KEY uk_plan_admin_new_plan_id (new_plan_id);

-- If plan_code was blank/null in older data, backfill from old plan_id before dropping old column.
UPDATE plan_admin
SET plan_code = CAST(plan_id AS CHAR)
WHERE plan_code IS NULL OR TRIM(plan_code) = '';

ALTER TABLE plan_admin
    DROP PRIMARY KEY,
    DROP COLUMN plan_id,
    CHANGE COLUMN new_plan_id plan_id BIGINT NOT NULL AUTO_INCREMENT,
    ADD PRIMARY KEY (plan_id);

ALTER TABLE plan_admin
    MODIFY COLUMN plan_code VARCHAR(30) NOT NULL;

-- Ensure business code uniqueness.
ALTER TABLE plan_admin
    ADD CONSTRAINT uk_plan_admin_plan_code UNIQUE (plan_code);

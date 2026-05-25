ALTER TABLE anniversary
    ADD COLUMN calendar_type VARCHAR(16) NOT NULL DEFAULT 'SOLAR' COMMENT '日期类型 SOLAR/LUNAR' AFTER type,
    ADD COLUMN lunar_year INT DEFAULT NULL COMMENT '农历年' AFTER anniversary_date,
    ADD COLUMN lunar_month INT DEFAULT NULL COMMENT '农历月' AFTER lunar_year,
    ADD COLUMN lunar_day INT DEFAULT NULL COMMENT '农历日' AFTER lunar_month,
    ADD COLUMN lunar_leap_month TINYINT(1) DEFAULT NULL COMMENT '是否闰月' AFTER lunar_day;

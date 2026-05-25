SET @dbname = (SELECT DATABASE());
SET @tablename = 'WORKER';
SET @columnname = 'hourly_pay';

SELECT COUNT(*) INTO @exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'agrosys_worker'
  AND TABLE_NAME = @tablename
  AND COLUMN_NAME = @columnname;

SET @query = IF(@exists = 0,
    'ALTER TABLE agrosys_worker.WORKER ADD COLUMN hourly_pay DECIMAL(10,2) NOT NULL DEFAULT 0.00',
    'SELECT 1 AS dummy');

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE agrosys_worker.WORKER SET hourly_pay = ROUND(salary/56, 2);

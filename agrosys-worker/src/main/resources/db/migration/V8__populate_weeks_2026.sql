-- Generate 52 weeks for 2026 starting from the first Monday (2026-01-05)
INSERT INTO agrosys_worker.week (start_date, end_date, name)
WITH RECURSIVE weeks AS (
    SELECT 1 AS n, DATE '2026-01-05' AS start_date
    UNION ALL
    SELECT n + 1, DATE_ADD(start_date, INTERVAL 7 DAY)
    FROM weeks
    WHERE n < 52
)
SELECT
    start_date,
    DATE_ADD(start_date, INTERVAL 6 DAY) AS end_date,
    CONCAT('Semana ', LPAD(n, 2, '0')) AS name
FROM weeks;

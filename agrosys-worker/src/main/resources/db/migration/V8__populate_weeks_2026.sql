INSERT INTO agrosys_worker.week (week_id, start_date, end_date, name)
WITH RECURSIVE weeks AS (
    SELECT 1 AS n, DATE('2026-01-05') AS start_date
    UNION ALL
    SELECT n + 1, DATE_ADD(start_date, INTERVAL 7 DAY)
    FROM weeks WHERE n < 52
)
SELECT
    CONCAT('2026-W', LPAD(n, 2, '0')),
    start_date,
    DATE_ADD(start_date, INTERVAL 6 DAY),
    CONCAT('Semana ', n)
FROM weeks;

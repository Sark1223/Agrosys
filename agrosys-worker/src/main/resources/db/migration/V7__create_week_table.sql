CREATE TABLE IF NOT EXISTS agrosys_worker.week (
    week_id VARCHAR(10) NOT NULL PRIMARY KEY,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

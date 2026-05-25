DROP TABLE IF EXISTS agrosys_worker.weekly_pay;
DROP TABLE IF EXISTS agrosys_worker.week;

CREATE TABLE agrosys_worker.week (
    week_id INT NOT NULL AUTO_INCREMENT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    name VARCHAR(20) NOT NULL,
    PRIMARY KEY (week_id),
    UNIQUE KEY start_date_UNIQUE (start_date),
    UNIQUE KEY end_date_UNIQUE (end_date),
    UNIQUE KEY name_UNIQUE (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

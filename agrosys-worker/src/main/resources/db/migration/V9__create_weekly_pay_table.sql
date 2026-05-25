DROP TABLE IF EXISTS agrosys_worker.weekly_pay;

CREATE TABLE agrosys_worker.weekly_pay (
    worker_id INT NOT NULL,
    week_id INT NOT NULL,
    amount DECIMAL(10,2) NULL,
    PRIMARY KEY (worker_id, week_id),
    CONSTRAINT fk_weekly_pay_worker
        FOREIGN KEY (worker_id)
        REFERENCES agrosys_worker.WORKER (workerId)
        ON DELETE CASCADE,
    CONSTRAINT fk_weekly_pay_week
        FOREIGN KEY (week_id)
        REFERENCES agrosys_worker.week (week_id)
        ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

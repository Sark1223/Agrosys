CREATE TABLE IF NOT EXISTS agrosys_worker.weekly_pay (
    worker_id INT NOT NULL,
    week_id VARCHAR(10) NOT NULL,
    amount DECIMAL(18,2) NULL,
    PRIMARY KEY (worker_id, week_id),
    CONSTRAINT FK_WEEKLY_PAY_WORKER
        FOREIGN KEY (worker_id)
        REFERENCES agrosys_worker.WORKER (workerId)
        ON DELETE CASCADE
        ON UPDATE NO ACTION,
    CONSTRAINT FK_WEEKLY_PAY_WEEK
        FOREIGN KEY (week_id)
        REFERENCES agrosys_worker.week (week_id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

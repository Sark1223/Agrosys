CREATE TABLE IF NOT EXISTS agrosys_task.SPECIAL_TASK (
    task_id INT NOT NULL,
    worker_id INT NOT NULL,
    payment_amount DECIMAL(18,2),
    PRIMARY KEY (task_id),
    FOREIGN KEY (task_id) REFERENCES agrosys_task.TASK(task_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

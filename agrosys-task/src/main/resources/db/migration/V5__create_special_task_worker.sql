
CREATE TABLE IF NOT EXISTS agrosys_task.WORKER_TASK(
    worker_task_id INT NOT NULL AUTO_INCREMENT,
    worker_id INT NOT NULL,
    special_task_id INT NOT NULL,
    PRIMARY KEY (worker_task_id),
    UNIQUE KEY uk_worker_special (worker_id, special_task_id),
    FOREIGN KEY (worker_id) REFERENCES agrosys_worker.WORKER(workerId) ON DELETE CASCADE,
    FOREIGN KEY (special_task_id) REFERENCES agrosys_task.SPECIAL_TASK(special_task_id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
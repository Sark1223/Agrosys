DROP TABLE IF EXISTS agrosys_task.WORKER_TASK;
DROP TABLE IF EXISTS agrosys_task.SPECIAL_TASK;

CREATE TABLE agrosys_task.SPECIAL_TASK (
    special_task_id  INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    payment_amount DECIMAL(18,2) NOT NULL,
    create_at DATE NOT NULL,
    task_stage_id INT NOT NULL,
    end_at DATE NULL,
    description VARCHAR(256) NULL,
    PRIMARY KEY (special_task_id)
) ENGINE= InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE agrosys_task.WORKER_TASK(
    worker_task_id INT NOT NULL AUTO_INCREMENT,
    worker_id INT NOT NULL,
    special_task_id INT NOT NULL,
    PRIMARY KEY (worker_task_id),
    UNIQUE KEY uk_worker_special (worker_id, special_task_id),
    FOREIGN KEY (worker_id) REFERENCES agrosys_worker.WORKER(worker_id) ON DELETE CASCADE,
    FOREIGN KEY (special_task_id) REFERENCES agrosys_task.SPECIAL_TASK(special_task_id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
    
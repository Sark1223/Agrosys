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

-- WORKER_TASK is created in V5
    
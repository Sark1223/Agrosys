CREATE SCHEMA IF NOT EXISTS agrosys_task;

CREATE TABLE IF NOT EXISTS agrosys_task.TASK (
    task_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(256),
    create_at DATE NOT NULL,
    end_at DATE,
    task_stage_id INT,
    plantation_id INT NOT NULL,
    PRIMARY KEY (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
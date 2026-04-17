CREATE SCHEMA IF NOT EXISTS agrosys_worker;

CREATE TABLE
    IF NOT EXISTS agrosys_worker.WORKER (
        workerId INT NOT NULL AUTO_INCREMENT,
        name VARCHAR(50) NOT NULL,
        notas VARCHAR(255) NOT NULL,
        PRIMARY KEY (workerId),
        UNIQUE KEY name_UNIQUE (name)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
CREATE TABLE
    IF NOT EXISTS plot (
        plotId int NOT NULL AUTO_INCREMENT,
        name varchar(100) NOT NULL,
        description varchar(255) DEFAULT NULL,
        PRIMARY KEY (plotId),
        UNIQUE KEY name_UNIQUE (name)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
    
CREATE TABLE
    IF NOT EXISTS plantatio_stage (
        stageId int NOT NULL,
        name varchar(50) NOT NULL,
        PRIMARY KEY (stageId),
        UNIQUE KEY name_UNIQUE (name)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
    IF NOT EXISTS plantatio (
        plantatioId int NOT NULL AUTO_INCREMENT,
        name varchar(100) NOT NULL,
        start_at date NOT NULL,
        end_at date DEFAULT NULL,
        notas varchar(500) DEFAULT NULL,
        plotId int NOT NULL,
        PRIMARY KEY (plantatioId),
        KEY FK_platatio_plot_idx (plotId),
        CONSTRAINT FK_platatio_plot FOREIGN KEY (plotId) REFERENCES plot (plotId)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
    IF NOT EXISTS plantatio_stage_relation (
        plantatioId int NOT NULL,
        stageId int NOT NULL,
        start_at date NOT NULL,
        end_at date DEFAULT NULL,
        notes varchar(500) DEFAULT NULL,
        PRIMARY KEY (plantatioId, stageId),
        KEY FK_plantatio_stage_relation_stage_idx (stageId),
        CONSTRAINT FK_plantatio_stage_relation_plantatio FOREIGN KEY (plantatioId) REFERENCES plantatio (plantatioId),
        CONSTRAINT FK_plantatio_stage_relation_stage FOREIGN KEY (stageId) REFERENCES plantatio_stage (stageId)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
CREATE TABLE
    IF NOT EXISTS LOT (
        lotId int NOT NULL AUTO_INCREMENT,
        name varchar(100) NOT NULL,
        plantationId int NOT NULL,
        description varchar(256) DEFAULT NULL,
        PRIMARY KEY (lotId),
        KEY FK_plantatio_lot_idx (plantationId),
        CONSTRAINT FK_plantatio_lot FOREIGN KEY (plantationId) REFERENCES plantatio (plantatioId)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
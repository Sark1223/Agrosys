CREATE TABLE
    IF NOT EXISTS PRODUCT_TYPE (
        typeId int NOT NULL AUTO_INCREMENT,
        nombre varchar(45) NOT NULL,
        active tinyint NOT NULL,
        PRIMARY KEY (typeId)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
    IF NOT EXISTS PRODUCT_UNIT (
        unitId int NOT NULL AUTO_INCREMENT,
        nombre varchar(45) NOT NULL,
        active tinyint NOT NULL,
        PRIMARY KEY (unitId)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
    IF NOT EXISTS TRADE_MODE (
        tradeId int NOT NULL AUTO_INCREMENT,
        nombre varchar(45) NOT NULL,
        active tinyint NOT NULL,
        PRIMARY KEY (tradeId)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci; 
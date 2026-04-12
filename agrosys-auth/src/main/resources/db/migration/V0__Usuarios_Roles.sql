CREATE TABLE
    IF NOT EXISTS ROL (
        rolId int unsigned NOT NULL AUTO_INCREMENT,
        name varchar(50) DEFAULT NULL,
        description varchar(100) DEFAULT NULL,
        PRIMARY KEY (rolId),
        UNIQUE KEY name_UNIQUE (name)
    ) ENGINE = InnoDB AUTO_INCREMENT = 2 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
    IF NOT EXISTS MODULE (
        moduleId int unsigned NOT NULL,
        name varchar(50) DEFAULT NULL,
        PRIMARY KEY (moduleId),
        UNIQUE KEY name_UNIQUE (name)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
    IF NOT EXISTS ROL_MODULE (
        moduleId int unsigned NOT NULL,
        rolId int unsigned NOT NULL,
        PRIMARY KEY (moduleId, rolId),
        KEY FK_RELATION_ROL_idx (rolId),
        CONSTRAINT FK_RELATION_MODULE FOREIGN KEY (moduleId) REFERENCES MODULE (moduleId) ON DELETE CASCADE,
        CONSTRAINT FK_RELATION_ROL FOREIGN KEY (rolId) REFERENCES ROL (rolId) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
    IF NOT EXISTS USER (
        userId int unsigned NOT NULL AUTO_INCREMENT,
        userName varchar(100) NOT NULL,
        password varchar(256) NOT NULL,
        rolId int unsigned NOT NULL,
        token varchar(500) DEFAULT NULL,
        firstName varchar(100) DEFAULT NULL,
        lastName varchar(100) DEFAULT NULL,
        PRIMARY KEY (userId),
        UNIQUE KEY userName_UNIQUE (userName),
        KEY FK_USER_ROL_idx (rolId),
        CONSTRAINT FK_USER_ROL FOREIGN KEY (rolId) REFERENCES ROL (rolId) ON DELETE RESTRICT
    ) ENGINE = InnoDB AUTO_INCREMENT = 6 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

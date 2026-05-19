CREATE TABLE
    IF NOT EXISTS LOT_TRADE (
        lotTradeId int NOT NULL AUTO_INCREMENT,
        loteId int NOT NULL,
        productType int NOT NULL,
        unitType int NOT NULL,
        unitCost decimal(18, 2) NOT NULL,
        tradeMode int NOT NULL,
        freightCost decimal(18, 2) DEFAULT NULL,
        PRIMARY KEY (lotTradeId),
        KEY FK_LOT_TRADE_PRODUCT_UNIT_idx (unitType),
        KEY FK_LOT_TRADE_PRODUCT_TYPE_idx (productType),
        KEY FK_LOT_TRADE_MODE_idx (tradeMode),
        KEY FK_LOT_TRADE_LOT_idx (loteId),
        CONSTRAINT FK_LOT_TRADE_LOT FOREIGN KEY (loteId) REFERENCES LOT (lotId) ON DELETE CASCADE,
        CONSTRAINT FK_LOT_TRADE_MODE FOREIGN KEY (tradeMode) REFERENCES TRADE_MODE (tradeId),
        CONSTRAINT FK_LOT_TRADE_PRODUCT_TYPE FOREIGN KEY (productType) REFERENCES PRODUCT_TYPE (typeId),
        CONSTRAINT FK_LOT_TRADE_PRODUCT_UNIT FOREIGN KEY (unitType) REFERENCES PRODUCT_UNIT (unitId)
    ) ENGINE = InnoDB AUTO_INCREMENT = 14 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS agrosys_db.transactions (
    transaction_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    transaction_type VARCHAR(50) NOT NULL,
    create_at DATE NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    description VARCHAR(256),
    lot_trade_id INT 
) ENGINE= InnoDB DEFAULT CHARSET= utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
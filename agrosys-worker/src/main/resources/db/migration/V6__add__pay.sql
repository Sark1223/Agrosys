ALTER TABLE agrosys_worker.WORKER
    ADD COLUMN hourly_pay DECIMAL(10,2) NOT NULL DEFAULT 0.00;

UPDATE agrosys_worker.WORKER SET hourly_pay = ROUND(salary/56, 2);

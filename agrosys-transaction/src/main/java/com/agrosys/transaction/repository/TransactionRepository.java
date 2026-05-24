package com.agrosys.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrosys.transaction.entity.AgrosysTransaction;

@Repository
public interface TransactionRepository extends JpaRepository<AgrosysTransaction, Integer> {
}

package com.agrosys.transaction.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.agrosys.transaction.entity.AgrosysTransaction;

@Repository
public interface TransactionRepository extends JpaRepository<AgrosysTransaction, Integer> {

    @Query(value = """
        SELECT * FROM agrosys_db.
    transactions t 
        WHERE t.create_at 
    BETWEEN :startDate AND :endDate 
            AND(:type IS NULL OR 
    t.transaction_type = :type)
        ORDER BY t.create_at 
    DESC, t.transaction_id DESC
        """, nativeQuery = true)
    List<AgrosysTransaction> findByFilters(LocalDate startDate, LocalDate endDate, String type);

}

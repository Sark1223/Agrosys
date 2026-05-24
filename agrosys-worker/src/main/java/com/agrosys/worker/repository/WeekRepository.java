package com.agrosys.worker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrosys.worker.entity.Week;

@Repository
public interface WeekRepository extends JpaRepository<Week, String> {

    List<Week> findAllByOrderByStartDateAsc();
}

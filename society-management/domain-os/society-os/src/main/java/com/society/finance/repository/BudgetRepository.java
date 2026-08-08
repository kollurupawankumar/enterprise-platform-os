package com.society.finance.repository;

import com.society.finance.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, Integer> {
    List<BudgetEntity> findByYear(String year);
}

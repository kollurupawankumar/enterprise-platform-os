package com.society.finance.repository;

import com.society.finance.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Integer> {
}

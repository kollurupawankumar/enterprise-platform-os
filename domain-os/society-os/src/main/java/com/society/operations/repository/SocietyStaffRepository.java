package com.society.operations.repository;

import com.society.operations.entity.SocietyStaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocietyStaffRepository extends JpaRepository<SocietyStaffEntity, Integer> {
    List<SocietyStaffEntity> findByActiveTrue();
}

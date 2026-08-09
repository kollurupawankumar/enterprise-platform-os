package com.society.society.repository;

import com.society.society.entity.SocietyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocietyRepository extends JpaRepository<SocietyEntity, Integer> {

    Optional<SocietyEntity> findFirstByActiveTrue();

    boolean existsByActiveTrue();

}
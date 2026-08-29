package com.society.property.repository;

import com.society.property.entity.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<PropertyEntity, Integer> {

    @Query("SELECT p FROM PropertyEntity p LEFT JOIN FETCH p.currentOwner")
    List<PropertyEntity> findAllWithOwners();

    @Query("SELECT p FROM PropertyEntity p LEFT JOIN FETCH p.currentOwner WHERE p.id = :id")
    Optional<PropertyEntity> findByIdWithOwner(@org.springframework.data.repository.query.Param("id") Integer id);

    Optional<PropertyEntity> findByPropertyNumber(String propertyNumber);
    boolean existsByPropertyNumberIgnoreCaseAndBlockIgnoreCaseAndTypeIgnoreCase(String propertyNumber, String block, String type);
    List<PropertyEntity> findByCurrentOwnerId(Integer memberId);
}

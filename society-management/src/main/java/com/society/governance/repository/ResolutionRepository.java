package com.society.governance.repository;

import com.society.governance.entity.ResolutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResolutionRepository extends JpaRepository<ResolutionEntity, Integer> {
    Optional<ResolutionEntity> findByResolutionNumber(String resolutionNumber);
    List<ResolutionEntity> findByMeetingId(Integer meetingId);
}

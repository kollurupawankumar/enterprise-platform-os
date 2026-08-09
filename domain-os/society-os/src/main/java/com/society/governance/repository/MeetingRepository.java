package com.society.governance.repository;

import com.society.governance.entity.MeetingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<MeetingEntity, Integer> {
    List<MeetingEntity> findByMeetingType(String meetingType);
}

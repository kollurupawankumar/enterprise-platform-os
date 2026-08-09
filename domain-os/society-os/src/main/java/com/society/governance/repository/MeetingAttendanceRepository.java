package com.society.governance.repository;

import com.society.governance.entity.MeetingAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingAttendanceRepository extends JpaRepository<MeetingAttendanceEntity, Integer> {

    @Query("SELECT ma FROM MeetingAttendanceEntity ma LEFT JOIN FETCH ma.member WHERE ma.meeting.id = :meetingId")
    List<MeetingAttendanceEntity> findByMeetingIdWithMembers(@Param("meetingId") Integer meetingId);

    Optional<MeetingAttendanceEntity> findByMeetingIdAndMemberId(Integer meetingId, Integer memberId);
}

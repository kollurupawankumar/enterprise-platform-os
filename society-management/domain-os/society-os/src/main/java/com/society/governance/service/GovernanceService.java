package com.society.governance.service;

import com.society.governance.entity.ManagingCommitteeEntity;
import com.society.governance.entity.MeetingEntity;
import com.society.governance.entity.ResolutionEntity;
import java.util.List;
import java.util.Optional;

public interface GovernanceService {
    MeetingEntity scheduleMeeting(MeetingEntity meeting);
    MeetingEntity completeMeeting(Integer meetingId, String minutes);
    Optional<MeetingEntity> getMeetingById(Integer id);
    List<MeetingEntity> getAllMeetings();
    List<MeetingEntity> getMeetingsByType(String type);

    ResolutionEntity createResolution(ResolutionEntity resolution);
    Optional<ResolutionEntity> getResolutionById(Integer id);
    Optional<ResolutionEntity> getResolutionByNumber(String resolutionNumber);
    List<ResolutionEntity> getResolutionsForMeeting(Integer meetingId);
    List<ResolutionEntity> getAllResolutions();

    ManagingCommitteeEntity addCommitteeMember(ManagingCommitteeEntity mc);
    List<ManagingCommitteeEntity> getAllCommitteeMembers();

    void markAttendance(Integer meetingId, Integer memberId, String status);
    List<com.society.governance.entity.MeetingAttendanceEntity> getAttendanceForMeeting(Integer meetingId);
}

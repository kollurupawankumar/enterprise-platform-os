package com.society.governance.service;

import com.society.governance.entity.ManagingCommitteeEntity;
import com.society.governance.entity.MeetingEntity;
import com.society.governance.entity.ResolutionEntity;
import com.society.governance.repository.ManagingCommitteeRepository;
import com.society.governance.repository.MeetingRepository;
import com.society.governance.repository.ResolutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GovernanceServiceImpl implements GovernanceService {

    private final MeetingRepository meetingRepository;
    private final ResolutionRepository resolutionRepository;
    private final ManagingCommitteeRepository managingCommitteeRepository;
    private final com.society.governance.repository.MeetingAttendanceRepository meetingAttendanceRepository;
    private final com.society.member.repository.MemberRepository memberRepository;

    public GovernanceServiceImpl(
            MeetingRepository meetingRepository,
            ResolutionRepository resolutionRepository,
            ManagingCommitteeRepository managingCommitteeRepository,
            com.society.governance.repository.MeetingAttendanceRepository meetingAttendanceRepository,
            com.society.member.repository.MemberRepository memberRepository) {
        this.meetingRepository = meetingRepository;
        this.resolutionRepository = resolutionRepository;
        this.managingCommitteeRepository = managingCommitteeRepository;
        this.meetingAttendanceRepository = meetingAttendanceRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public MeetingEntity scheduleMeeting(MeetingEntity meeting) {
        meeting.setStatus("SCHEDULED");
        return meetingRepository.save(meeting);
    }

    @Override
    public MeetingEntity completeMeeting(Integer meetingId, String minutes) {
        MeetingEntity meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found with ID: " + meetingId));
        meeting.setMinutes(minutes);
        meeting.setStatus("COMPLETED");
        return meetingRepository.save(meeting);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MeetingEntity> getMeetingById(Integer id) {
        return meetingRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetingEntity> getAllMeetings() {
        return meetingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetingEntity> getMeetingsByType(String type) {
        return meetingRepository.findByMeetingType(type);
    }

    @Override
    public ResolutionEntity createResolution(ResolutionEntity resolution) {
        return resolutionRepository.save(resolution);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResolutionEntity> getResolutionById(Integer id) {
        return resolutionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResolutionEntity> getResolutionByNumber(String resolutionNumber) {
        return resolutionRepository.findByResolutionNumber(resolutionNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResolutionEntity> getResolutionsForMeeting(Integer meetingId) {
        return resolutionRepository.findByMeetingId(meetingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResolutionEntity> getAllResolutions() {
        return resolutionRepository.findAllWithMeetings();
    }

    @Override
    public ManagingCommitteeEntity addCommitteeMember(ManagingCommitteeEntity mc) {
        if (mc.getStatus() == null) {
            mc.setStatus("ACTIVE");
        }

        // For single-holder designations (CHAIRMAN, SECRETARY, TREASURER, INTERNAL_AUDITOR),
        // automatically expire any existing ACTIVE officer and set their end_date to today
        String desig = mc.getDesignation();
        if ("CHAIRMAN".equalsIgnoreCase(desig) || "SECRETARY".equalsIgnoreCase(desig)
                || "TREASURER".equalsIgnoreCase(desig) || "INTERNAL_AUDITOR".equalsIgnoreCase(desig)) {

            List<ManagingCommitteeEntity> existingActive = managingCommitteeRepository.findByDesignationAndStatus(desig, "ACTIVE");
            String todayStr = java.time.LocalDate.now().toString();

            for (ManagingCommitteeEntity existing : existingActive) {
                existing.setStatus("EXPIRED");
                existing.setEndDate(todayStr);
                managingCommitteeRepository.save(existing);
            }
        }

        return managingCommitteeRepository.save(mc);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagingCommitteeEntity> getAllCommitteeMembers() {
        return managingCommitteeRepository.findAllWithMembers();
    }

    @Override
    public void markAttendance(Integer meetingId, Integer memberId, String status) {
        MeetingEntity meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found: " + meetingId));
        com.society.member.entity.MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));

        com.society.governance.entity.MeetingAttendanceEntity attendance =
                meetingAttendanceRepository.findByMeetingIdAndMemberId(meetingId, memberId)
                        .orElseGet(() -> {
                            com.society.governance.entity.MeetingAttendanceEntity a = new com.society.governance.entity.MeetingAttendanceEntity();
                            a.setMeeting(meeting);
                            a.setMember(member);
                            return a;
                        });
        attendance.setStatus(status);
        meetingAttendanceRepository.save(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.society.governance.entity.MeetingAttendanceEntity> getAttendanceForMeeting(Integer meetingId) {
        return meetingAttendanceRepository.findByMeetingIdWithMembers(meetingId);
    }
}

package com.society.governance.service;

import com.society.governance.entity.MeetingEntity;
import com.society.governance.entity.ResolutionEntity;
import com.society.governance.repository.MeetingRepository;
import com.society.governance.repository.ResolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GovernanceServiceTest {

    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private ResolutionRepository resolutionRepository;
    @Mock
    private com.society.governance.repository.ManagingCommitteeRepository managingCommitteeRepository;
    @Mock
    private com.society.governance.repository.MeetingAttendanceRepository meetingAttendanceRepository;
    @Mock
    private com.society.member.repository.MemberRepository memberRepository;

    private GovernanceService governanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        governanceService = new GovernanceServiceImpl(meetingRepository, resolutionRepository, managingCommitteeRepository, meetingAttendanceRepository, memberRepository);
    }

    @Test
    void testScheduleMeeting() {
        MeetingEntity m = new MeetingEntity();
        m.setTitle("Annual General Meeting");
        m.setMeetingType("AGM");
        m.setMeetingDate("2026-09-01");
        m.setVenue("Clubhouse");

        when(meetingRepository.save(any(MeetingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        MeetingEntity scheduled = governanceService.scheduleMeeting(m);

        assertNotNull(scheduled);
        assertEquals("SCHEDULED", scheduled.getStatus());
        verify(meetingRepository, times(1)).save(m);
    }

    @Test
    void testCompleteMeeting() {
        MeetingEntity m = new MeetingEntity();
        m.setId(1);
        m.setStatus("SCHEDULED");

        when(meetingRepository.findById(1)).thenReturn(Optional.of(m));
        when(meetingRepository.save(any(MeetingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        MeetingEntity completed = governanceService.completeMeeting(1, "Discussed budget.");

        assertNotNull(completed);
        assertEquals("COMPLETED", completed.getStatus());
        assertEquals("Discussed budget.", completed.getMinutes());
    }
}

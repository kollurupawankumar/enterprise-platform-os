package com.society.governance.entity;

import com.society.member.entity.MemberEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_attendance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"meeting_id", "member_id"})
})
public class MeetingAttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private MeetingEntity meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(nullable = false, length = 20)
    private String status; // PRESENT, ABSENT, APOLOGY

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MeetingAttendanceEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public MeetingEntity getMeeting() { return meeting; }
    public void setMeeting(MeetingEntity meeting) { this.meeting = meeting; }

    public MemberEntity getMember() { return member; }
    public void setMember(MemberEntity member) { this.member = member; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

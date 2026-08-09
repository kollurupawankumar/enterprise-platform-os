package com.society.share.entity;

import com.society.member.entity.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "share_certificate")
public class ShareCertificateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "certificate_number", nullable = false, unique = true, length = 30)
    private String certificateNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(name = "from_share_number", nullable = false)
    private Integer fromShareNumber;

    @Column(name = "to_share_number", nullable = false)
    private Integer toShareNumber;

    @Column(name = "total_shares", nullable = false)
    private Integer totalShares;

    @Column(name = "face_value_per_share", nullable = false)
    private Double faceValuePerShare;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "issue_date", nullable = false)
    private String issueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShareCertificateStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = ShareCertificateStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public ShareCertificateEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public MemberEntity getMember() {
        return member;
    }

    public void setMember(MemberEntity member) {
        this.member = member;
    }

    public Integer getFromShareNumber() {
        return fromShareNumber;
    }

    public void setFromShareNumber(Integer fromShareNumber) {
        this.fromShareNumber = fromShareNumber;
    }

    public Integer getToShareNumber() {
        return toShareNumber;
    }

    public void setToShareNumber(Integer toShareNumber) {
        this.toShareNumber = toShareNumber;
    }

    public Integer getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(Integer totalShares) {
        this.totalShares = totalShares;
    }

    public Double getFaceValuePerShare() {
        return faceValuePerShare;
    }

    public void setFaceValuePerShare(Double faceValuePerShare) {
        this.faceValuePerShare = faceValuePerShare;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public ShareCertificateStatus getStatus() {
        return status;
    }

    public void setStatus(ShareCertificateStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

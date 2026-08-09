package com.society.share.service;

import com.society.member.entity.MemberEntity;
import com.society.member.repository.MemberRepository;
import com.society.share.dto.ShareCertificateDto;
import com.society.share.dto.ShareTransferDto;
import com.society.share.entity.ShareCertificateEntity;
import com.society.share.entity.ShareCertificateStatus;
import com.society.share.entity.ShareTransferHistoryEntity;
import com.society.share.repository.ShareCertificateRepository;
import com.society.share.repository.ShareTransferHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ShareServiceImpl implements ShareService {

    private final ShareCertificateRepository certificateRepository;
    private final ShareTransferHistoryRepository transferRepository;
    private final MemberRepository memberRepository;

    public ShareServiceImpl(
            ShareCertificateRepository certificateRepository,
            ShareTransferHistoryRepository transferRepository,
            MemberRepository memberRepository) {

        this.certificateRepository = certificateRepository;
        this.transferRepository = transferRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public ShareCertificateDto allotShares(ShareCertificateDto dto) {

        validateAllotment(dto);

        MemberEntity member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        ShareCertificateEntity entity = new ShareCertificateEntity();

        String certNo = (dto.certificateNumber() == null || dto.certificateNumber().isBlank())
                ? generateCertificateNumber()
                : dto.certificateNumber().trim();

        entity.setCertificateNumber(certNo);
        entity.setMember(member);
        entity.setFromShareNumber(dto.fromShareNumber());
        entity.setToShareNumber(dto.toShareNumber());

        int totalShares = dto.toShareNumber() - dto.fromShareNumber() + 1;
        entity.setTotalShares(totalShares);

        double faceValue = dto.faceValuePerShare() != null && dto.faceValuePerShare() > 0
                ? dto.faceValuePerShare()
                : 50.0;
        entity.setFaceValuePerShare(faceValue);
        entity.setTotalAmount(totalShares * faceValue);

        entity.setIssueDate((dto.issueDate() != null ? dto.issueDate() : LocalDate.now()).toString());
        entity.setStatus(ShareCertificateStatus.ACTIVE);

        ShareCertificateEntity saved = certificateRepository.save(entity);

        return toDto(saved);
    }

    @Override
    public ShareTransferDto transferShares(Integer certificateId, Integer newMemberId, Double transferFee, String remarks) {

        if (certificateId == null) {
            throw new IllegalArgumentException("Certificate ID is required.");
        }

        if (newMemberId == null) {
            throw new IllegalArgumentException("Target Member is required for transfer.");
        }

        ShareCertificateEntity certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new IllegalArgumentException("Share Certificate not found."));

        if (certificate.getStatus() != ShareCertificateStatus.ACTIVE) {
            throw new IllegalArgumentException("Only ACTIVE certificates can be transferred.");
        }

        MemberEntity fromMember = certificate.getMember();
        if (fromMember.getId().equals(newMemberId)) {
            throw new IllegalArgumentException("Cannot transfer shares to the same member.");
        }

        MemberEntity toMember = memberRepository.findById(newMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Target member not found."));

        ShareTransferHistoryEntity history = new ShareTransferHistoryEntity();
        history.setCertificate(certificate);
        history.setFromMember(fromMember);
        history.setToMember(toMember);
        history.setTransferDate(LocalDate.now().toString());
        history.setTransferFee(transferFee != null ? transferFee : 0.0);
        history.setRemarks(remarks);

        transferRepository.save(history);

        certificate.setMember(toMember);
        ShareCertificateEntity updated = certificateRepository.save(certificate);

        return toTransferDto(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShareCertificateDto> findAll() {
        return certificateRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShareCertificateDto> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        return certificateRepository.search(keyword.trim())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShareCertificateDto findById(Integer id) {
        return certificateRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShareTransferDto> findTransferHistory(Integer certificateId) {
        return transferRepository.findByCertificateIdOrderByIdDesc(certificateId)
                .stream()
                .map(this::toTransferDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String generateCertificateNumber() {
        return certificateRepository.findFirstByOrderByIdDesc()
                .map(sc -> String.format("SC-%05d", sc.getId() + 1))
                .orElse("SC-00001");
    }

    @Override
    @Transactional(readOnly = true)
    public int getNextAvailableShareNumber() {
        Integer maxTo = certificateRepository.findMaxToShareNumber();
        return (maxTo != null && maxTo > 0) ? maxTo + 1 : 1;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return certificateRepository.count();
    }

    private void validateAllotment(ShareCertificateDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Share details are mandatory.");
        }

        if (dto.memberId() == null) {
            throw new IllegalArgumentException("Member selection is mandatory.");
        }

        if (dto.fromShareNumber() == null || dto.fromShareNumber() <= 0) {
            throw new IllegalArgumentException("From Share Number must be positive.");
        }

        if (dto.toShareNumber() == null || dto.toShareNumber() < dto.fromShareNumber()) {
            throw new IllegalArgumentException("To Share Number must be greater than or equal to From Share Number.");
        }

        boolean overlaps = certificateRepository.isShareRangeOverlapping(
                dto.fromShareNumber(), dto.toShareNumber());

        if (overlaps) {
            throw new IllegalArgumentException("Share number range ("
                    + dto.fromShareNumber() + " - " + dto.toShareNumber()
                    + ") overlaps with existing active shares.");
        }
    }

    private ShareCertificateDto toDto(ShareCertificateEntity entity) {

        MemberEntity member = entity.getMember();

        String memberName = (member != null)
                ? (member.getFirstName() + " " + (member.getLastName() != null ? member.getLastName() : "")).trim()
                : "Unknown";

        String memberNumber = (member != null) ? member.getMemberNumber() : "";

        return new ShareCertificateDto(
                entity.getId(),
                entity.getCertificateNumber(),
                member != null ? member.getId() : null,
                memberName,
                memberNumber,
                entity.getFromShareNumber(),
                entity.getToShareNumber(),
                entity.getTotalShares(),
                entity.getFaceValuePerShare(),
                entity.getTotalAmount(),
                parseDate(entity.getIssueDate()),
                entity.getStatus()
        );
    }

    private ShareTransferDto toTransferDto(ShareTransferHistoryEntity entity) {

        MemberEntity from = entity.getFromMember();
        MemberEntity to = entity.getToMember();

        String fromName = from != null ? (from.getFirstName() + " " + (from.getLastName() != null ? from.getLastName() : "")).trim() : "";
        String toName = to != null ? (to.getFirstName() + " " + (to.getLastName() != null ? to.getLastName() : "")).trim() : "";

        return new ShareTransferDto(
                entity.getId(),
                entity.getCertificate().getId(),
                entity.getCertificate().getCertificateNumber(),
                from != null ? from.getId() : null,
                fromName,
                to != null ? to.getId() : null,
                toName,
                parseDate(entity.getTransferDate()),
                entity.getTransferFee(),
                entity.getRemarks()
        );
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return LocalDate.now();
        }
        try {
            if (value.matches("^\\d+$")) {
                long millis = Long.parseLong(value);
                return java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            }
            return LocalDate.parse(value);
        } catch (Exception ex) {
            return LocalDate.now();
        }
    }
}

package com.society.share.dto;

import com.society.share.entity.ShareCertificateStatus;

import java.time.LocalDate;

public record ShareCertificateDto(
        Integer id,
        String certificateNumber,
        Integer memberId,
        String memberName,
        String memberNumber,
        Integer fromShareNumber,
        Integer toShareNumber,
        Integer totalShares,
        Double faceValuePerShare,
        Double totalAmount,
        LocalDate issueDate,
        ShareCertificateStatus status
) {
}

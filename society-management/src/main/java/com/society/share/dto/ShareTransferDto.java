package com.society.share.dto;

import java.time.LocalDate;

public record ShareTransferDto(
        Integer id,
        Integer certificateId,
        String certificateNumber,
        Integer fromMemberId,
        String fromMemberName,
        Integer toMemberId,
        String toMemberName,
        LocalDate transferDate,
        Double transferFee,
        String remarks
) {
}

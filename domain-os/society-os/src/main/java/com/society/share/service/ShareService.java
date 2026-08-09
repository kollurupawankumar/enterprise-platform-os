package com.society.share.service;

import com.society.share.dto.ShareCertificateDto;
import com.society.share.dto.ShareTransferDto;

import java.util.List;

public interface ShareService {

    ShareCertificateDto allotShares(ShareCertificateDto dto);

    ShareTransferDto transferShares(Integer certificateId, Integer newMemberId, Double transferFee, String remarks);

    List<ShareCertificateDto> findAll();

    List<ShareCertificateDto> search(String keyword);

    ShareCertificateDto findById(Integer id);

    List<ShareTransferDto> findTransferHistory(Integer certificateId);

    String generateCertificateNumber();

    int getNextAvailableShareNumber();

    long count();
}

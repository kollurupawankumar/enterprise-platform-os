package com.society.society.service;

import com.society.society.dto.SocietyDto;
import com.society.society.entity.SocietyEntity;
import com.society.society.repository.SocietyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SocietyServiceImpl implements SocietyService {

    private final SocietyRepository repository;

    public SocietyServiceImpl(SocietyRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SocietyDto> getSociety() {

        return repository.findFirstByActiveTrue()
                .map(this::toDto);

    }

    @Override
    public SocietyDto save(SocietyDto dto) {

        SocietyEntity entity = repository.findFirstByActiveTrue().orElse(new SocietyEntity());

        entity.setName(dto.name());
        entity.setShortName(dto.shortName());
        entity.setRegistrationNumber(dto.registrationNumber());
        entity.setAddressLine1(dto.addressLine1());
        entity.setAddressLine2(dto.addressLine2());
        entity.setCity(dto.city());
        entity.setState(dto.state());
        entity.setPinCode(dto.pinCode());
        entity.setPhone(dto.phone());
        entity.setEmail(dto.email());
        entity.setWebsite(dto.website());
        entity.setFinancialYearStartMonth(dto.financialYearStartMonth());
        entity.setMemberNumberFormat(dto.memberNumberFormat() != null && !dto.memberNumberFormat().isBlank() ? dto.memberNumberFormat().trim() : "MEM-{SEQ}");
        entity.setMembershipNumberFormat(dto.membershipNumberFormat() != null && !dto.membershipNumberFormat().isBlank() ? dto.membershipNumberFormat().trim() : "SSTS/{YEAR}/{SEQ}");
        entity.setShareCertificateFormat(dto.shareCertificateFormat() != null && !dto.shareCertificateFormat().isBlank() ? dto.shareCertificateFormat().trim() : "SC/{YEAR}/{SEQ}");
        entity.setLogoPath(dto.logoPath());
        entity.setSealPath(dto.sealPath());
        entity.setActive(dto.active() != null ? dto.active() : true);

        SocietyEntity saved = repository.save(entity);

        return toDto(saved);

    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists() {

        return repository.existsByActiveTrue();

    }

    private SocietyDto toDto(SocietyEntity entity) {

        return new SocietyDto(
                entity.getName(),
                entity.getShortName(),
                entity.getRegistrationNumber(),
                entity.getAddressLine1(),
                entity.getAddressLine2(),
                entity.getCity(),
                entity.getState(),
                entity.getPinCode(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getWebsite(),
                entity.getFinancialYearStartMonth(),
                entity.getMemberNumberFormat() != null ? entity.getMemberNumberFormat() : "MEM-{SEQ}",
                entity.getMembershipNumberFormat() != null ? entity.getMembershipNumberFormat() : "SSTS/{YEAR}/{SEQ}",
                entity.getShareCertificateFormat() != null ? entity.getShareCertificateFormat() : "SC/{YEAR}/{SEQ}",
                entity.getLogoPath(),
                entity.getSealPath(),
                entity.getActive()
        );

    }
}
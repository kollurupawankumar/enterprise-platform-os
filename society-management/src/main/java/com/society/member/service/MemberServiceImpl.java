package com.society.member.service;

import com.society.common.service.NumberGeneratorService;
import com.society.member.dto.MemberDto;
import com.society.member.entity.MemberEntity;
import com.society.member.entity.MemberStatus;
import com.society.member.mapper.MemberMapper;
import com.society.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;
    private final MemberMapper mapper;
    private final NumberGeneratorService numberGeneratorService;

    public MemberServiceImpl(
            MemberRepository repository,
            MemberMapper mapper,
            NumberGeneratorService numberGeneratorService) {

        this.repository = repository;
        this.mapper = mapper;
        this.numberGeneratorService = numberGeneratorService;
    }

    @Override
    public MemberDto register(MemberDto dto) {

        validate(dto);

        MemberEntity entity = mapper.toEntity(dto);

        if (entity.getMemberNumber() == null
                || entity.getMemberNumber().isBlank()) {

            entity.setMemberNumber(generateMemberNumber());

        }

        if (entity.getStatus() == null) {
            entity.setStatus(MemberStatus.ACTIVE);
        }

        entity.setActive(true);

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        MemberEntity saved = repository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    public MemberDto update(MemberDto dto) {

        validate(dto);

        if (dto.id() == null) {
            throw new IllegalArgumentException("Member ID is required for update.");
        }

        MemberEntity existing = repository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + dto.id()));

        existing.setMembershipNumber(dto.membershipNumber());
        existing.setFirstName(dto.firstName());
        existing.setLastName(dto.lastName());
        existing.setMobileNumber(dto.mobileNumber());
        existing.setEmail(dto.email());
        existing.setAadhaarNumber(dto.aadhaarNumber());
        existing.setPanNumber(dto.panNumber());

        if (dto.status() != null) {
            existing.setStatus(dto.status());
        }

        existing.setActive(dto.active());
        existing.setUpdatedAt(java.time.LocalDateTime.now());

        MemberEntity saved = repository.save(existing);

        return mapper.toDto(saved);
    }

    @Override
    public List<MemberDto> findAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public MemberDto findById(Integer id) {

        return repository.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public void deactivate(Integer id) {

        repository.findById(id).ifPresent(member -> {

            member.setActive(false);
            member.setStatus(MemberStatus.INACTIVE);

            repository.save(member);

        });

    }

    @Override
    public long count() {

        return repository.count();
    }

    @Override
    public String generateMemberNumber() {

        return numberGeneratorService.nextMemberNumber();
    }

    private void validate(MemberDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Member details are required.");
        }

        if (dto.firstName() == null || dto.firstName().isBlank()) {
            throw new IllegalArgumentException("First Name is mandatory.");
        }

        if (dto.mobileNumber() == null || dto.mobileNumber().isBlank()) {
            throw new IllegalArgumentException("Mobile Number is mandatory.");
        }

        String mobileClean = dto.mobileNumber().replaceAll("\\s+", "");
        if (!mobileClean.matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("Mobile Number must be a valid 10-digit number.");
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            if (!dto.email().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                throw new IllegalArgumentException("Invalid Email address format.");
            }
        }

        if (dto.aadhaarNumber() != null && !dto.aadhaarNumber().isBlank()) {
            String aadhaarClean = dto.aadhaarNumber().replaceAll("\\s+", "");
            if (!aadhaarClean.matches("^[0-9]{12}$")) {
                throw new IllegalArgumentException("Aadhaar Number must be a 12-digit number.");
            }
        }

        if (dto.panNumber() != null && !dto.panNumber().isBlank()) {
            String panClean = dto.panNumber().trim().toUpperCase();
            if (!panClean.matches("^[A-Z]{5}[0-9]{4}[A-Z]{1}$")) {
                throw new IllegalArgumentException("PAN Number must be in format (e.g. ABCDE1234F).");
            }
        }

        if (dto.membershipNumber() != null && !dto.membershipNumber().isBlank()) {
            boolean exists = dto.id() == null
                    ? repository.existsByMembershipNumber(dto.membershipNumber())
                    : repository.existsByMembershipNumberAndIdNot(dto.membershipNumber(), dto.id());

            if (exists) {
                throw new IllegalArgumentException("Membership Number already exists.");
            }
        }
    }

    @Override
    public List<MemberDto> search(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        return repository.search(keyword)
                .stream()
                .map(mapper::toDto)
                .toList();

    }

}
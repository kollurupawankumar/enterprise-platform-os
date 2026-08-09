package com.society.member.service;

import com.society.common.service.NumberGeneratorService;
import com.society.member.dto.JointOwnerDto;
import com.society.member.dto.MemberDto;
import com.society.member.dto.NomineeDto;
import com.society.member.entity.JointOwnerEntity;
import com.society.member.entity.MemberEntity;
import com.society.member.entity.MemberStatus;
import com.society.member.entity.NomineeEntity;
import com.society.member.mapper.MemberMapper;
import com.society.member.repository.JointOwnerRepository;
import com.society.member.repository.MemberRepository;
import com.society.member.repository.NomineeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;
    private final JointOwnerRepository jointOwnerRepository;
    private final NomineeRepository nomineeRepository;
    private final MemberMapper mapper;
    private final NumberGeneratorService numberGeneratorService;

    public MemberServiceImpl(
            MemberRepository repository,
            JointOwnerRepository jointOwnerRepository,
            NomineeRepository nomineeRepository,
            MemberMapper mapper,
            NumberGeneratorService numberGeneratorService) {

        this.repository = repository;
        this.jointOwnerRepository = jointOwnerRepository;
        this.nomineeRepository = nomineeRepository;
        this.mapper = mapper;
        this.numberGeneratorService = numberGeneratorService;
    }

    @Override
    @Transactional
    public MemberDto register(MemberDto dto) {
        validate(dto);

        MemberEntity entity = mapper.toEntity(dto);

        if (entity.getMemberNumber() == null || entity.getMemberNumber().isBlank()) {
            entity.setMemberNumber(generateMemberNumber());
        }

        if (entity.getMembershipNumber() == null || entity.getMembershipNumber().isBlank()) {
            entity.setMembershipNumber(numberGeneratorService.nextMembershipNumber());
        }

        if (entity.getStatus() == null) {
            entity.setStatus(MemberStatus.ACTIVE);
        }

        entity.setActive(true);

        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        MemberEntity saved = repository.save(entity);

        saveJointOwnersAndNominees(saved, dto.jointOwners(), dto.nominees());

        return findById(saved.getId());
    }

    @Override
    @Transactional
    public MemberDto update(MemberDto dto) {
        validate(dto);

        if (dto.id() == null) {
            throw new IllegalArgumentException("Member ID is required for update.");
        }

        MemberEntity existing = repository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + dto.id()));

        existing.setMembershipNumber(dto.membershipNumber());
        existing.setFirstName(dto.firstName());
        existing.setMiddleName(dto.middleName());
        existing.setLastName(dto.lastName());
        existing.setMobileNumber(dto.mobileNumber());
        existing.setEmail(dto.email());
        existing.setGender(dto.gender());
        existing.setDob(dto.dob());
        existing.setOccupation(dto.occupation());
        existing.setEmergencyContactName(dto.emergencyContactName());
        existing.setEmergencyContactPhone(dto.emergencyContactPhone());

        existing.setMemberType(dto.memberType());
        existing.setAdmissionDate(dto.admissionDate());
        existing.setResolutionNumber(dto.resolutionNumber());
        existing.setResolutionDate(dto.resolutionDate());

        existing.setPermanentAddress(dto.permanentAddress());
        existing.setCorrespondenceAddress(dto.correspondenceAddress());

        existing.setPhotoPath(dto.photoPath());
        existing.setAadhaarDocPath(dto.aadhaarDocPath());
        existing.setPanDocPath(dto.panDocPath());

        existing.setAadhaarNumber(dto.aadhaarNumber());
        existing.setPanNumber(dto.panNumber());

        if (dto.status() != null) {
            existing.setStatus(dto.status());
        }

        existing.setActive(dto.active());
        existing.setUpdatedAt(LocalDateTime.now());

        MemberEntity saved = repository.save(existing);

        // Update joint owners & nominees
        saveJointOwnersAndNominees(saved, dto.jointOwners(), dto.nominees());

        return findById(saved.getId());
    }

    private void saveJointOwnersAndNominees(MemberEntity member, List<JointOwnerDto> jointOwners, List<NomineeDto> nominees) {
        // Clear existing joint owners
        List<JointOwnerEntity> existingJoints = jointOwnerRepository.findByMemberId(member.getId());
        if (!existingJoints.isEmpty()) {
            jointOwnerRepository.deleteAll(existingJoints);
        }
        if (jointOwners != null) {
            for (JointOwnerDto jDto : jointOwners) {
                JointOwnerEntity jo = new JointOwnerEntity();
                jo.setMember(member);
                jo.setFirstName(jDto.firstName());
                jo.setLastName(jDto.lastName());
                jo.setRelationship(jDto.relationship());
                jo.setAadhaarNumber(jDto.aadhaarNumber());
                jo.setPanNumber(jDto.panNumber());
                jointOwnerRepository.save(jo);
            }
        }

        // Clear existing nominees
        List<NomineeEntity> existingNominees = nomineeRepository.findByMemberId(member.getId());
        if (!existingNominees.isEmpty()) {
            nomineeRepository.deleteAll(existingNominees);
        }
        if (nominees != null) {
            for (NomineeDto nDto : nominees) {
                NomineeEntity ne = new NomineeEntity();
                ne.setMember(member);
                ne.setFirstName(nDto.firstName());
                ne.setLastName(nDto.lastName());
                ne.setRelationship(nDto.relationship());
                ne.setSharePercentage(nDto.sharePercentage() == null ? 0.0 : nDto.sharePercentage());
                nomineeRepository.save(ne);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDto> findAll() {
        return repository.findAll()
                .stream()
                .map(entity -> {
                    List<JointOwnerEntity> joints = jointOwnerRepository.findByMemberId(entity.getId());
                    List<NomineeEntity> nominees = nomineeRepository.findByMemberId(entity.getId());
                    return mapper.toDto(entity, joints, nominees);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto findById(Integer id) {
        return repository.findById(id)
                .map(entity -> {
                    List<JointOwnerEntity> joints = jointOwnerRepository.findByMemberId(entity.getId());
                    List<NomineeEntity> nominees = nomineeRepository.findByMemberId(entity.getId());
                    return mapper.toDto(entity, joints, nominees);
                })
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
    @Transactional(readOnly = true)
    public List<MemberDto> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        return repository.search(keyword)
                .stream()
                .map(entity -> {
                    List<JointOwnerEntity> joints = jointOwnerRepository.findByMemberId(entity.getId());
                    List<NomineeEntity> nominees = nomineeRepository.findByMemberId(entity.getId());
                    return mapper.toDto(entity, joints, nominees);
                })
                .toList();
    }
}
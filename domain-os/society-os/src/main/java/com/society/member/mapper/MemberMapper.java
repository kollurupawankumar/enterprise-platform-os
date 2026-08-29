package com.society.member.mapper;

import com.society.member.dto.JointOwnerDto;
import com.society.member.dto.MemberDto;
import com.society.member.dto.NomineeDto;
import com.society.member.entity.JointOwnerEntity;
import com.society.member.entity.MemberEntity;
import com.society.member.entity.NomineeEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberMapper {

    public MemberDto toDto(MemberEntity entity) {
        return toDto(entity, Collections.emptyList(), Collections.emptyList());
    }

    public MemberDto toDto(MemberEntity entity, List<JointOwnerEntity> jointOwners, List<NomineeEntity> nominees) {
        if (entity == null) {
            return null;
        }

        List<JointOwnerDto> jointOwnerDtos = jointOwners == null ? Collections.emptyList() :
                jointOwners.stream().map(this::toJointOwnerDto).collect(Collectors.toList());

        List<NomineeDto> nomineeDtos = nominees == null ? Collections.emptyList() :
                nominees.stream().map(this::toNomineeDto).collect(Collectors.toList());

        return new MemberDto(
                entity.getId(),
                entity.getMemberNumber(),
                entity.getMembershipNumber(),
                entity.getFirstName(),
                entity.getMiddleName(),
                entity.getLastName(),
                entity.getFatherOrSpouseName(),
                entity.getMobileNumber(),
                entity.getEmail(),
                entity.getGender(),
                entity.getDob(),
                entity.getOccupation(),
                entity.getEmergencyContactName(),
                entity.getEmergencyContactPhone(),
                entity.getMemberType(),
                entity.getAdmissionDate(),
                entity.getResolutionNumber(),
                entity.getResolutionDate(),
                entity.getPermanentAddress(),
                entity.getCorrespondenceAddress(),
                entity.getPhotoPath(),
                entity.getAadhaarDocPath(),
                entity.getPanDocPath(),
                entity.getAadhaarNumber(),
                entity.getPanNumber(),
                entity.getStatus(),
                entity.isActive(),
                jointOwnerDtos,
                nomineeDtos
        );
    }

    public MemberEntity toEntity(MemberDto dto) {
        if (dto == null) {
            return null;
        }

        MemberEntity entity = new MemberEntity();
        entity.setId(dto.id());
        entity.setMemberNumber(dto.memberNumber());
        entity.setMembershipNumber(dto.membershipNumber());
        entity.setFirstName(dto.firstName());
        entity.setMiddleName(dto.middleName());
        entity.setLastName(dto.lastName());
        entity.setFatherOrSpouseName(dto.fatherOrSpouseName());
        entity.setMobileNumber(dto.mobileNumber());
        entity.setEmail(dto.email());
        entity.setGender(dto.gender());
        entity.setDob(dto.dob());
        entity.setOccupation(dto.occupation());
        entity.setEmergencyContactName(dto.emergencyContactName());
        entity.setEmergencyContactPhone(dto.emergencyContactPhone());
        entity.setMemberType(dto.memberType());
        entity.setAdmissionDate(dto.admissionDate());
        entity.setResolutionNumber(dto.resolutionNumber());
        entity.setResolutionDate(dto.resolutionDate());
        entity.setPermanentAddress(dto.permanentAddress());
        entity.setCorrespondenceAddress(dto.correspondenceAddress());
        entity.setPhotoPath(dto.photoPath());
        entity.setAadhaarDocPath(dto.aadhaarDocPath());
        entity.setPanDocPath(dto.panDocPath());
        entity.setAadhaarNumber(dto.aadhaarNumber());
        entity.setPanNumber(dto.panNumber());
        entity.setStatus(dto.status());
        entity.setActive(dto.active());

        return entity;
    }

    public JointOwnerDto toJointOwnerDto(JointOwnerEntity entity) {
        if (entity == null) return null;
        return new JointOwnerDto(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getRelationship(),
                entity.getAadhaarNumber(),
                entity.getPanNumber()
        );
    }

    public NomineeDto toNomineeDto(NomineeEntity entity) {
        if (entity == null) return null;
        return new NomineeDto(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getRelationship(),
                entity.getSharePercentage()
        );
    }
}
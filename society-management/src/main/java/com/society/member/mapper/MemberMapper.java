package com.society.member.mapper;

import com.society.member.dto.MemberDto;
import com.society.member.entity.MemberEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public MemberDto toDto(MemberEntity entity) {

        if (entity == null) {
            return null;
        }

        return new MemberDto(
                entity.getId(),
                entity.getMemberNumber(),
                entity.getMembershipNumber(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getMobileNumber(),
                entity.getEmail(),
                entity.getAadhaarNumber(),
                entity.getPanNumber(),
                entity.getStatus(),
                entity.isActive()
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
        entity.setLastName(dto.lastName());
        entity.setMobileNumber(dto.mobileNumber());
        entity.setEmail(dto.email());
        entity.setAadhaarNumber(dto.aadhaarNumber());
        entity.setPanNumber(dto.panNumber());
        entity.setStatus(dto.status());
        entity.setActive(dto.active());

        return entity;
    }
}
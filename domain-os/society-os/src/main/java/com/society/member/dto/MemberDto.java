package com.society.member.dto;

import com.society.member.entity.MemberStatus;
import java.util.List;

public record MemberDto(
        Integer id,
        String memberNumber,
        String membershipNumber,
        String firstName,
        String middleName,
        String lastName,
        String fatherOrSpouseName,
        String mobileNumber,
        String email,
        String gender,
        String dob,
        String occupation,
        String emergencyContactName,
        String emergencyContactPhone,
        String memberType,
        String admissionDate,
        String resolutionNumber,
        String resolutionDate,
        String permanentAddress,
        String correspondenceAddress,
        String photoPath,
        String aadhaarDocPath,
        String panDocPath,
        String aadhaarNumber,
        String panNumber,
        MemberStatus status,
        boolean active,
        List<JointOwnerDto> jointOwners,
        List<NomineeDto> nominees
) {
    @Override
    public String toString() {
        String full = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        full = full.trim();
        String num = (membershipNumber != null && !membershipNumber.isBlank()) ? membershipNumber : memberNumber;
        if (num != null && !num.isBlank()) {
            return (full.isEmpty() ? "Member" : full) + " (" + num + ")";
        }
        return full.isEmpty() ? "Member #" + id : full;
    }
}
package com.society.member.dto;

import com.society.member.entity.MemberStatus;

public record MemberDto(

        Integer id,

        String memberNumber,

        String membershipNumber,

        String firstName,

        String lastName,

        String mobileNumber,

        String email,

        String aadhaarNumber,

        String panNumber,

        MemberStatus status,

        boolean active

) {
}
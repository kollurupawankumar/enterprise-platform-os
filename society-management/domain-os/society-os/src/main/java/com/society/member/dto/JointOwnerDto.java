package com.society.member.dto;

public record JointOwnerDto(
        Integer id,
        String firstName,
        String lastName,
        String relationship,
        String aadhaarNumber,
        String panNumber
) {}

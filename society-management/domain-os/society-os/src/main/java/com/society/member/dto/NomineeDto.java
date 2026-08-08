package com.society.member.dto;

public record NomineeDto(
        Integer id,
        String firstName,
        String lastName,
        String relationship,
        Double sharePercentage
) {}

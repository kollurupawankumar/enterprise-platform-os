package com.society.society.dto;

import com.society.society.entity.FinancialYearStartMonth;

public record SocietyDto(

        String name,

        String shortName,

        String registrationNumber,

        String addressLine1,

        String addressLine2,

        String city,

        String state,

        String pinCode,

        String phone,

        String email,

        String website,

        FinancialYearStartMonth financialYearStartMonth,

        String logoPath,

        String sealPath,

        Boolean active

) {
}
package com.society.property.dto;

public record OwnershipHistoryDto(
        Integer id,
        String ownerName,
        String fromDate,
        String toDate,
        String status
) {}

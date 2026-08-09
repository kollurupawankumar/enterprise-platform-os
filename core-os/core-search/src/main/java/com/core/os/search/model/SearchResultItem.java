package com.core.os.search.model;

public record SearchResultItem(
        String title,
        String category,
        String subtitle,
        String detail,
        String navigationModule
) {
}

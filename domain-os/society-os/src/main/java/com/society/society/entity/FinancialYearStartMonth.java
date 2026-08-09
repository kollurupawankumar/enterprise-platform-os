package com.society.society.entity;


public enum FinancialYearStartMonth {

    JANUARY(1),
    FEBRUARY(2),
    MARCH(3),
    APRIL(4),
    MAY(5),
    JUNE(6),
    JULY(7),
    AUGUST(8),
    SEPTEMBER(9),
    OCTOBER(10),
    NOVEMBER(11),
    DECEMBER(12);

    private final int monthNumber;

    FinancialYearStartMonth(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public static FinancialYearStartMonth fromMonthNumber(int monthNumber) {
        for (FinancialYearStartMonth month : values()) {
            if (month.monthNumber == monthNumber) {
                return month;
            }
        }

        throw new IllegalArgumentException(
                "Invalid month number: " + monthNumber);
    }
}
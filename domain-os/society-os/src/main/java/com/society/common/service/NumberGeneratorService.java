package com.society.common.service;

public interface NumberGeneratorService {

    String nextMemberNumber();

    String nextMembershipNumber();

    String formatNumber(String pattern, long sequence, String prefix);
}
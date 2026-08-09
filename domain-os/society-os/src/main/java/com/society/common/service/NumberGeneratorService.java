package com.society.common.service;

public interface NumberGeneratorService {

    String nextMemberNumber();

    String formatNumber(String pattern, long sequence, String prefix);
}
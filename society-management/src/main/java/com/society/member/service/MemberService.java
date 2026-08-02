package com.society.member.service;

import com.society.member.dto.MemberDto;

import java.util.List;

public interface MemberService {

    MemberDto save(MemberDto member);

    List<MemberDto> findAll();

    MemberDto findById(Integer id);

    void delete(Integer id);

    long count();

    String generateMemberNumber();

}
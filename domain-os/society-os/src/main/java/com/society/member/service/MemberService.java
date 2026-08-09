package com.society.member.service;

import com.society.member.dto.MemberDto;

import java.util.List;

public interface MemberService {

    /**
     * Register a new member.
     */
    MemberDto register(MemberDto member);

    /**
     * Update an existing member.
     */
    MemberDto update(MemberDto member);

    /**
     * Find all members.
     */
    List<MemberDto> findAll();

    /**
     * Find member by id.
     */
    MemberDto findById(Integer id);

    /**
     * Soft delete (deactivate) member.
     */
    void deactivate(Integer id);

    /**
     * Total members.
     */
    long count();

    /**
     * Generate next member number.
     */
    String generateMemberNumber();

    List<MemberDto> search(String keyword);

}
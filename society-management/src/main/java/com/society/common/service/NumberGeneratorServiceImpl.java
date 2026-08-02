package com.society.common.service;

import com.society.member.entity.MemberEntity;
import com.society.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NumberGeneratorServiceImpl implements NumberGeneratorService {

    private final MemberRepository memberRepository;

    public NumberGeneratorServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public String nextMemberNumber() {

        Optional<MemberEntity> lastMember =
                memberRepository.findFirstByOrderByIdDesc();

        if (lastMember.isEmpty()) {
            return "M000001";
        }

        String lastNumber = lastMember.get().getMemberNumber();

        if (lastNumber == null || lastNumber.isBlank()) {
            return "M000001";
        }

        int value = Integer.parseInt(lastNumber.substring(1));

        return String.format("M%06d", value + 1);

    }

}
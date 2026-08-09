package com.society.common.service;

import com.society.member.entity.MemberEntity;
import com.society.member.repository.MemberRepository;
import com.society.society.entity.SocietyEntity;
import com.society.society.repository.SocietyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class NumberGeneratorServiceImpl implements NumberGeneratorService {

    private final MemberRepository memberRepository;
    private final SocietyRepository societyRepository;

    public NumberGeneratorServiceImpl(MemberRepository memberRepository, SocietyRepository societyRepository) {
        this.memberRepository = memberRepository;
        this.societyRepository = societyRepository;
    }

    @Override
    public String nextMemberNumber() {
        Optional<MemberEntity> lastMember = memberRepository.findFirstByOrderByIdDesc();
        long nextSeq = lastMember.map(m -> m.getId() + 1L).orElse(1L);

        Optional<SocietyEntity> societyOpt = societyRepository.findFirstByActiveTrue();
        String pattern = societyOpt.map(SocietyEntity::getMemberNumberFormat).orElse("MEM-{SEQ}");
        String prefix = societyOpt.map(SocietyEntity::getShortName).orElse("MEM");

        return formatNumber(pattern, nextSeq, prefix);
    }

    @Override
    public String nextMembershipNumber() {
        Optional<MemberEntity> lastMember = memberRepository.findFirstByOrderByIdDesc();
        long nextSeq = lastMember.map(m -> m.getId() + 1L).orElse(1L);

        Optional<SocietyEntity> societyOpt = societyRepository.findFirstByActiveTrue();
        String pattern = societyOpt.map(SocietyEntity::getMembershipNumberFormat).orElse("SSTS/{YEAR}/{SEQ}");
        String prefix = societyOpt.map(SocietyEntity::getShortName).orElse("SST");

        return formatNumber(pattern, nextSeq, prefix);
    }

    @Override
    public String formatNumber(String pattern, long sequence, String prefix) {
        if (pattern == null || pattern.isBlank()) {
            pattern = "MEM-{SEQ}";
        }

        LocalDate now = LocalDate.now();
        String year4 = String.valueOf(now.getYear());
        String year2 = year4.substring(2);
        String seqPadded = String.format("%05d", sequence);
        String prefixClean = (prefix != null && !prefix.isBlank()) ? prefix.trim() : "SYS";

        return pattern
                .replace("{YEAR}", year4)
                .replace("{YY}", year2)
                .replace("{SEQ}", seqPadded)
                .replace("{PREFIX}", prefixClean);
    }
}
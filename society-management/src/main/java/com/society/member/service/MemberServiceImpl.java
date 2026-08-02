package com.society.member.service;

import com.society.common.service.NumberGeneratorService;
import com.society.member.dto.MemberDto;
import com.society.member.entity.MemberEntity;
import com.society.member.mapper.MemberMapper;
import com.society.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;
    private final MemberMapper mapper;
    private final NumberGeneratorService numberGeneratorService;

    public MemberServiceImpl(
            MemberRepository repository,
            MemberMapper mapper,
            NumberGeneratorService numberGeneratorService) {

        this.repository = repository;
        this.mapper = mapper;
        this.numberGeneratorService = numberGeneratorService;
    }

    @Override
    public MemberDto save(MemberDto dto) {

        MemberEntity entity = mapper.toEntity(dto);

        MemberEntity saved = repository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    public List<MemberDto> findAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();

    }

    @Override
    public MemberDto findById(Integer id) {

        return repository.findById(id)
                .map(mapper::toDto)
                .orElse(null);

    }

    @Override
    public void delete(Integer id) {

        repository.deleteById(id);

    }

    @Override
    public long count() {

        return repository.count();

    }

    @Override
    public String generateMemberNumber() {

        return numberGeneratorService.nextMemberNumber();

    }

}
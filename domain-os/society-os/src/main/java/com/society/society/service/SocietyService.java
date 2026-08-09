package com.society.society.service;

import com.society.society.dto.SocietyDto;

import java.util.Optional;

public interface SocietyService {

    Optional<SocietyDto> getSociety();

    SocietyDto save(SocietyDto society);

    boolean exists();

}
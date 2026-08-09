package com.society.society.runtime;

import com.society.society.dto.SocietyDto;

public interface SocietyProvider {

    SocietyDto getCurrentSociety();

    boolean hasSociety();

    void refresh();
}
package com.society.society.runtime;

import com.society.society.dto.SocietyDto;
import com.society.society.service.SocietyService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DefaultSocietyProvider implements SocietyProvider {

    private final SocietyService societyService;

    private SocietyDto currentSociety;

    public DefaultSocietyProvider(SocietyService societyService) {
        this.societyService = societyService;
    }

    @PostConstruct
    @Override
    public void refresh() {
        currentSociety = societyService.getSociety().orElse(null);
    }

    @Override
    public SocietyDto getCurrentSociety() {
        return currentSociety;
    }

    @Override
    public boolean hasSociety() {
        return currentSociety != null;
    }
}
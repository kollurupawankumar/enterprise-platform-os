package com.society.society.context;

import com.society.society.dto.SocietyDto;
import com.society.society.service.SocietyService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SocietyContext {

    private final SocietyService societyService;

    private SocietyDto currentSociety;

    public SocietyContext(SocietyService societyService) {
        this.societyService = societyService;
    }

    @PostConstruct
    public void initialize() {
        refresh();
    }

    public void refresh() {
        currentSociety = societyService.getSociety().orElse(null);
    }

    public SocietyDto getCurrentSociety() {
        return currentSociety;
    }

    public boolean hasSociety() {
        return currentSociety != null;
    }
}
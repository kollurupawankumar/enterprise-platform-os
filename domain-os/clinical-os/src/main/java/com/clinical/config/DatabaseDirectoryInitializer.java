package com.clinical.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class DatabaseDirectoryInitializer {

    @PostConstruct
    public void init() {
        File dbDir = new File("database");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
    }
}

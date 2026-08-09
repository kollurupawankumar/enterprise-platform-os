package com.core.os.ai.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LocalAiEngineServiceTest {

    @Test
    void testLocalAiEngineServiceInitialization() {
        LocalAiEngineService service = new LocalAiEngineService();
        assertNotNull(service);
    }

    @Test
    void testStopLocalAiServerSafelyWhenNotStarted() {
        LocalAiEngineService service = new LocalAiEngineService();
        assertDoesNotThrow(service::stopLocalAiServer);
    }
}

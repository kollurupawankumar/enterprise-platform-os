package com.core.os.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Service
public class LocalAiEngineService {

    private static final Logger log = LoggerFactory.getLogger(LocalAiEngineService.class);
    private Process aiProcess;

    public void startLocalAiServer() {
        String os = System.getProperty("os.name").toLowerCase();
        String binaryName;
        String osFolder;

        if (os.contains("win")) {
            osFolder = "windows";
            binaryName = "llama-server.exe";
        } else if (os.contains("mac")) {
            osFolder = "mac";
            binaryName = "llama-server";
        } else {
            osFolder = "linux";
            binaryName = "llama-server";
        }

        File executable = Paths.get("ai", osFolder, binaryName).toFile();
        File modelFile = Paths.get("ai", "models", "society-ai.gguf").toFile();

        if (executable.exists() && modelFile.exists()) {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        executable.getAbsolutePath(),
                        "-m", modelFile.getAbsolutePath(),
                        "--port", "8080",
                        "-c", "4096"
                );
                this.aiProcess = pb.start();
                log.info("Local AI Engine started successfully on port 8080 ({})", osFolder);
            } catch (IOException e) {
                log.error("Failed to start local AI server: {}", e.getMessage(), e);
            }
        } else {
            log.info("Local AI binary or model not found under ./ai directory. Running without offline AI.");
        }
    }

    public void stopLocalAiServer() {
        if (aiProcess != null && aiProcess.isAlive()) {
            aiProcess.destroyForcibly();
            log.info("Local AI Engine process stopped.");
        }
    }
}

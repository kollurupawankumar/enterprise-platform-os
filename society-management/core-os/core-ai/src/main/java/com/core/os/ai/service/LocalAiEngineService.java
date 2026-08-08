package com.core.os.ai.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Service
public class LocalAiEngineService {

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
                System.out.println("🤖 Local AI Engine started successfully (" + osFolder + ")");
            } catch (IOException e) {
                System.err.println("Failed to start local AI server: " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️ Local AI binary or model not found under ./ai directory. Running without offline AI.");
        }
    }

    public void stopLocalAiServer() {
        if (aiProcess != null && aiProcess.isAlive()) {
            aiProcess.destroyForcibly();
        }
    }
}

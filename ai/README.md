# Offline Local AI Subsystem Directory Structure

Place your cross-platform `llama-server` binaries and `.gguf` quantized models here:

```
ai/
├── windows/
│   └── llama-server.exe       <-- Downloaded llama.cpp server for Windows x64
├── mac/
│   └── llama-server           <-- Downloaded llama.cpp server for macOS (ARM64/Intel)
├── linux/
│   └── llama-server           <-- Downloaded llama.cpp server for Linux x64
└── models/
    └── society-ai.gguf        <-- Quantized Llama-3 / Mistral / Qwen GGUF model
```

### Official Download Links:
- **`llama-server` binaries**: Download from [llama.cpp releases](https://github.com/ggerganov/llama.cpp/releases).
- **GGUF Models**: Download any 4-bit quantized GGUF model (e.g., `Llama-3.2-3B-Instruct-Q4_K_M.gguf`) from HuggingFace and rename to `society-ai.gguf` under `ai/models/`.

### Automatic Execution:
When `LocalAiEngineService` starts up:
1. It automatically detects the host OS (`windows`, `mac`, or `linux`).
2. It executes `./ai/{os}/llama-server -m ./ai/models/society-ai.gguf --port 8080`.
3. If binaries are absent, the application gracefully skips offline AI and logs an informational notice.

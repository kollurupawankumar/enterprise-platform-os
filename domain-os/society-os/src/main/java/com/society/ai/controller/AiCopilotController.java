package com.society.ai.controller;

import com.core.os.ai.service.LocalAiEngineService;
import com.society.document.service.DocumentService;
import com.society.knowledge.repository.KnowledgeRepository;
import com.society.member.service.MemberService;
import com.society.operations.service.OperationsService;
import com.society.society.service.SocietyService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class AiCopilotController {

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField inputField;

    @FXML
    private Button sendButton;

    @FXML
    private VBox copilotDrawer;

    private final LocalAiEngineService aiEngineService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final MemberService memberService;
    private final OperationsService operationsService;
    private final SocietyService societyService;
    private final DocumentService documentService;
    private final KnowledgeRepository knowledgeRepository;

    private Runnable onCloseHandler;

    public AiCopilotController(
            LocalAiEngineService aiEngineService,
            MemberService memberService,
            OperationsService operationsService,
            SocietyService societyService,
            DocumentService documentService,
            KnowledgeRepository knowledgeRepository) {
        this.aiEngineService = aiEngineService;
        this.memberService = memberService;
        this.operationsService = operationsService;
        this.societyService = societyService;
        this.documentService = documentService;
        this.knowledgeRepository = knowledgeRepository;
    }

    public void setOnCloseHandler(Runnable onCloseHandler) {
        this.onCloseHandler = onCloseHandler;
    }

    @FXML
    public void initialize() {
        chatArea.appendText("🤖 Society OS AI Copilot initialized.\n");
        chatArea.appendText("Ask me anything about members, assets, staff, society rules, or financial records!\n\n");
    }

    @FXML
    private void handleCloseCopilot() {
        if (onCloseHandler != null) {
            onCloseHandler.run();
        }
    }

    @FXML
    private void handleSend() {
        String prompt = inputField.getText().trim();
        if (prompt.isEmpty()) return;

        chatArea.appendText("👤 You: " + prompt + "\n");
        inputField.clear();
        chatArea.appendText("🤖 AI Thinking...\n");

        new Thread(() -> {
            String response = queryLocalAiEngine(prompt);
            Platform.runLater(() -> {
                chatArea.appendText("🤖 AI: " + response + "\n\n");
            });
        }).start();
    }

    private String queryLocalAiEngine(String userPrompt) {
        try {
            String url = "http://localhost:8080/v1/chat/completions";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String ragContext = buildSystemContext();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "society-ai");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            sysMsg.put("content", "You are the Society OS Assistant. Use the provided real-time Society Data Context below to answer user queries accurately:\n\n" + ragContext);
            messages.add(sysMsg);

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userPrompt);
            messages.add(userMsg);

            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List choices = (List) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map firstChoice = (Map) choices.get(0);
                    Map message = (Map) firstChoice.get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            return "🤖 Local AI Engine offline. Simulated AI Response for prompt: \"" + userPrompt + "\"";
        }
        return "No response from AI Engine.";
    }

    private String buildSystemContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SOCIETY DATA CONTEXT ===\n");

        try {
            var societyOpt = societyService.getSociety();
            if (societyOpt.isPresent()) {
                var society = societyOpt.get();
                sb.append("Society Name: ").append(society.name()).append("\n");
                sb.append("Address: ").append(society.addressLine1()).append(", ").append(society.city()).append("\n");
                sb.append("Registration Number: ").append(society.registrationNumber()).append("\n");
            }
        } catch (Exception ignored) {}

        try {
            var members = memberService.findAll();
            sb.append("\nTotal Members Registered: ").append(members.size()).append("\n");
            int count = 0;
            for (var m : members) {
                if (count++ >= 10) break; // Limit context payload size
                sb.append("- ").append(m.firstName()).append(" ").append(m.lastName())
                        .append(" (Member No: ").append(m.memberNumber()).append(")\n");
            }
        } catch (Exception ignored) {}

        try {
            var assets = operationsService.getAllAssets();
            sb.append("\nAssets & Facilities Count: ").append(assets.size()).append("\n");
        } catch (Exception ignored) {}

        try {
            var docs = documentService.getAllDocuments();
            sb.append("\nDocuments Uploaded: ").append(docs.size()).append("\n");
        } catch (Exception ignored) {}

        try {
            var kbList = knowledgeRepository.findAll();
            sb.append("\nKnowledge Articles / SOPs: ").append(kbList.size()).append("\n");
        } catch (Exception ignored) {}

        return sb.toString();
    }
}

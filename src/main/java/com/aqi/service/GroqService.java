package com.aqi.service;

import com.aqi.dto.chat.ChatRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GroqService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final ObjectMapper mapper;

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    public GroqService(RestTemplate restTemplate, @Value("${app.groq.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.mapper = new ObjectMapper();
    }

    public String getChatResponse(String userMessage, String contextData, List<ChatRequest.MessageContext> history) {
        try {
            if (apiKey == null || apiKey.isEmpty() || apiKey.contains("WAQI_API_KEY")) {
                return "Configuration Error: API Key missing.";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", MODEL);

            ArrayNode messages = requestBody.putArray("messages");

            // 1. SYSTEM PROMPT (The Guardrails)
            ObjectNode systemMessage = messages.addObject();
            systemMessage.put("role", "system");
            systemMessage.put("content",
                    "You are an expert air quality health assistant. " +
                            "Current Real-time Data: " + contextData + ". " +
                            "RULES: " +
                            "1. You can NOT change user settings or location. If asked, tell them to use the Settings page. " +
                            "2. Base your advice ONLY on the provided 'Current Real-time Data'. " +
                            "3. If the user asks about a different city, say you only have access to their home city data. " +
                            "4. Be concise, serious, and medical."
            );

            // 2. HISTORY (The Memory)
            if (history != null) {
                for (ChatRequest.MessageContext msg : history) {
                    ObjectNode historyNode = messages.addObject();
                    historyNode.put("role", msg.getRole());
                    historyNode.put("content", msg.getContent());
                }
            }

            // 3. CURRENT USER MESSAGE
            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

            JsonNode response = restTemplate.postForObject(API_URL, entity, JsonNode.class);

            if (response != null && response.has("choices")) {
                return response.path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();
            }

        } catch (Exception e) {
            System.err.println("[GroqService] Error: " + e.getMessage());
            return "I'm having trouble processing that right now.";
        }
        return "No response from AI.";
    }
}
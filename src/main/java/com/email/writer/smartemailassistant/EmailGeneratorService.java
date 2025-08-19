package com.email.writer.smartemailassistant;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl ;
    @Value("${gemini.api.key}")
    private String geminiApiKey ;

    private final WebClient webClient;

    public EmailGeneratorService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String generateEmailReply(EmailRequest emailRequest){
        String prompt = buildPrompt(emailRequest);
        Map<String,Object>requestBody = Map.of("contents",new Object[]{
        Map.of("parts",new Object[]{
                Map.of("text", prompt)
        })
});
String response = webClient.post()
                .uri(geminiApiUrl)
                .header("X-goog-api-key" , geminiApiKey).header("Content-Type", "application/json").bodyValue(requestBody)
                .retrieve().bodyToMono(String.class).block();

        return extractResponseContent(response);

    }

    private String extractResponseContent(String response) {

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        }catch (Exception e){
            return "Error processing response: " + e.getMessage();
        }
    }


    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply based on the following email content:\n");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Use a  ").append(emailRequest.getTone()).append(" tone. \n");
        }
        prompt.append("Orignal Email Content:\n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}

package myaong.popolog.psservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
public class OpenAIClient {

    @Value("${openai.api.key}")
    private String apiKey; // application.properties 또는 application.yml에서 가져오기

    @Value("${openai.api.url}")
    private String apiUrl; // application.properties 또는 application.yml에서 가져오기

    private final RestTemplate restTemplate;

    public String callChatGPT(String role, String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); // API 키 설정

        String requestBody = createRequestBody(role, prompt);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            throw new IllegalArgumentException("OpenAI API 호출 실패: " + e.getMessage(), e);
        }
    }

    private String createRequestBody(String role, String prompt) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", "gpt-3.5-turbo");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", role);
            message.put("content", prompt);
            messages.add(message);

            requestMap.put("messages", messages);

            return objectMapper.writeValueAsString(requestMap);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON 생성 실패: " + e.getMessage(), e);
        }
    }

    private String parseResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(responseBody)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText()
                    .trim();
        } catch (Exception e) {
            throw new IllegalArgumentException("OpenAI API 응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}

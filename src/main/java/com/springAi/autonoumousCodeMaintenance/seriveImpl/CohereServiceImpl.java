package com.springAi.autonoumousCodeMaintenance.seriveImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springAi.autonoumousCodeMaintenance.exception.AnalysisException;
import com.springAi.autonoumousCodeMaintenance.exception.EmbeddingException;
import com.springAi.autonoumousCodeMaintenance.exception.NoResponseException;
import com.springAi.autonoumousCodeMaintenance.model.AnalysisResponse;
import com.springAi.autonoumousCodeMaintenance.model.EmbeddingResponse;
import com.springAi.autonoumousCodeMaintenance.service.CohereService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class CohereServiceImpl implements CohereService {

    @Value("${cohere.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.cohere.ai/v2")
            .build();

    public AnalysisResponse chat(String prompt) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        String filePath, issue, rootCause, fix, impact;
        int confidence;

        try {
            Map response = webClient.post()
                    .uri("/chat")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of(
                            "model", "command-a-03-2025",
                            "messages", List.of(
                                    Map.of(
                                            "role", "user",
                                            "content", prompt
                                    )
                            )
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Extract text
            Map message = (Map) response.get("message");
            List content = (List) message.get("content");
            Map first = (Map) content.get(0);
            String text = first.get("text").toString();

            // Clean markdown
            String cleaned = text
                    .replaceAll("\\*\\*", "")
                    .replaceAll("`", "")
                    .trim();

            String json = extractJson(cleaned);
            JsonNode node = mapper.readTree(json);

            // Extract fields
            String file = node.get("filePath").asText();
            issue = node.get("issue").asText();
            rootCause = node.get("rootCause").asText();
            fix = node.get("fix").asText();
            impact = node.get("impact").asText();
            confidence = node.get("confidence").asInt();

            int index = file.indexOf("src/main");
            filePath = null;
            if (index != -1) {
                filePath = file.substring(index);
                System.out.println(filePath);
            } else {
                System.out.println("src/main not found in path");
            }

        } catch (Exception exception) {
            throw new AnalysisException("Failed to analyze AI Response for relevant code", exception);
        }
        return new AnalysisResponse(filePath, issue, rootCause, fix, impact, confidence);
    }

    private String extractJson(String text) {
        return text
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .replaceFirst("^\\s*json\\s*", "")
                .trim();
    }

    public List<Double> embed(String text) {
        if (text.length() > 2000) {
            text = text.substring(0, 2000);
        }

        final Map<String, Object> body = getStringObjectMap(text);

        System.out.println(" Sending request to Cohere...");
        System.out.println(body);

        EmbeddingResponse   response = webClient.post()
                    .uri("https://api.cohere.ai/v2/embed")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(
                                            new EmbeddingException(
                                                    clientResponse.statusCode().value(),
                                                    errorBody
                                            )
                                    ))
                    )
                    .bodyToMono(EmbeddingResponse.class)
                    .block();
            System.out.println("Cohere response received");

        if(response == null || response.getEmbeddings() == null){
            throw new NoResponseException("No response from Cohere API for Embeddings");
        }

        System.out.println("Cohere Embed Api Response" + response.getEmbeddings().getFloatValues().get(0));

        return response.getEmbeddings().getFloatValues().get(0);
    }

    private static Map<String, Object> getStringObjectMap(String text) {
        final Map<String, Object> content = Map.of(
                "type", "text",
                "text", text
        );
        final Map<String, Object> input = Map.of(
                "content", List.of(content)
        );

        final Map<String, Object> body = Map.of(
                 "model", "embed-v4.0",
                 "input_type", "search_document",
                 "embedding_types", List.of("float"),
                 "inputs", List.of(input)
         );

        return body;
    }
}
package com.springAi.autonoumousCodeMaintenance.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.springAi.autonoumousCodeMaintenance.model.Patch;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateFile {
    private final DiffApplier diffApplier = new DiffApplier();

    public void updateCode(String path, String content, String branch,
                           String repo, String token, WebClient webClient, List<Patch> patches) {


        try {
            // STEP 1: Get Latest SHA
            String sha = getFileSha(repo, path, branch, webClient);

            // STEP 2: APPLY PATCH IF AVAILABLE
            String finalContent;

            if (patches != null && !patches.isEmpty()) {
                System.out.println("⚡ Using DIFF PATCH mode");

                finalContent = diffApplier.applyPatch(content, patches);

            } else {
                System.out.println("⚡ Using FULL REWRITE mode");

                finalContent = content;
            }

            // STEP 3: ENCODE CONTENT
            String encodedContent = Base64.getEncoder()
                    .encodeToString(finalContent.getBytes(StandardCharsets.UTF_8));

            // STEP 4: BUILD REQUEST
            Map<String, Object> body = new HashMap<>();
            body.put("message", "🤖 AI Fix: automated repair");
            body.put("content", encodedContent);
            body.put("branch", branch);

            // only include sha if file exists
            if (sha != null) {
                body.put("sha", sha);
            }

            // STEP 5: CALL GITHUB API
            String uri = "/repos/" + repo + "/contents/" + path;
            webClient.put()
                    .uri(uri)
                    .header("Authorization", "token " + token)
                    .header("Accept", "application/vnd.github+json")
                    // .uri("/repos/{repo}/contents/{path}", repo, path)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            System.out.println("Success: File updated -> " + path);
            return;

        } catch (WebClientResponseException.Conflict e) {
            System.out.println("Conflict detected.");

           /* if (attempt >= maxRetries) {
                throw new RuntimeException("Failed after retries due to conflicts", e);
            }*/

        } catch (Exception e) {
            throw new RuntimeException("GitHub update failed", e);
        }
        /* String encoded = Base64.getEncoder().encodeToString(newContent.getBytes());

        webClient.put()
                .uri("/repos/" + repo + "/contents/" + path)
                .header("Authorization", "Bearer " + token)
                .bodyValue(Map.of(
                        "message", "🤖 AI Fix: automated repair",
                        "content", encoded,
                        "sha", sha,
                        "branch", branch
                ))
                .retrieve()
                .bodyToMono(Void.class)
                .block();*/
    }

    public String getFileSha(String repo, String path, String branch, WebClient webClient) {
        try {
            String uri = "/repos/" + repo + "/contents/" + path + "?ref=" + branch;
            return webClient.get()
                    .uri(uri)
                    /* .uri(uriBuilder -> uriBuilder
                             .path("/repos/{repo}/contents/{path}")
                             .queryParam("ref", branch)
                             .build(false, repo, path))*/
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(json -> json.get("sha").asText())
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            return null; // file does not exist
        }
    }
}

package com.springAi.autonoumousCodeMaintenance.util;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FetchLogs {
    public String fetchLogs(String repo, Long runId, String token, WebClient webClient) {

        String url = "https://api.github.com/repos/" + repo +
                "/actions/runs/" + runId + "/logs";

        byte[] zipBytes = webClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(token))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(byte[].class);
                    }

                    // Handle redirect (THIS IS YOUR CASE)
                    if (response.statusCode().is3xxRedirection()) {
                        String redirectUrl = Objects.requireNonNull(response.headers()
                                        .asHttpHeaders()
                                        .getLocation())
                                .toString();

                        return webClient.get()
                                .uri(redirectUrl)
                                .retrieve()
                                .bodyToMono(byte[].class);
                    }

                    // Real error
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("No error body")
                            .flatMap(body -> Mono.error(
                                    new RuntimeException("GitHub API Error: " + body)
                            ));
                })
                .block();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {

            StringBuilder logs = new StringBuilder();

            while (zis.getNextEntry() != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
                String line;

                while ((line = reader.readLine()) != null) {
                    logs.append(line).append("\n");
                }
            }

            return logs.toString();

        } catch (Exception e) {
            return "Failed to read logs: " + e.getMessage();
        }
    }
}

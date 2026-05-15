package com.springAi.autonoumousCodeMaintenance.util;

import com.springAi.autonoumousCodeMaintenance.exception.PrReviewerException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

public class Reviewer {
    void assignReviewer(Integer prNumber, String approver, WebClient webClient,
                        String repo, String token) {
        String uri = "/repos/" + repo + "/pulls/" + prNumber + "/requested_reviewers";
        webClient.post()
                .uri(uri)
                .header("Authorization", "token " + token)
                .header("Accept", "application/vnd.github+json")
                .bodyValue(Map.of("reviewers", List.of(approver)))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .map(body -> new PrReviewerException("GitHub Error: " + body))
                )
                .bodyToMono(Void.class)
                .block();
    }
}

package com.springAi.autonoumousCodeMaintenance.util;

import com.springAi.autonoumousCodeMaintenance.exception.PrCreationException;
import com.springAi.autonoumousCodeMaintenance.model.AnalysisResponse;
import com.springAi.autonoumousCodeMaintenance.model.PrResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public record CreatePr(FormatPr formatPr, Reviewer reviewer, RepoOwner repoOwner) {

    public PrResponse CreatePrAddApprover(String branch, AnalysisResponse analysisResponse,
                                    String approver, WebClient webClient, String repo, String token,
                                    String baseBranch) {
        String prReviewer = "";
        Map pr = webClient.post()
                    .uri("/repos/" + repo + "/pulls")
                    .header("Authorization", "token " + token)
                    .bodyValue(Map.of(
                            "title", "AI Fix: " + analysisResponse.getIssue(),
                            "head", branch,
                            "base", baseBranch,
                            "body", formatPr.pRFormat(analysisResponse)
                    ))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(String.class)
                                    .map(body -> new PrCreationException("GitHub Error: " + body))
                    )
                    .bodyToMono(Map.class)
                    .block();

        Integer prNumber = (Integer) pr.get("number");

        if (approver == null || approver.isBlank()) {
            approver = repoOwner.get(webClient, repo, token);
        }

        String prAuthor = ((Map) pr.get("user")).get("login").toString();
        System.out.println("prNumber: " + prNumber);
        System.out.println("PR approver: " + approver);
        if (!approver.equalsIgnoreCase(prAuthor)) {
            reviewer.assignReviewer(prNumber, approver, webClient, repo, token);
            prReviewer = approver;
        } else {
            System.out.println("Skipping reviewer: cannot assign PR author");
            prReviewer = prAuthor;
        }

        return new PrResponse(prNumber, prReviewer);
    }
}

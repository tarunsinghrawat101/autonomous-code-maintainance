package com.springAi.autonoumousCodeMaintenance.util;

import com.springAi.autonoumousCodeMaintenance.exception.CreateBranchException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class CreateBranch {
    public String newBranch(WebClient webClient, String issue, String repo, String token, String baseBranch) {
        String branch;
        try {
            Map ref = webClient.get()
                    .uri("/repos/" + repo + "/git/ref/heads/" + baseBranch)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            assert ref != null;
            String sha = ((Map<?, ?>) ref.get("object")).get("sha").toString();
            branch = "pr-" + System.currentTimeMillis();

            webClient.post()
                    .uri("/repos/" + repo + "/git/refs")
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .bodyValue(Map.of(
                            "ref", "refs/heads/" + branch,
                            "sha", sha
                    ))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("GitHub Error: " + body))
                    )
                    .bodyToMono(Void.class)
                    .block();
        }catch (Exception exception){
            throw new CreateBranchException("Failed to create branch", exception);
        }
        return branch;
    }
}

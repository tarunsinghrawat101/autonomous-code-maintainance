package com.springAi.autonoumousCodeMaintenance.util;

import com.springAi.autonoumousCodeMaintenance.exception.GitHubFileNotReadableException;
import com.springAi.autonoumousCodeMaintenance.model.GithubFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

public class ReadFile {
    public GithubFile readGitHubFile(WebClient webClient, String filePath, String repo, String baseBranch, String token) {
        String decoded = "";
        String sha =  "";
        Map res = Map.of();
        try {
            res = webClient.get()
                    .uri("/repos/" + repo + "/contents/" + filePath + "?ref=" + baseBranch)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            decoded = new String(Base64.getDecoder()
                    .decode(res != null ? res.get("content").toString().replace("\n", "") : null));
            sha = res != null ? res.get("sha").toString() : null;
        }catch (GitHubFileNotReadableException exception){
            throw  new GitHubFileNotReadableException(exception.getMessage());
        }

        return new GithubFile(decoded, sha);
    }
}

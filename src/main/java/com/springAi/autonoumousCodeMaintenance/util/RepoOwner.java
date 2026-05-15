package com.springAi.autonoumousCodeMaintenance.util;

import com.springAi.autonoumousCodeMaintenance.exception.NoOwnerFoundException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class RepoOwner {
    String get(WebClient webClient, String repo, String token) {
        Map owner;
        try {
            Map repoDetails = webClient.get()
                    .uri("/repos/" + repo)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            owner = (Map) repoDetails.get("owner");
        }catch (Exception e){
            throw new NoOwnerFoundException("Repo Owner Not Found", e);
        }

        return owner.get("login").toString();
    }
}

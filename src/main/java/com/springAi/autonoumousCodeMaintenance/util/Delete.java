package com.springAi.autonoumousCodeMaintenance.util;

import org.springframework.web.reactive.function.client.WebClient;

public class Delete {
    public void deleteBranch(WebClient webClient, String repo, String token, String branch) {
        try {
            webClient.delete()
                    .uri("/repos/" + repo + "/git/refs/heads/" + branch)
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/vnd.github+json")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            System.out.println("✅ Rolled back branch: " + branch);

        } catch (Exception ex) {
            System.out.println("⚠️ Failed to delete branch: " + branch);
            ex.printStackTrace();
        }
    }
}

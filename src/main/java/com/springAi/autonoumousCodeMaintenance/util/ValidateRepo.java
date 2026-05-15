package com.springAi.autonoumousCodeMaintenance.util;

import com.springAi.autonoumousCodeMaintenance.exception.EmbeddingException;
import com.springAi.autonoumousCodeMaintenance.exception.RepoNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ValidateRepo {
    public void validateRepository(WebClient webClient, String repo, String token){
            webClient.get()
                    .uri("/repos/" + repo)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(
                                            new RepoNotFoundException(
                                                    "No repo found for given logs"
                                            )
                                    ))
                    )
                    .bodyToMono(Map.class)
                    .block();
    }
}

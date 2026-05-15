package com.springAi.autonoumousCodeMaintenance.service;

import com.springAi.autonoumousCodeMaintenance.model.AnalysisResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InterruptedIOException;

public interface GithubService {
    String processFix(AnalysisResponse ai, String approver, WebClient webClient, String repo, String token,
                    String baseBranch, String newBranch) throws InterruptedIOException, InterruptedException;
}

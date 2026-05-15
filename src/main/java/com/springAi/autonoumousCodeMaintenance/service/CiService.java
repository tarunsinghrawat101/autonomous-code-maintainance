package com.springAi.autonoumousCodeMaintenance.service;

import org.springframework.web.reactive.function.client.WebClient;

import java.io.InterruptedIOException;

public interface CiService {
    //public String compile(String repoPath, String branch);
    public String waitForBuild(String repo, String branch, String token, WebClient webClient) throws InterruptedIOException, InterruptedException;
}

package com.springAi.autonoumousCodeMaintenance.seriveImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springAi.autonoumousCodeMaintenance.exception.*;
import com.springAi.autonoumousCodeMaintenance.model.*;
import com.springAi.autonoumousCodeMaintenance.service.*;
import com.springAi.autonoumousCodeMaintenance.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InterruptedIOException;
import java.util.List;
import java.util.Objects;

@Service
public class AutoFixServiceImpl implements AutoFixService {
    @Value("${github.token}")
    private String token;

    @Value("${github.repo}")
    private String repo;

    @Value("${github.base-branch}")
    private String baseBranch;

    private final CodeSearchServiceImpl searchService;
    private final LogAnalysisServiceImpl logAnalysisService;
    private final GithubServiceImpl githubService;
    private final CompileExtractErrorServiceImpl compileExtractErrorService;

    public AutoFixServiceImpl(CodeSearchServiceImpl searchService, LogAnalysisServiceImpl logAnalysisService1,
                              GithubServiceImpl githubService, CompileExtractErrorServiceImpl compileExtractErrorService) {
        this.searchService = searchService;
        this.logAnalysisService = logAnalysisService1;
        this.githubService = githubService;
        this.compileExtractErrorService = compileExtractErrorService;
    }

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.github.com")
            .build();

    @Override
    public FixResponse process(String logs, String approver) throws JsonProcessingException, InterruptedIOException, InterruptedException {
        int maxAttempts = 2;
        int attempt = 0;

        // 1. Find relevant code from embeddings
        if(logs.isEmpty()){
            throw new NoLogsException("Input Logs are empty");
        }
        List<CodeResult> result = searchService.searchRelevantCode(logs);
        System.out.println("Searching of relevant code on the basis of logs COMPLETED");

        if (result == null
                || result.isEmpty()
                || result.get(0).filePath() == null
                || result.get(0).filePath().isEmpty()) {

            throw new RepoNotFoundException("No repo found for given logs");

        }else if (result.get(0).content() == null
                || result.get(0).content().isEmpty()) {

            throw new RelevantCodeNotFoundException("No Relevant code Found");
        }

        String filePath = result.get(0).filePath();
        String codeChunk = result.get(0).content();

        // 2. AI analysis
        AnalysisResponse analysisResponse = logAnalysisService.analyze(logs, codeChunk, filePath);
        System.out.println("Analysis of log is COMPLETED");

        // 1. Validate Repo
        ValidateRepo validateRepo = new ValidateRepo();
        validateRepo.validateRepository(webClient, repo, token);
        System.out.println(repo + " existed over GitHub");

        // 2. Create new Branch for fix
        CreateBranch createBranch = new CreateBranch();
        String newBranch = createBranch.newBranch(webClient, analysisResponse.getIssue(), repo, token, baseBranch);
        System.out.println("Branch created SUCCESSFULLY. Branch name: " + newBranch);

        // 3. Apply fix via GitHub
        String compilerMsg = githubService.processFix(analysisResponse, approver, webClient, repo, token, baseBranch, newBranch);
        System.out.println("Compile Status: " + compilerMsg);

        if (!compilerMsg.equalsIgnoreCase("Success")) {
            while (attempt < maxAttempts) {
                attempt++;

                CompileError error = compileExtractErrorService.extractError(compilerMsg);

                if (error.getFilePath() != null && !error.getFilePath().equals(filePath)) {
                    // Only NOW re-search
                    List<CodeResult> newResult = searchService.searchRelevantCode(error.getMessage());

                    if (newResult == null || newResult.isEmpty()) {
                        if (attempt >= maxAttempts) {
                            throw new RetriesMaxedException("No code results found after retries and code can't be compiled");
                        }
                        continue;
                    }
                    filePath = newResult.get(0).filePath();
                    codeChunk = newResult.get(0).content();

                    analysisResponse = logAnalysisService.analyze(error.getMessage(), codeChunk, filePath);

                    // Retry applying fix
                    compilerMsg = githubService.processFix(
                            analysisResponse,
                            approver,
                            webClient,
                            repo,
                            token,
                            baseBranch,
                            newBranch
                    );

                    // Compilation successful
                    if (compilerMsg.equalsIgnoreCase("Success")) {
                        break; //Success -> exit loop
                    }
                }

            }
            throw new NoCodeErrorException("No Error Found in code. Error may have occurred of code base. With Error: " + compilerMsg);
        }

        System.out.println("Compile SUCCESS. Proceeding to PR CREATION");

        // 6. Create PR for fix applied
        CreatePr createPR = new CreatePr(new FormatPr(), new Reviewer(), new RepoOwner());
        PrResponse prResponse = createPR.CreatePrAddApprover(newBranch, analysisResponse, approver, webClient, repo, token, baseBranch);

        // Final Response
        return new FixResponse(prResponse.getPrNumber(), prResponse.getReviewer(), attempt);
    }
}

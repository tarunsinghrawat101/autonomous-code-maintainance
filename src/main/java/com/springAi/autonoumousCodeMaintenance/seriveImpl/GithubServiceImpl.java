package com.springAi.autonoumousCodeMaintenance.seriveImpl;

import com.springAi.autonoumousCodeMaintenance.model.AnalysisResponse;
import com.springAi.autonoumousCodeMaintenance.model.GithubFile;
import com.springAi.autonoumousCodeMaintenance.model.RuntimeValidationResult;
import com.springAi.autonoumousCodeMaintenance.service.GithubService;
import com.springAi.autonoumousCodeMaintenance.service.RuntimeExecutionService;
import com.springAi.autonoumousCodeMaintenance.util.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InterruptedIOException;

@Service
public class GithubServiceImpl implements GithubService {
    private final CiServiceImpl ciService;
    private final RuntimeExecutionServiceImpl runtimeService;

    public GithubServiceImpl(CiServiceImpl ciService, RuntimeExecutionServiceImpl runtimeService) {
        this.ciService = ciService;
        this.runtimeService = runtimeService;
    }

    @Override
    public String processFix(AnalysisResponse analysisResponse, String approver, WebClient webClient, String repo,
                             String token, String baseBranch, String newBranch) throws InterruptedIOException, InterruptedException {

        String compileMsg;
        try {
            // 1. Read faulty file
            ReadFile readFile = new ReadFile();
            GithubFile file = readFile.readGitHubFile(webClient, analysisResponse.getFilePath(), repo, baseBranch, token);

            // 2. Apply fix
            FixIssue fix = new FixIssue();
            String updated = fix.applyASTFix(file.getContent(), analysisResponse);

            // 3. Update file with fix applied
            UpdateFile updateFile = new UpdateFile();
            updateFile.updateCode(analysisResponse.getFilePath(), updated, newBranch, repo, token, webClient, analysisResponse.getPatches());

            // 4. Runtime validation
            RuntimeValidationResult validationResult = runtimeService.validate(updated, analysisResponse);

            if (!validationResult.isSuccess()) {
                return "RUNTIME_VALIDATION_FAILED\n" + validationResult.getFailureReason();
            }


            // 5. compile code (CI)
            compileMsg = ciService.waitForBuild(repo, newBranch, token, webClient);

        } catch (Exception e) {
            compileMsg = "Error occurred while fixing the issue in the code: " + e.getMessage() + "\n" + e.getCause();

            System.out.println("Error occurred, rolling back branch: " + newBranch);
            e.printStackTrace();

            if (newBranch != null) {
                Delete delete = new Delete();
                delete.deleteBranch(webClient, repo, token, newBranch);
            }
        }
        return compileMsg;
    }
}


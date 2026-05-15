package com.springAi.autonoumousCodeMaintenance.seriveImpl;

import com.springAi.autonoumousCodeMaintenance.model.WorkFlowRun;
import com.springAi.autonoumousCodeMaintenance.service.CiService;
import com.springAi.autonoumousCodeMaintenance.util.FetchLogs;
import com.springAi.autonoumousCodeMaintenance.util.WorkFlow;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InterruptedIOException;

@Service
public class CiServiceImpl implements CiService {
    public String waitForBuild(String repo, String branch, String token, WebClient webClient) throws InterruptedIOException, InterruptedException {
        int delay = 5000;
        int maxAttempt = 12;

        for(int i = 0; i < maxAttempt; i++) {
            WorkFlow workFlow = new WorkFlow();
            FetchLogs logs = new FetchLogs();
            WorkFlowRun run = workFlow.getLatestWorkflowRun(repo, branch, token, webClient);

            if (run == null) {
                Thread.sleep(delay);
            }

            System.out.println("Status: " + run.getStatus());

            if ("completed".equalsIgnoreCase(run.getStatus())) {
                if ("success".equalsIgnoreCase((run.getConclusion()))) {
                    return "success";
                } else {
                    System.out.println("Check GitHub action as Conclusion is: " + run.getConclusion());
                    return logs.fetchLogs(repo, run.getId(), token, webClient);
                }
            }
            Thread.sleep(delay);
        }
        return "Build timeout";
    }
}

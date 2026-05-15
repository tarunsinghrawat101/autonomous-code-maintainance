package com.springAi.autonoumousCodeMaintenance.util;

import com.springAi.autonoumousCodeMaintenance.model.WorkFlowRun;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

public class WorkFlow {
    public WorkFlowRun getLatestWorkflowRun(String repo, String branch, String token, WebClient webClient) {
        String url = "https://api.github.com/repos/" + repo +
                "/actions/runs?branch=" + branch;

        Map response = webClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> runs = (List<Map<String, Object>>) response.get("workflow_runs");

        if (runs == null || runs.isEmpty()) return null;

        Map<String, Object> run = runs.get(0);

        WorkFlowRun wr = new WorkFlowRun();
        wr.setId(Long.valueOf(run.get("id").toString()));
        wr.setStatus((String) run.get("status"));
        wr.setConclusion((String) run.get("conclusion"));
        wr.setHeadSha((String) run.get("head_sha"));

        return wr;
    }
}

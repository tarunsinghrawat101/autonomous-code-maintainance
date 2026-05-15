package com.springAi.autonoumousCodeMaintenance.model;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisResponse {
    private String filePath;
    private String issue;
    private String rootCause;
    private String fix;
    private String impact;
    private int confidence;
    private List<Patch> patches;

    public AnalysisResponse(String filePath, String issue, String rootCause, String fix, String impact, int confidence ) {
        this.filePath = filePath;
        this.issue = issue;
        this.rootCause = rootCause;
        this.fix = fix;
        this.impact = impact;
        this.confidence = confidence;
    }
}
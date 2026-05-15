package com.springAi.autonoumousCodeMaintenance.model;

import lombok.Data;

@Data
public class WorkFlowRun {
    private Long id;
    private String status;      // queued, in_progress, completed
    private String conclusion;  // success, failure
    private String headSha;
}

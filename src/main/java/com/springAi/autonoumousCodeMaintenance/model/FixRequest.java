package com.springAi.autonoumousCodeMaintenance.model;

import com.github.javaparser.quality.NotNull;
import lombok.Data;

@Data
public class FixRequest {
    private String logs;
    private String approver;

    public FixRequest(String approver, String logs) {
        this.approver = approver;
        this.logs = logs;
    }
}

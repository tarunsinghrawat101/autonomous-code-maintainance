package com.springAi.autonoumousCodeMaintenance.model;

import lombok.Data;

@Data
public class FixResponse {
    Integer prNumber;
    String prReviewer;
    Integer attemptsUsed;

    public FixResponse(Integer prNumber, String prReviewer, Integer attemptsUsed) {
        this.prNumber = prNumber;
        this.prReviewer = prReviewer;
        this.attemptsUsed = attemptsUsed;
    }
}

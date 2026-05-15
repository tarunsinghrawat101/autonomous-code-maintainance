package com.springAi.autonoumousCodeMaintenance.model;

import lombok.Data;

@Data
public class PrResponse {
    Integer PrNumber;
    String reviewer;

    public PrResponse(Integer prNumber, String reviewer) {
        PrNumber = prNumber;
        this.reviewer = reviewer;
    }
}

package com.springAi.autonoumousCodeMaintenance.model;


import lombok.Getter;

@Getter
public class RuntimeValidationResult {
    private boolean success;

    private String failureReason;

    public static RuntimeValidationResult success() {

        RuntimeValidationResult result =
                new RuntimeValidationResult();

        result.success = true;

        return result;
    }

    public static RuntimeValidationResult failure(
            String reason
    ) {

        RuntimeValidationResult result =
                new RuntimeValidationResult();

        result.success = false;

        result.failureReason = reason;

        return result;
    }

}

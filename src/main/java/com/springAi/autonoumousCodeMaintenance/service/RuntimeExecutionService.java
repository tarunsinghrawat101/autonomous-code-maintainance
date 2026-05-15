package com.springAi.autonoumousCodeMaintenance.service;

import com.springAi.autonoumousCodeMaintenance.model.AnalysisResponse;
import com.springAi.autonoumousCodeMaintenance.model.RuntimeValidationResult;

public interface RuntimeExecutionService {
    RuntimeValidationResult validate(String updatedCode, AnalysisResponse analysisResponse);
}

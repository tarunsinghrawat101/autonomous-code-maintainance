package com.springAi.autonoumousCodeMaintenance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springAi.autonoumousCodeMaintenance.model.AnalysisResponse;

public interface LogAnalysisService {
    AnalysisResponse analyze(String logs, String codeChunk, String filePath) throws JsonProcessingException;
}

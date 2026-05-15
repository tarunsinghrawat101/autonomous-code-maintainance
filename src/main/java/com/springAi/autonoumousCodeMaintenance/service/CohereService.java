package com.springAi.autonoumousCodeMaintenance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springAi.autonoumousCodeMaintenance.model.AnalysisResponse;

import java.util.List;

public interface CohereService {
    AnalysisResponse chat(String prompt) throws JsonProcessingException;
    List<Double> embed(String text);
}

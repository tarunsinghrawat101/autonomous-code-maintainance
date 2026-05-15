package com.springAi.autonoumousCodeMaintenance.seriveImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springAi.autonoumousCodeMaintenance.model.AnalysisResponse;
import com.springAi.autonoumousCodeMaintenance.service.CohereService;
import com.springAi.autonoumousCodeMaintenance.service.LogAnalysisService;
import org.springframework.stereotype.Service;

@Service
public class LogAnalysisServiceImpl implements LogAnalysisService {

    private final CohereServiceImpl cohereService;

    public LogAnalysisServiceImpl(CohereServiceImpl cohereService) {
        this.cohereService = cohereService;
    }

    @Override
    public AnalysisResponse analyze(String logs, String codeChunk, String filePath) throws JsonProcessingException {

        String prompt = """
        You are a senior backend engineer debugging production issues.
       
                 Logs:
                 %s
       
                 Relevant Code:
                 %s
       
                 File Path:
                 %s
       
                 Instructions:
                 - Identify exact file/class
                 - Explain root cause
                 - Suggest FIX (code level)
                 - Mention impact
                 - Give confidence (0-100)
       
                 Additionally:
                 - Provide MINIMAL PATCH FIXES for the code
                 - Do NOT rewrite full file
                 - Only include changed lines with line numbers
       
                 Return ONLY valid JSON. No explanation.
       
                 {
                   "filePath": "...",
                   "issue": "...",
                   "rootCause": "...",
                   "fix": "...",
                   "impact": "...",
                   "confidence": 0-100,
                   "patches": [
                     {
                       "line": 0,
                       "oldCode": "",
                       "newCode": ""
                     }
                   ]
                 }
       """.formatted(logs, codeChunk, filePath);

        return cohereService.chat(prompt);
    }
}

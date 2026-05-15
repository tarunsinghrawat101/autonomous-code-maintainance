package com.springAi.autonoumousCodeMaintenance.util;

import com.springAi.autonoumousCodeMaintenance.model.AnalysisResponse;

public class FormatPr {
    public String pRFormat(AnalysisResponse analysisResponse){
        return """
    ##  AI Generated Fix

    ### File
    %s

    ### Issue
    %s

    ### Root Cause
    %s

    ### Fix Applied
    ```java
    %s
    ```

    ### Confidence
    %d%%

    ---
    ⚠️ Please review before merging.
    """.formatted(
                analysisResponse.getFilePath(),
                analysisResponse.getIssue(),
                analysisResponse.getRootCause(),
                analysisResponse.getFix(),
                analysisResponse.getConfidence()
        );
    }
}

package com.springAi.autonoumousCodeMaintenance.config;

import com.springAi.autonoumousCodeMaintenance.service.CodeIngestionService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner {

    private final CodeIngestionService ingestionService;

    public StartupRunner(CodeIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostConstruct
    public void load() throws Exception {
        ingestionService.ingestRepo("/Users/tarunrawat/Downloads/untitled1");
    }
}
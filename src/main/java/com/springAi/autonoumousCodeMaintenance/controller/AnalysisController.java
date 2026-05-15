package com.springAi.autonoumousCodeMaintenance.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springAi.autonoumousCodeMaintenance.model.FixRequest;
import com.springAi.autonoumousCodeMaintenance.model.FixResponse;
import com.springAi.autonoumousCodeMaintenance.seriveImpl.AutoFixServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InterruptedIOException;

@RestController
@RequestMapping("/analyze")
public class AnalysisController {

    private final AutoFixServiceImpl autoFixService;

    public AnalysisController(AutoFixServiceImpl autoFixService) {
        this.autoFixService = autoFixService;
    }

    @PostMapping("/auto-fix")
    public FixResponse autoFix(@RequestBody FixRequest request) throws JsonProcessingException, InterruptedIOException, InterruptedException {
        return autoFixService.process(request.getLogs(), request.getApprover());
    }
}
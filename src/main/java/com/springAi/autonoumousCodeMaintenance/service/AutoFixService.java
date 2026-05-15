package com.springAi.autonoumousCodeMaintenance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springAi.autonoumousCodeMaintenance.model.FixResponse;

import java.io.InterruptedIOException;

public interface AutoFixService {
    FixResponse process(String Logs, String Approver) throws JsonProcessingException, InterruptedIOException, InterruptedException;
}

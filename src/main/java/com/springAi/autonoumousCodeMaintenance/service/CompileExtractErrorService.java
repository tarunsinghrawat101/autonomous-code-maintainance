package com.springAi.autonoumousCodeMaintenance.service;

import com.springAi.autonoumousCodeMaintenance.model.CompileError;

public interface CompileExtractErrorService {
    public CompileError extractError(String compilerOutput);
}

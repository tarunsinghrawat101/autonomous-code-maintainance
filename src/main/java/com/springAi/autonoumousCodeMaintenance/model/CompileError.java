package com.springAi.autonoumousCodeMaintenance.model;

import lombok.Data;

@Data
public class CompileError {
    private String filePath;
    private int line;
    private String message;

    public CompileError(String filePath, int line, String message) {
        this.filePath = filePath;
        this.line = line;
        this.message = message;
    }

    public CompileError() {

    }
}

package com.springAi.autonoumousCodeMaintenance.exception;

public class RetriesMaxedException extends RuntimeException {
    public RetriesMaxedException(String message) {
        super(message);
    }
}

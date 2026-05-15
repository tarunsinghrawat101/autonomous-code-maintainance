package com.springAi.autonoumousCodeMaintenance.exception;

public class GitHubFileNotReadableException extends RuntimeException {
    public GitHubFileNotReadableException(String message) {
        super(message);
    }
}

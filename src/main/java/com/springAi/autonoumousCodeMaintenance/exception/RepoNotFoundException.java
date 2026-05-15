package com.springAi.autonoumousCodeMaintenance.exception;

public class RepoNotFoundException extends RuntimeException{
    public RepoNotFoundException(String message) {
        super(message);
    }
}

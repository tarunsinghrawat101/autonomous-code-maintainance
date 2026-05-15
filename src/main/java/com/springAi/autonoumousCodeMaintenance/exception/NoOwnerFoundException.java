package com.springAi.autonoumousCodeMaintenance.exception;

public class NoOwnerFoundException extends RuntimeException {
    public NoOwnerFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

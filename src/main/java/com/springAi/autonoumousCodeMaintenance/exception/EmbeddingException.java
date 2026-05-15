package com.springAi.autonoumousCodeMaintenance.exception;

import lombok.Getter;

@Getter
public class EmbeddingException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public EmbeddingException(
            int statusCode,
            String responseBody
    ) {
        super("Embedding API failed with status: " + statusCode);

        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

}

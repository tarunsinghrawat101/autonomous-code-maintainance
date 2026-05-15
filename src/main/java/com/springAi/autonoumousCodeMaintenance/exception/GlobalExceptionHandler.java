package com.springAi.autonoumousCodeMaintenance.exception;

import com.springAi.autonoumousCodeMaintenance.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Repository Not Found
    @ExceptionHandler(RepoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRepoNotFound(RepoNotFoundException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    //Not able to read GitHub File
    @ExceptionHandler(GitHubFileNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleFileNotReadable(GitHubFileNotReadableException ex){
        ErrorResponse error = ErrorResponse.builder()
                .message("GitHub File Not Readable " + ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    //No input logs are given as input
    @ExceptionHandler(NoLogsException.class)
    public ResponseEntity<ErrorResponse> handleNoLogsException(NoLogsException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    //Cohere API Failed
    @ExceptionHandler(EmbeddingException.class)
    public ResponseEntity<ErrorResponse> handleEmbeddingException(EmbeddingException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .message("Cohere API Failed " + ex.getResponseBody())
                .status(ex.getStatusCode())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatusCode.valueOf(ex.getStatusCode()));
    }

    //Cohere API returned no data
    @ExceptionHandler(NoResponseException.class)
    public ResponseEntity<ErrorResponse> handleNoResponseException(Exception ex) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // No Relevant code found
    @ExceptionHandler(RelevantCodeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRelevantCodeNotFoundException(RelevantCodeNotFoundException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // AI Analysis for code fix failed
    @ExceptionHandler(AnalysisException.class)
    public ResponseEntity<ErrorResponse> handleAnalysisException(AnalysisException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage() + ex.getCause())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Creation of new branch failed
    @ExceptionHandler(CreateBranchException.class)
    public ResponseEntity<ErrorResponse> handleCreateBranchException(CreateBranchException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage() + ex.getCause())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Retries Maxed out
    @ExceptionHandler(RetriesMaxedException.class)
    public ResponseEntity<ErrorResponse> handleRetriesMaxedException(RetriesMaxedException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Error occurred during PR Creation
    @ExceptionHandler(PrCreationException.class)
    public ResponseEntity<ErrorResponse> handlePrCreationException(PrCreationException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Error occurred while fetching Repository Owner
    @ExceptionHandler(NoOwnerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoOwnerFoundException(NoOwnerFoundException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage() + ex.getCause())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Error occurred while fetching Repository Owner
    @ExceptionHandler(PrReviewerException.class)
    public ResponseEntity<ErrorResponse> handlePrReviewerException(PrReviewerException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Error Not Found in code
    @ExceptionHandler(NoCodeErrorException.class)
    public ResponseEntity<ErrorResponse> handleNoCodeErrorException(NoCodeErrorException exception){
        ErrorResponse error = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // fallback (important)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {

        ErrorResponse error = ErrorResponse.builder()
                .message("Internal Server Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

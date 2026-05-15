package com.springAi.autonoumousCodeMaintenance.service;

public interface CodeIngestionService {
    void ingestRepo(String path) throws Exception;
}

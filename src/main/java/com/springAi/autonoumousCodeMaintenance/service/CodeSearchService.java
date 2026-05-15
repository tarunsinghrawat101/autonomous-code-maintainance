package com.springAi.autonoumousCodeMaintenance.service;

import com.springAi.autonoumousCodeMaintenance.model.CodeResult;

import java.util.List;

public interface CodeSearchService {
    List<CodeResult>  searchRelevantCode(String query);
}

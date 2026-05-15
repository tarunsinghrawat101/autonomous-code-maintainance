package com.springAi.autonoumousCodeMaintenance.model;

import lombok.Data;

@Data
public class GithubFile {
    private String content;
    private String sha;

    public GithubFile(String content, String sha) {
        this.content = content;
        this.sha = sha;
    }

}

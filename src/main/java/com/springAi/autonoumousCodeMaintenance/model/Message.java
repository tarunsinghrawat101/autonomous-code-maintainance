package com.springAi.autonoumousCodeMaintenance.model;

import lombok.Data;

import java.util.List;

@Data
public class Message {
    private List<Content> content;
}

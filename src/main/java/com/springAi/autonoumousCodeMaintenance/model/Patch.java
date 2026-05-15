package com.springAi.autonoumousCodeMaintenance.model;

public record Patch(int line,
                    String oldCode,
                    String newCode) { }

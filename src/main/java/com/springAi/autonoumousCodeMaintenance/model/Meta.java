package com.springAi.autonoumousCodeMaintenance.model;

import lombok.Data;

import java.util.Map;

@Data
public class Meta {
    private Map<String, Object> api_version;
    private Map<String, Object> billed_units;
}

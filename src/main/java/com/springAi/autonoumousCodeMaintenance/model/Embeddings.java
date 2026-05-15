package com.springAi.autonoumousCodeMaintenance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Embeddings {

    @JsonProperty("float")
    private List<List<Double>> floatValues;
}

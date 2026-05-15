package com.springAi.autonoumousCodeMaintenance.model;

import lombok.Data;
import java.util.List;

@Data
public class EmbeddingResponse {
    private String id;
    private List<Input> inputs;
    private Embeddings embeddings;
    private Meta meta;
    private String response_type;
}

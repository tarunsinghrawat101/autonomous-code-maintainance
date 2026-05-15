package com.springAi.autonoumousCodeMaintenance.seriveImpl;

import com.springAi.autonoumousCodeMaintenance.exception.EmbeddingDatabaseException;
import com.springAi.autonoumousCodeMaintenance.model.CodeResult;
import com.springAi.autonoumousCodeMaintenance.service.CodeSearchService;
import com.springAi.autonoumousCodeMaintenance.service.CohereService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CodeSearchServiceImpl implements CodeSearchService {
    private final CohereServiceImpl cohereService;

    public CodeSearchServiceImpl(CohereServiceImpl cohereService, JdbcTemplate jdbcTemplate) {
        this.cohereService = cohereService;
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    public List<CodeResult> searchRelevantCode(String logs) {

        List<Double> embedding = cohereService.embed(logs);

        List<CodeResult> result;
        try {
            result = jdbcTemplate.query(
                    """
                            SELECT content, file_path
                            FROM code_embeddings
                            ORDER BY embedding <-> ?::vector
                            LIMIT 1
                            """,
                    (rs, rowNum) -> new CodeResult(
                            rs.getString("content"),
                            rs.getString("file_path")
                    ),
                    toVector(embedding)
            );
        } catch (DataAccessException ex) {
            throw new EmbeddingDatabaseException("Database error while searching embeddings", ex.getCause());
        }
        return result;
    }

    private String toVector(List<Double> emb) {
        return "[" + emb.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + "]";
    }
}

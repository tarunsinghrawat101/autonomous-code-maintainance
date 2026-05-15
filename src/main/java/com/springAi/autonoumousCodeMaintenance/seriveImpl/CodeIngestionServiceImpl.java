package com.springAi.autonoumousCodeMaintenance.seriveImpl;

import com.springAi.autonoumousCodeMaintenance.service.CodeIngestionService;
import com.springAi.autonoumousCodeMaintenance.service.CohereService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class CodeIngestionServiceImpl implements CodeIngestionService {

    private final CohereService cohereService;
    private final JdbcTemplate jdbcTemplate;

    public CodeIngestionServiceImpl(CohereService cohereService, JdbcTemplate jdbcTemplate) {
        this.cohereService = cohereService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void ingestRepo(String rootPath) throws IOException {

        Files.walk(Path.of(rootPath))
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(this::processFile);
    }

    private void processFile(Path path) {
        try {
            String content = Files.readString(path);

            // Split into chunks
            List<String> chunks = chunkText(content, 1000);

            for (String chunk : chunks) {
                try {
                    List<Double> embedding = cohereService.embed(chunk);

                    jdbcTemplate.update(
                            "INSERT INTO code_embeddings (file_name, file_path, content, embedding) VALUES (?, ?, ?, ?::vector)",
                            path.getFileName().toString(),
                            path.toString(),   // FULL PATH STORED
                            chunk,
                            toVector(embedding)
                    );

                } catch (Exception e) {
                    System.out.println(" Skipped chunk in file: " + path);
                }
            }

            System.out.println(" Processed file: " + path.getFileName());

        } catch (Exception e) {
            System.out.println(" Failed file: " + path);
            e.printStackTrace();
        }
    }

    /**
     * Split large file into smaller chunks to avoid API limits
     */
    private List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }

        return chunks;
    }

    /**
     * Convert embedding list → pgvector format
     */
    private String toVector(List<Double> emb) {
        return "[" + emb.stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse("") + "]";
    }
}
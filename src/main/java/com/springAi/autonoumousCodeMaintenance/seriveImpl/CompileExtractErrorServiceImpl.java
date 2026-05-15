package com.springAi.autonoumousCodeMaintenance.seriveImpl;

import com.springAi.autonoumousCodeMaintenance.exception.CompileParsingException;
import com.springAi.autonoumousCodeMaintenance.model.CompileError;
import com.springAi.autonoumousCodeMaintenance.service.CompileExtractErrorService;
import org.springframework.stereotype.Service;

@Service
public class CompileExtractErrorServiceImpl implements CompileExtractErrorService {
    @Override
    public CompileError extractError(String compilerOutput) {
        if (compilerOutput == null || compilerOutput.isEmpty()) {
            return null;
        }

        CompileError error = new CompileError();

        String[] lines = compilerOutput.split("\n");

        for (String line : lines) {

            // Pattern 1: javac style
            // Example: SecretMessageDecoder.java:42: error: cannot find symbol
            if (line.contains(".java:") && line.contains("error")) {
                try {
                    String[] parts = line.split(":");

                    error.setFilePath(parts[0].trim());
                    error.setLine(Integer.parseInt(parts[1].trim()));
                    error.setMessage(line.substring(line.indexOf("error:") + 6).trim());

                    return error;

                } catch (Exception e) {
                    throw new CompileParsingException(
                            "Failed to parse javac error line: " + line,
                            e);
                }
            }

            // Pattern 2: Gradle style
            // Example: > Task :compileJava FAILED
            if (line.contains("FAILED") || line.contains("Exception")) {
                error.setMessage(line.trim());
            }

            // Pattern 3: stack trace reference
            // Example: at com.example.SecretMessageDecoder.method(SecretMessageDecoder.java:42)
            if (line.contains(".java:") && line.contains("(")) {
                try {
                    int start = line.indexOf('(');
                    int end = line.indexOf(')');

                    String content = line.substring(start + 1, end);
                    String[] parts = content.split(":");

                    error.setFilePath(parts[0]);
                    error.setLine(Integer.parseInt(parts[1]));

                    return error;

                } catch (Exception e) {
                    throw new CompileParsingException(
                            "Failed to parse stack trace line: " + line,
                            e);
                }
            }
        }

        // fallback
        error.setMessage(compilerOutput.substring(0, Math.min(200, compilerOutput.length())));
        return error;
    }
}

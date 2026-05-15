package com.springAi.autonoumousCodeMaintenance.util;

import com.springAi.autonoumousCodeMaintenance.model.ExecutionResult;
import com.springAi.autonoumousCodeMaintenance.model.MethodInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.UUID;

public class DockerSandboxExecutor {
    public ExecutionResult execute(String updatedCode, MethodInfo methodInfo, Object[] args) {
        ExecutionResult result = new ExecutionResult();
        File tempDir = null;
        try {
            // 1. Create Temporary Directory
            tempDir = Files.createTempDirectory("ai-runtime-").toFile();

           // 2. Write Java File
            File javaFile = new File(tempDir, "RuntimeTest.java");

            String javaCode = buildJavaExecutionCode(updatedCode);

            Files.writeString(javaFile.toPath(), javaCode);

            // 3. Run inside Docker
            String containerName = "runtime-" + UUID.randomUUID();
            Process process = getProcess(containerName, tempDir);

            BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream()));
            StringBuilder output = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                result.setSuccess(false);
                result.setMessage(output.toString());
            } else {
                result.setSuccess(true);
                result.setMessage(output.toString());
            }

        } catch (Exception ex) {
            result.setSuccess(false);
            result.setMessage(ex.getMessage());
        } finally {
            if (tempDir != null) {
                deleteDirectory(tempDir);
            }
        }
        return result;
    }

    private static Process getProcess(String containerName, File tempDir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                        "docker",
                        "run",
                        "--rm",
                        "--name",
                containerName,
                        "-m",
                        "256m",
                        "--cpus",
                        "1",
                        "--network",
                        "none",
                        "-v",
                        tempDir.getAbsolutePath()
                                + ":/app",
                        "openjdk:17",
                        "sh",
                        "-c",
                        "cd /app && javac RuntimeTest.java && java RuntimeTest"
                );

        pb.redirectErrorStream(true);

        return pb.start();
    }

    private String buildJavaExecutionCode(String updatedCode) {
        return """
                public class RuntimeTest {
                
                    %s
                
                    public static void main(String[] args)
                            throws Exception {
                
                        System.out.println(
                                "Runtime validation successful"
                        );
                    }
                }
                """.formatted(updatedCode);
    }

    private void deleteDirectory(File file) {

        File[] files = file.listFiles();

        if (files != null) {
            for (File f : files) {
                deleteDirectory(f);
            }
        }
        file.delete();
    }
}

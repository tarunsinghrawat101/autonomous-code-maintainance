package com.springAi.autonoumousCodeMaintenance.seriveImpl;

import com.springAi.autonoumousCodeMaintenance.model.AnalysisResponse;
import com.springAi.autonoumousCodeMaintenance.model.ExecutionResult;
import com.springAi.autonoumousCodeMaintenance.model.MethodInfo;
import com.springAi.autonoumousCodeMaintenance.model.RuntimeValidationResult;
import com.springAi.autonoumousCodeMaintenance.service.RuntimeExecutionService;
import com.springAi.autonoumousCodeMaintenance.util.ModifiedMethodDetector;
import com.springAi.autonoumousCodeMaintenance.util.RuntimeInputGenerator;
import com.springAi.autonoumousCodeMaintenance.util.DockerSandboxExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuntimeExecutionServiceImpl implements RuntimeExecutionService {
    @Override
    public RuntimeValidationResult validate(String updatedCode, AnalysisResponse analysisResponse) {
        try {

            // 1. Detect methods
            ModifiedMethodDetector detector = new ModifiedMethodDetector();

            List<MethodInfo> methods = detector.detect(updatedCode);

            // 2. Generate runtime inputs
            RuntimeInputGenerator inputGenerator = new RuntimeInputGenerator();

            // 3. Execute methods
            DockerSandboxExecutor executor = new DockerSandboxExecutor();

            for (MethodInfo method : methods) {

                List<Object[]> inputs = inputGenerator.generate(method);

                for (Object[] args : inputs) {
                    ExecutionResult result = executor.execute(updatedCode, method, args);

                    if (!result.isSuccess()) {
                        return RuntimeValidationResult.failure(result.getMessage());
                    }
                }
            }

            return RuntimeValidationResult.success();
        } catch (Exception ex) {
            return RuntimeValidationResult.failure(ex.getMessage()
            );
        }
    }
}

package com.springAi.autonoumousCodeMaintenance.model;

import com.github.javaparser.ast.body.Parameter;

import java.util.List;

public class MethodInfo {
    private String methodName;

    private List<Parameter> parameters;

    public String getMethodName() {

        return methodName;
    }

    public void setMethodName(
            String methodName
    ) {

        this.methodName = methodName;
    }

    public List<Parameter> getParameters() {

        return parameters;
    }

    public void setParameters(
            List<Parameter> parameters
    ) {

        this.parameters = parameters;
    }
}

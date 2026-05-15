package com.springAi.autonoumousCodeMaintenance.util;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.springAi.autonoumousCodeMaintenance.model.MethodInfo;

import java.util.ArrayList;
import java.util.List;

public class ModifiedMethodDetector {
    public List<MethodInfo> detect(String sourceCode) {

        CompilationUnit cu = StaticJavaParser.parse(sourceCode);

        List<MethodInfo> methods = new ArrayList<>();

        cu.findAll(MethodDeclaration.class)
                .forEach(method -> {
                    MethodInfo info = new MethodInfo();
                    info.setMethodName(method.getNameAsString());
                    info.setParameters(method.getParameters());

                    methods.add(info);
                });

        return methods;
    }
}

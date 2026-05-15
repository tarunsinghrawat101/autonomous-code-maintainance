package com.springAi.autonoumousCodeMaintenance.util;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.springAi.autonoumousCodeMaintenance.model.AnalysisResponse;

public class FixIssue {
    public String applyASTFix(String originalCode, AnalysisResponse analysisResponse) {
        /* TODO: Replace code with Rule-based, extensible fix engine */

        try {
            CompilationUnit cu = StaticJavaParser.parse(originalCode);

            cu.findAll(MethodDeclaration.class).forEach(method -> {

                // Example: fix NumberFormatException
                if (analysisResponse.getIssue().contains("NumberFormatException")) {

                    method.findAll(MethodCallExpr.class).forEach(call -> {
                        if (call.getNameAsString().equals("parseInt")) {

                            Expression arg = call.getArgument(0);

                            String replacement = """
                        (%s.matches("\\\\d+") ? Integer.parseInt(%s) : 0)
                        """.formatted(arg, arg);

                            Expression newExpr = StaticJavaParser.parseExpression(replacement);

                            call.replace(newExpr);
                        }
                    });
                }
            });

            return cu.toString();

        } catch (Exception e) {
            throw new RuntimeException("AST patch failed", e);
        }
    }
}

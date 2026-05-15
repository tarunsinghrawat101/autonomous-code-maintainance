package com.springAi.autonoumousCodeMaintenance.util;

import com.springAi.autonoumousCodeMaintenance.model.MethodInfo;

import java.util.ArrayList;
import java.util.List;

public class RuntimeInputGenerator {
    public List<Object[]> generate(MethodInfo method) {

        List<Object[]> testCases = new ArrayList<>();

        int paramSize = method.getParameters().size();

        Object[] normal = new Object[paramSize];

        Object[] edge = new Object[paramSize];

        Object[] invalid = new Object[paramSize];

        for (int i = 0; i < paramSize; i++) {

            String type = method.getParameters()
                            .get(i)
                            .getType()
                            .asString();

            normal[i] = generateNormal(type);
            edge[i] = generateEdge(type);
            invalid[i] = generateInvalid(type);
        }

        testCases.add(normal);
        testCases.add(edge);
        testCases.add(invalid);

        return testCases;
    }

    private Object generateNormal(String type) {
        return switch (type) {

            case "String" -> "hello";

            case "Integer", "int" -> 1;

            case "Long", "long" -> 1L;

            case "Boolean", "boolean" -> true;

            case "Double", "double" -> 10.5;

            default -> null;
        };
    }

    private Object generateEdge(String type) {
        return switch (type) {

            case "String" -> "";

            case "Integer", "int" -> Integer.MAX_VALUE;

            case "Long", "long" -> Long.MAX_VALUE;

            case "Boolean", "boolean" -> false;

            case "Double", "double" -> Double.MAX_VALUE;

            default -> null;
        };
    }

    private Object generateInvalid(String type) {
        return switch (type) {

            case "String" -> "' OR 1=1 --";

            case "Integer", "int" -> -1;

            case "Long", "long" -> -1L;

            case "Boolean", "boolean" -> null;

            case "Double", "double" -> -99999.99;

            default -> null;
        };
    }
}

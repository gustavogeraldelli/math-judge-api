package dev.gustavo.math.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class ProblemVariables {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };
    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");

    private ProblemVariables() {
    }

    public static List<String> fromJson(String variables) {
        if (variables == null || variables.isBlank())
            return List.of();

        try {
            return OBJECT_MAPPER.readValue(variables, STRING_LIST_TYPE);
        }
        catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid problem variables JSON", e);
        }
    }

    public static String toJson(List<String> variables) {
        if (variables == null)
            return null;

        try {
            return OBJECT_MAPPER.writeValueAsString(variables);
        }
        catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid problem variables", e);
        }
    }

    public static boolean isValidName(String variable) {
        return variable != null && VARIABLE_NAME_PATTERN.matcher(variable).matches();
    }

    public static boolean hasDuplicates(List<String> variables) {
        Set<String> uniqueVariables = new LinkedHashSet<>(variables);
        return uniqueVariables.size() != variables.size();
    }
}

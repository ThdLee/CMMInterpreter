package com.interpreter.intermediatecode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class VariableRecorder {
    private final Map<String, Integer> variableMap = new LinkedHashMap<>();
    private final Map<String, PrimaryType> typeMap = new HashMap<>();

    private final VariableRecorder parent;

    VariableRecorder() {
        this(null);
    }

    private VariableRecorder(VariableRecorder parent) {
        this.parent = parent;
    }

    VariableRecorder link() {
        return new VariableRecorder(this);
    }

    boolean contains(String variableName) {
        return getVarIndex(variableName) != null;
    }

    int getSize() {
        return variableMap.size();
    }

    boolean localContains(String variableName) {
        return variableMap.containsKey(variableName);
    }

    void define(String variableName, int varIndex, PrimaryType type) {
        variableMap.put(variableName, varIndex);
        typeMap.put(variableName, type);
    }

    Integer getVarIndex(String variableName) {
        Integer varIndex = variableMap.get(variableName);
        if(varIndex == null && parent != null) {
            varIndex = parent.getVarIndex(variableName);
        }
        return varIndex;
    }
}

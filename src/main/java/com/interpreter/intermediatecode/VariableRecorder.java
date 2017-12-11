package com.interpreter.intermediatecode;

import com.interpreter.virtualmachine.Runtime;
import com.interpreter.virtualmachine.Value;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class VariableRecorder {


    private final Map<String, Integer> variableMap = new LinkedHashMap<>();
    private static final Map<Integer, PrimaryType> typeMap = new HashMap<>();
    private static final Map<Integer, PrimaryType> arrayTypeMap = new HashMap<>();

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
        define(variableName, varIndex, type, null);
    }

    void define(String variableName, int varIndex, PrimaryType type, PrimaryType elemType) {
        if (variableMap.containsKey(variableName)) {
            throw new IntermediateException("'" + variableName + "' has defined");
        }
        if (type == PrimaryType.Array && elemType != null) {
            arrayTypeMap.put(varIndex, elemType);
        }
        typeMap.put(varIndex, type);
        variableMap.put(variableName, varIndex);
    }

    public Integer getVarIndex(String variableName) {
        Integer varIndex = variableMap.get(variableName);
        if(varIndex == null && parent != null) {
            varIndex = parent.getVarIndex(variableName);
        }
        return varIndex;
    }

    public static PrimaryType getType(int varIndex) {
        return typeMap.get(varIndex);
    }
    public static PrimaryType getElementType(int varIndex) {
        return arrayTypeMap.get(varIndex);
    }
}

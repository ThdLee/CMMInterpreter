package com.interpreter.intermediatecode;

import com.interpreter.virtualmachine.Runtime;
import com.interpreter.virtualmachine.Value;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class VariableRecorder {
    static class VariableInfo {
        private Integer register;
        private PrimaryType type;

        public VariableInfo(int register, PrimaryType type) {
            this.register = register;
            this.type = type;
        }

        public Integer getRegister() {
            return register;
        }

        public PrimaryType getType() {
            return type;
        }

    }

    private final Map<String, VariableInfo> variableMap = new LinkedHashMap<>();
    private final Map<Integer, PrimaryType> typeMap = new HashMap<>();

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
        VariableInfo newInfo;
        newInfo = new VariableInfo(varIndex, type);
        if (type == PrimaryType.Array) {
            typeMap.put(varIndex, elemType);
        }
        variableMap.put(variableName, newInfo);
    }

    Integer getVarIndex(String variableName) {
        VariableInfo info = variableMap.get(variableName);
        Integer varIndex = info == null ? null : info.getRegister();
        if(varIndex == null && parent != null) {
            varIndex = parent.getVarIndex(variableName);
        }
        return varIndex;
    }

    public PrimaryType getArrayType(int varIndex) {
        return typeMap.get(varIndex);
    }
}

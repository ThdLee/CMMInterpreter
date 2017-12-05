package com.interpreter.intermediatecode;


import java.util.HashMap;
import java.util.Map;

public class TypeLazyBinding {

    private static TypeLazyBinding instance = new TypeLazyBinding();

    public static TypeLazyBinding getInstance() {
        return instance;
    }

    private TypeLazyBinding() {}

    private final Map<Integer, PrimaryType> typeMap = new HashMap<>();
    private final Map<Integer, Integer> bindingMap = new HashMap<>();

    void put(int register, PrimaryType type, int line) {
        typeMap.put(register, type);
        bindingMap.put(register, line);
    }

    public PrimaryType get(int register) {
        return typeMap.get(register);
    }

    public boolean isBinding(int register, int curLine) {
        if (!bindingMap.containsKey(register)) {
            return false;
        }
        int line = bindingMap.get(register);
        return curLine >= line;
    }
}

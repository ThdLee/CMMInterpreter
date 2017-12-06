package com.interpreter.debug;

import com.interpreter.intermediatecode.VariableRecorder;

import java.util.HashMap;


public class Debug {

    public static final Debug instance = new Debug();

    private Debug() {}

    private final HashMap<Integer, VariableRecorder> recorderMap = new HashMap<>();
    private final HashMap<Integer, Integer> codeMap = new HashMap<>();

    private boolean activate = false;
    private int maxLine = 0;

    public void activation() {
        activate = true;
    }

    public void mapRecorder(int line, VariableRecorder recorder) {
        if (!activate) return;
        recorderMap.put(line, recorder);
        maxLine = line > maxLine ? line : maxLine;
    }

    public void mapCode(int line) {
        if (!activate) return;
        if (recorderMap.isEmpty()) {
            throw new RuntimeException("");
        }
        codeMap.putIfAbsent(maxLine, line);
    }


}

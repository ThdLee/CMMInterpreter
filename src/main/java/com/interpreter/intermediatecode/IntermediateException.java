package com.interpreter.intermediatecode;

public class IntermediateException extends RuntimeException {
    public IntermediateException(char c) {
        super("unexpected '" + c + "'");
    }

    public IntermediateException(String msg) {
        super("unexpected \"" + msg + "\"");
    }
}

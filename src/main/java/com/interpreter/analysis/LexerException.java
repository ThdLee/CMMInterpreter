package com.interpreter.analysis;

public class LexerException extends Exception {
    public LexerException(char c) {
        super("unexpected '" + c + "'");
    }

    public LexerException(String msg) {
        super("unexpected \"" + msg + "\"");
    }
}

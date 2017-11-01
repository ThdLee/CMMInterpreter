package com.interpreter.analysis;

public class ParserException extends RuntimeException {
    public ParserException(char c) {
        super("unexpected '" + c + "'");
    }

    public ParserException(String msg) {
        super("unexpected \"" + msg + "\"");
    }

    public ParserException(Token token) {
        super("<line:" + token.line + " pos:" + token.pos + "> unexpected \"" + token.value + "\"");
    }
}

package com.interpreter.analysis;

public class ParserException extends RuntimeException {
    public ParserException(char c) {
        super("unexpected '" + c + "'");
    }

    public ParserException(Token token, String msg) {
        super("<line:" + token.line + " pos:" + token.pos + "> " + msg);
    }

    public ParserException(Token token) {
        super("<line:" + token.line + " pos:" + token.pos + "> unexpected \"" + token.value + "\"");
    }
}

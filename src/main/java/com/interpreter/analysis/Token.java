package com.interpreter.analysis;

import java.util.HashSet;

public class Token {
    public enum Type {
        Keyword, Decimal, Integer, Identifier, Sign, Annotation, String, Space, NewStatement, EndSymbol;
    }

    private static final HashSet<String> KeywordsSet = new HashSet<String>() {{
        add("true");
        add("false");
        add("if");
        add("else");
        add("while");
        add("int");
        add("string");
        add("bool");
        add("double");
        add("break");
        add("continue");
        add("read");
        add("write");
    }};
    
    final Type type;
    final String value;

    final int line;
    final int pos;

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    Token(Type type, String value, int pos, int line) {
        this.value = value;
        if (KeywordsSet.contains(value)) {
            type = Type.Keyword;
        }
        this.type = type;
        this.pos = pos;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return type + " " + value;
    }
}

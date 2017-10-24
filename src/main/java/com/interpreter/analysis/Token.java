package com.interpreter.analysis;

import java.util.HashSet;

public class Token {
    public static enum Type {
        Keyword, Decimal, Integer, Identifier, Sign, Annotation, String, Space, NewStatement, EndSymbol;
    }

    private static final HashSet<String> KeywordsSet = new HashSet<String>() {{
        add("true");
        add("false");
        add("if");
        add("when");
        add("else");
        add("while");
        add("for");
        add("do");
        add("void");
        add("int");
        add("float");
        add("double");
        add("char");
        add("long");
        add("break");
        add("continue");
        add("return");
    }};
    
    final Type type;
    final String value;

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    Token(Type type, String value) {

        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return type + " " + value;
    }
}

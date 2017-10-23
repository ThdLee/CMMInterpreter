package com.interpreter.analysis;

import java.util.HashSet;

class Token {
    public static enum Type {
        Keyword, Number, Identifier, Sign, Annotation, String, Space, NewStatement, EndSymbol;
    }

    private static final HashSet<String> KeywordsSet = new HashSet<String>() {{
        add("true");
        add("false");
        add("if");
        add("when");
        add("elsif");
        add("else");
        add("while");
        add("begin");
        add("until");
        add("for");
        add("do");
        add("try");
        add("catch");
        add("finally");
        add("end");
        add("def");
        add("var");
        add("this");
        add("null");
        add("throw");
        add("break");
        add("continue");
        add("return");
        add("operator");
        add("instanceof");
        add("is");
    }};
    
    final Type type;
    final String value;

    Token(Type type, String value) {

        this.type = type;
        this.value = value;
    }
}

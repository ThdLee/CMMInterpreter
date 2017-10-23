package com.interpreter.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.interpreter.analysis.Token.Type;

public class Lexer {

    private static enum State {
        Normal, Identifier, Sign, Annotation, String, Space, Number;
    }

    private static final char[] FilterChar = new char[] {
            '\b', '\f', '\r'
    };

    private static final char[] Space = new char[] {' ', '\t'};

    private static final HashMap<Character, Character> EscapeMap = new HashMap<Character, Character>() {{
        put('\"', '\"');
        put('\'', '\'');
        put('\\', '\\');
        put('b', '\b');
        put('f', '\f');
        put('t', '\t');
        put('r', '\r');
        put('n', '\n');
    }};

    private Reader reader;

    private State state;
    private Token endToken = null;
    private final LinkedList<Token> tokenBuffer;

    private int pos;
    private int lines;

    private StringBuilder buf;

    public Lexer(Reader reader) {
        this.reader = reader;
        this.state = State.Normal;
        tokenBuffer = new LinkedList<>();
    }

    Token read() throws IOException, LexerException {

        if(endToken != null) {
            return endToken;
        }
        while(tokenBuffer.isEmpty()) {
            pos++;
            int read = reader.read();
            char c = (read == -1 ? '\0' : (char) read);
            while(!readChar(c)) {}
        }
        Token token = tokenBuffer.removeLast();
        if(token.type == Token.Type.EndSymbol) {
            endToken = token;
        }
        return token;
    }

    private boolean readChar(char c) throws LexerException {
        boolean moveCursor = true;
        Type type = null;

        boolean isEscape = false;

        if(!include(FilterChar, c)) {

            if(state == State.Normal) {

                if(inIdentifierSetFirst(c)) {
                    state = State.Identifier;
                    buf = new StringBuilder().append(c);
                } else if(SignParser.inCharSet(c)) {
                    state = State.Sign;
                } else if(c == '#') {
                    state = State.Annotation;
                    buf = new StringBuilder().append(c);
                } else if(c == '\"' | c == '\'') {
                    state = State.String;
                    buf = new StringBuilder().append(c);
                    isEscape = false;
                } else if(include(Space, c)) {
                    state = State.Space;
                } else if (c == ';') {
                    type = Type.NewStatement;
                } else if(c == '\n') {
                    lines++;
                    pos = 0;
                } else if(c == '\0') {
                    type = Type.EndSymbol;
                } else {
                    throw new LexerException(c);
                }
                buf = new StringBuilder().append(c);

            } else if(state == State.Identifier) {

                if(inIdentifierSet(c)) {
                    buf.append(c);
                } else {
                    type = Type.Identifier;
                    state = State.Normal;
                    moveCursor = false;
                }
            } else if(state == State.Sign) {

                if(SignParser.inCharSet(c)) {
                    buf.append(c);
                } else {
                    List<String> list = SignParser.parse(buf.toString());
                    for(String signStr:list) {
                        createToken(Type.Sign, signStr);
                    }
                    type = null;
                    state = State.Normal;
                    moveCursor = false;
                }
            } else if(state == State.Annotation) {

                if(c != '\n' & c != '\0') {
                    buf.append(c);
                } else {
                    type = Type.Annotation;
                    state = State.Normal;
                    moveCursor = false;
                }
            } else if(state == State.String) {

                if(c == '\n') {
                    throw new LexerException(c);
                } else if(c == '\0') {
                    throw new LexerException(c);
                } else if(isEscape) {

                    Character tms = EscapeMap.get(c);
                    if(tms == null) {
                        throw new LexerException(c);
                    }
                    buf.append(tms);
                    isEscape = false;
                } else if(c == '\\') {
                    isEscape = true;
                } else {
                    buf.append(c);
                    char firstChar = buf.charAt(0);
                    if(firstChar == c) {
                        type = Type.String;
                        state = State.Normal;
                    }
                }
            }else if(state == State.Space) {

                if(include(Space, c)) {
                    buf.append(c);
                } else {
                    type = Type.Space;
                    state = State.Normal;
                    moveCursor = false;
                }
            }
        }
        if(type != null) {
            createToken(type);
        }
        return moveCursor;
    }

    private void createToken(Type type) {
        Token token = new Token(type, buf != null ? buf.toString() : null);
        tokenBuffer.addFirst(token);
        buf = null;
    }

    private void createToken(Type type, String value) {
        Token token = new Token(type, value);
        tokenBuffer.addFirst(token);
        buf = null;
    }

    private boolean inIdentifierSet(char c) {
        return (c >= 'a' && c <= 'z' ) || (c >='A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
    }

    private boolean inIdentifierSetFirst(char c) {
        return (c >= 'a' && c <= 'z' ) || (c >='A' && c <= 'Z') || c == '_';
    }

    private boolean inNumberSet(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean include(char[] range, char c) {
        boolean include = false;
        for (char ch : range) {
            if (ch == c) {
                include = true;
                break;
            }
        }
        return include;
    }
}

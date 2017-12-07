package com.interpreter.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.interpreter.analysis.Token.Type;
import com.interpreter.debug.Debug;

public class Lexer {

    private static enum State {
        Normal, Identifier, Sign, Annotation, String, Number, AnnOrSign, EndAnn, Space;
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
    private final ArrayList<String> codeArray;

    private int pos;
    private int lines = 1;

    private StringBuilder buf;
    private StringBuilder code;

    public Lexer(Reader reader) {
        this.reader = reader;
        this.state = State.Normal;
        tokenBuffer = new LinkedList<>();
        codeArray = new ArrayList<>();
        code = new StringBuilder();
        Debug.instance.setOriginCodes(codeArray);
    }

    Token read() throws IOException {

        if(endToken != null) {
            return endToken;
        }
        while(tokenBuffer.isEmpty()) {
            pos++;
            int read = reader.read();
            char c = (read == -1 || read == 0 ? '\0' : (char) read);
            if (c == '\n') {
                lines++;
                pos = 0;
                codeArray.add(code.toString());
                code = new StringBuilder();
            } else {
                code.append(c);
            }
            while(!readChar(c)) {}
        }
        Token token = tokenBuffer.removeLast();
        if(token.type == Token.Type.EndSymbol) {
            endToken = token;
        }
        return token;
    }

    String[] getCodes() {
        String[] codes = new String[codeArray.size()];
        for (int i = 0; i < codeArray.size(); i++) {
            codes[i] = codeArray.get(i);
        }
        return codes;

    }

    private boolean isEscape = false;
    private boolean hasDot = false;
    private boolean annotationOneLine = false;

    private int signOriginPos;

    private boolean readChar(char c) throws LexerException {
        boolean moveCursor = true;
        Type type = null;

        if(!include(FilterChar, c)) {

            if(state == State.Normal) {

                if(inIdentifierSetFirst(c)) {
                    state = State.Identifier;
                } else if(c == '/') {
                    signOriginPos = pos;
                    state = State.AnnOrSign;
                } else if(SignParser.inCharSet(c)) {
                    signOriginPos = pos;
                    state = State.Sign;
                } else if(c == '\"' | c == '\'') {
                    state = State.String;
                    isEscape = false;
                } else if(include(Space, c)) {
                    state = State.Space;
                } else if (inNumberSet(c)) {
                    state = State.Number;
                } else if (c == ';') {
                    type = Type.NewStatement;
                } else if(c == '\n') {

                } else if(c == '\0') {
                    type = Type.EndSymbol;
                } else {
                    throw new LexerException(lines, pos, c);
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
            } else if (state == State.Number) {

                if (c == '.') {
                    if (hasDot) throw new LexerException(lines, pos, c);
                    hasDot = true;
                    buf.append(c);
                } else if (inNumberSet(c)) {
                    buf.append(c);
                } else {
                    type = hasDot ? Type.Decimal : Type.Integer;
                    hasDot = false;
                    state = State.Normal;
                    moveCursor = false;
                }

            } else if (state == State.AnnOrSign) {

                if (c == '*' || c == '/') {
                    annotationOneLine = c != '*';
                    buf.append(c);
                    state = State.Annotation;
                } else if (SignParser.inCharSet(c)) {
                    buf.append(c);
                    state = State.Sign;
                } else {
                    createToken(Type.Sign, "/");
                    type = null;
                    state = State.Normal;
                    moveCursor = false;
                }
            } else if (state == State.Sign) {

                if(SignParser.inCharSet(c)) {
                    buf.append(c);
                } else {
                    List<String> list = SignParser.parse(buf.toString());
                    for(String signStr:list) {
                        createTokenForSign(Type.Sign, signStr, signOriginPos);
                        signOriginPos += signStr.length();
                    }
                    type = null;
                    state = State.Normal;
                    moveCursor = false;
                }
            } else if(state == State.Annotation) {

                if (annotationOneLine) {
                    if(c != '\n' & c != '\0') {
                        buf.append(c);
                    } else {
                        type = Type.Annotation;
                        state = State.Normal;
                        annotationOneLine = false;
                        moveCursor = false;
                    }
                } else {
                    if (c == '*') state = State.EndAnn;
                    buf.append(c);
                }
            } else if (state == State.EndAnn) {

                if (c == '/') {
                    buf.append(c);
                    type = Type.Annotation;
                    state = State.Normal;
                    moveCursor = true;
                } else {
                    state = State.Annotation;
                }
            } else if(state == State.String) {

                if(c == '\n') {
                    throw new LexerException(lines, pos, c);
                } else if(c == '\0') {
                    throw new LexerException(lines, pos, c);
                } else if(isEscape) {

                    Character tms = EscapeMap.get(c);
                    if(tms == null) {
                        throw new LexerException(lines, pos, c);
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
            } else if(state == State.Space) {

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
        Token token = new Token(type, buf.toString(), pos, lines);
        tokenBuffer.addFirst(token);
        buf = null;
    }

    private void createToken(Type type, String value) {
        Token token = new Token(type, value, pos, lines);
        tokenBuffer.addFirst(token);
        buf = null;
    }

    private void createTokenForSign(Type type, String value, int pos) {
        Token token = new Token(type, value, pos, lines);
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

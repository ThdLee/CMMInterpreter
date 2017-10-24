package com.interpreter;

import com.interpreter.analysis.Lexer;
import com.interpreter.analysis.LexerException;
import com.interpreter.analysis.Token;

import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException, LexerException {
        File file = new File("/Users/thdlee/Downloads/TestCases/test4_算术运算.cmm");
        Reader reader = new FileReader(file);
        Lexer lexer = new Lexer(reader);
        for (Token token = lexer.read(); token.getType() != Token.Type.EndSymbol; token = lexer.read()) {
            System.out.println(token);
        }
        reader.close();
    }
}

package com.interpreter;

import com.interpreter.analysis.*;
import com.interpreter.intermediatecode.CodeChunk;
import com.interpreter.intermediatecode.IntermediateCodeCreator;

import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException, LexerException {
        File file = new File("/Users/thdlee/Downloads/TestCases/test4_算术运算.cmm");
        Reader reader = new FileReader(file);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        AST ast = parser.prog();
        IntermediateCodeCreator codeCreator = new IntermediateCodeCreator();
        CodeChunk codeChunk = codeCreator.create(ast);
        System.out.println(codeChunk);
        reader.close();
    }
}

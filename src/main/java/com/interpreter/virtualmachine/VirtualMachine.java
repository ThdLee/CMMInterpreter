package com.interpreter.virtualmachine;

import com.interpreter.analysis.Lexer;
import com.interpreter.analysis.Parser;
import com.interpreter.intermediatecode.CodeChunk;
import com.interpreter.intermediatecode.IntermediateCodeCreator;

import java.io.IOException;
import java.io.Reader;

public class VirtualMachine {

    private static VirtualMachine instance = new VirtualMachine();

    public static VirtualMachine getInstance() {
        return instance;
    }

    private VirtualMachine() {}

    public void run(Reader reader) throws IOException {
        Parser parser = new Parser(new Lexer(reader));
        IntermediateCodeCreator codeCreator = new IntermediateCodeCreator();

        CodeChunk codeChunk = codeCreator.create(parser.prog());

        Runtime runtime = new Runtime(System.in, System.out);
        runtime.run(codeChunk);

    }
}
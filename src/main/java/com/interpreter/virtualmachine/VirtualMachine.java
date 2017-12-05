package com.interpreter.virtualmachine;

import com.interpreter.analysis.Lexer;
import com.interpreter.analysis.Parser;
import com.interpreter.intermediatecode.CodeChunk;
import com.interpreter.intermediatecode.Context;
import com.interpreter.intermediatecode.IntermediateCodeCreator;
import com.interpreter.intermediatecode.VariableRecorder;

import java.io.IOException;
import java.io.Reader;

public class VirtualMachine {

    private static VirtualMachine instance = new VirtualMachine();

    public static VirtualMachine getInstance() {
        return instance;
    }

    private VirtualMachine() {}

    public void run(Reader reader) throws IOException {
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        IntermediateCodeCreator codeCreator = new IntermediateCodeCreator();
        Context context = codeCreator.create(parser.prog());
        CodeChunk codeChunk = context.getChunk();
        Runtime runtime = new Runtime(System.in, System.out, context);
        try {
            runtime.run(codeChunk);
        } catch (Exception e) {
            System.out.println(runtime.code);
            throw e;
        }

    }
}
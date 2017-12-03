package com.interpreter.virtualmachine;

import com.interpreter.intermediatecode.CodeChunk;

import java.io.InputStream;
import java.io.PrintStream;

public class Runtime {
    private CodeChunk codeChunk;
    private DataChunk dataChunk;

    final PrintStream out;
    final InputStream in;

    private final Interpreter interpreter;
    public Runtime(InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;
        this.interpreter = new Interpreter(this);
    }

    public void run(CodeChunk runCodeChunk) {
        this.codeChunk = runCodeChunk;
        this.dataChunk = new DataChunk();
        int nextRunLine = 0;

        while (nextRunLine < runCodeChunk.getSize()) {
            CodeChunk.Code code = runCodeChunk.getCodeByLine(nextRunLine);
            nextRunLine = interpreter.run(nextRunLine, code, dataChunk);
        }
    }
}

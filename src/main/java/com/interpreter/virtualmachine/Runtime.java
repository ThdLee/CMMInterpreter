package com.interpreter.virtualmachine;

import com.interpreter.intermediatecode.CodeChunk;
import com.interpreter.intermediatecode.Context;
import com.interpreter.intermediatecode.PrimaryType;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

public class Runtime {
    private CodeChunk codeChunk;
    private DataChunk dataChunk;

    CodeChunk.Code code;
    Context context;

    final PrintStream out;
    final InputStream in;

    private final Interpreter interpreter;
    public Runtime(InputStream in, PrintStream out, Context context) {
        this.in = in;
        this.out = out;
        this.context = context;
        this.interpreter = new Interpreter(this);
    }

    public void run(CodeChunk runCodeChunk) {
        this.codeChunk = runCodeChunk;
        this.dataChunk = new DataChunk();
        int nextRunLine = 0;

        while (nextRunLine < runCodeChunk.getSize()) {
            code = runCodeChunk.getCodeByLine(nextRunLine);
            nextRunLine = interpreter.run(nextRunLine, code, dataChunk);
        }
    }
}

package com.interpreter.intermediatecode;

import com.interpreter.analysis.AST;

public class IntermediateCodeCreator {
    public CodeChunk create(AST tree) {
        Context context = new Context();

        CodeCreator.instance.handleRoot(tree.getRoot(), context);

        return context.chunk;
    }

}

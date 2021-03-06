package com.interpreter.intermediatecode;

import com.interpreter.analysis.AST;
import com.interpreter.debug.Debug;
import com.interpreter.intermediatecode.CodeChunk.*;

public class IntermediateCodeCreator {
    public CodeChunk create(AST tree) {
        Context context = new Context();
        CodeCreator.instance.handleRoot(tree.getRoot(), context);
        replacePlaceholder(context);
        return context.chunk;
    }

    private void replacePlaceholder(Context context) {
        for (Code code : context.chunk) {
            if (code.getCommand() == Command.Jmp ||
                    code.getCommand() == Command.JmpUnless) {
                code.num1 = context.positionPlaceholder.getPosition(code.num1);
            }
        }
    }

}

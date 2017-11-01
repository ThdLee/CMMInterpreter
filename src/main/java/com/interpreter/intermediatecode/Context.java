package com.interpreter.intermediatecode;

public class Context implements Cloneable {
    CodeChunk chunk;
    VariableRecorder recorder;
    LocalVariablePool variablePool;
    PositionPlaceholder positionPlaceholder;
    JumpStack jumpStack;

    Context() {
        jumpStack = new JumpStack();
        chunk = new CodeChunk();
        recorder = new VariableRecorder();
        variablePool = new LocalVariablePool();
        positionPlaceholder = new PositionPlaceholder();
    }

    private Context(Context context) {
        this.chunk = context.chunk;
        this.positionPlaceholder = context.positionPlaceholder;
        this.variablePool = context.variablePool;
        this.recorder = context.recorder.link();
        this.jumpStack = context.jumpStack;
    }


    Context link() {
        return new Context(this);
    }


}

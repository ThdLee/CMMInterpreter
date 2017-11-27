package com.interpreter.virtualmachine;

import com.interpreter.intermediatecode.CodeChunk;

public class VirtualMachine {

    private static VirtualMachine instance = new VirtualMachine();

    public static VirtualMachine getInstance() {
        return instance;
    }

    private VirtualMachine() {}
}
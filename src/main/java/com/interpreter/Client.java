package com.interpreter;

import com.interpreter.analysis.*;
import com.interpreter.debug.Debug;
import com.interpreter.intermediatecode.CodeChunk;
import com.interpreter.intermediatecode.IntermediateCodeCreator;
import com.interpreter.virtualmachine.VirtualMachine;

import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java -jar CMMInterpreter.jar <inputfile>");
            System.exit(1);
        }

        Debug.instance.activation();

        File file = new File(args[0]);
        Reader reader = new FileReader(file);
        VirtualMachine.getInstance().run(reader);
        reader.close();
    }
}
